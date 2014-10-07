package com.nsharmon.jpro.engine.statements;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.nsharmon.jpro.engine.FactsMapping;
import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class RuleStatement implements Statement<PrologProgram> {
	private final FactStatement resultingFact;
	private final ConditionStatement condition;
	
	public RuleStatement(final FactStatement resultingFact, final LinkedHashSet<FactStatement> rights) {
		this.condition = new ConditionStatement(rights);
		this.resultingFact = resultingFact;
	}
	
	public void run(final PrologProgram program) {
		program.getFactsMapping().addRule(this);
	}

	public FactStatement getResultingFact() {
		return resultingFact;
	}

	public ConditionStatement getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(resultingFact);
		sb.append(" ");
		sb.append(PrologTokenType.HORNOPER.getCode());
		sb.append(" ");
		sb.append(condition);
		sb.append(PrologTokenType.CLOSE.getCode());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resultingFact == null) ? 0 : resultingFact.hashCode());
		result = prime * result + condition.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RuleStatement other = (RuleStatement) obj;
		if (resultingFact == null) {
			if (other.resultingFact != null) {
				return false;
			}
		} else if (!resultingFact.equals(other.resultingFact)) {
			return false;
		}
		if (condition == null) {
			if (other.condition != null) {
				return false;
			} else {
				return true;
			}
		} 

		return condition.equals(other.condition);
	}

	public void deriveConclusions(final FactsMapping mapping, final FactStatement statementToCheck) {
		// Determine if rule can be applied by deduction
		mapping.letsAssume(statementToCheck);
		final ConditionSolution cs = deriveConclusionsInner(mapping, statementToCheck);
		if(cs.isSolutionPossible()) {
			// Rule matches and so all assumptions made are true.
			mapping.qed();
		} else {
			// Rule doesn't match or not all conditions are satisfied.
			mapping.clean();
		}
	}
	
	private ConditionSolution deriveConclusionsInner(final FactsMapping mapping, final FactStatement statementToCheck) {
		final ConditionSolution cs = new ConditionSolution();
		
		final MatchResult matchResult = statementToCheck.matches(getResultingFact());		
		if(!matchResult.hasMatches()) {
			cs.setSolutionPossible(false);
			return cs;
		}			

		final Match match = matchResult.getMatchByFact(statementToCheck);
		
		final List<FactStatement> predicates = condition.applySubstitutions(match);
		for (final FactStatement condition : predicates) {
			final Set<FactStatement> possibleConditions = condition.applySubstitutions(cs.getMatches());
			
			ConditionSolution solution = null;
			for (final FactStatement possibleCondition : possibleConditions) {
				solution = handleCondition(mapping, possibleCondition);
				if(solution.isSolutionPossible()) {
					break;
				}
			}
			// Each condition must be true before we can accept rule
			cs.accumulate(solution);
			if(!cs.isSolutionPossible()) {
				break;
			}			
		}
		return cs;
	}

	private ConditionSolution handleCondition(final FactsMapping mapping, final FactStatement factStatement) {
		final ConditionSolution cs = new ConditionSolution();
		final MatchResult factMatch = mapping.findRelevantFacts(factStatement);
		cs.addMatch(factMatch);
		
		if(!factMatch.hasMatches()) {
			final List<RuleStatement> rules = mapping.findRelevantRules(factStatement);
			if(rules.size() > 0) {
				boolean hasValidConclusion = false;
				for (final RuleStatement rule : rules) {
					mapping.letsAssume(factStatement);
					final ConditionSolution nestedCs = rule.deriveConclusionsInner(mapping, factStatement);
					if(nestedCs.isSolutionPossible()) {
						hasValidConclusion = true;
						cs.accumulate(nestedCs);
					}
				}
				cs.setSolutionPossible(hasValidConclusion);
			} else {
				cs.setSolutionPossible(false);					
			}
		}

		return cs;
	}	
	
	public class ConditionSolution {
		private boolean first = true;
		private boolean solutionPossible = true;
		private Set<Match> matches = new HashSet<Match>();

		public boolean isSolutionPossible() {
			return solutionPossible;
		}
		
		public void addMatch(final MatchResult factMatch) {
			matches.addAll(factMatch.getMatches().values());
		}

		public void accumulate(final ConditionSolution handleCondition) {
			if(first) {
				matches = handleCondition.matches;
			} else {
				matches.retainAll(handleCondition.matches);
			}
			solutionPossible = solutionPossible && handleCondition.solutionPossible && 
					(first || matches.size() > 0 || handleCondition.matches.size() == 0);
			first = false;
		}

		protected void setSolutionPossible(final boolean val) {
			this.solutionPossible = val;
		}

		protected void addMatch(final Match match) {
			matches.add(match);
		}
		
		public Set<Match> getMatches() {
			return matches;
		}
	}
}
