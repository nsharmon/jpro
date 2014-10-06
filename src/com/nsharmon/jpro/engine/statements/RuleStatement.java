package com.nsharmon.jpro.engine.statements;

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
	
	public RuleStatement(final FactStatement resultingFact, final Set<FactStatement> rights) {
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
		/*
		 * mortal(X) :- human(X).
		 * human(socrates).
		 * ?- mortal(socrates).
		 * yes.
		 */
		mapping.letsAssume(statementToCheck);
		if(deriveConclusionsInner(mapping, statementToCheck)) {
			mapping.qed();
		} else {
			mapping.clean();
		}
	}
	
	public boolean deriveConclusionsInner(final FactsMapping mapping, final FactStatement statementToCheck) {
		final MatchResult matchResult = statementToCheck.matches(getResultingFact());
		boolean conclusionsAdded = false;		
		if(matchResult.hasMatches()) {
			conclusionsAdded = true;
			final Match match = matchResult.getMatchByFact(statementToCheck);
			
			final List<FactStatement> predicates = condition.applySubstitutions(match);
			for (final FactStatement factStatement : predicates) {
				final MatchResult factMatch = mapping.findRelevantFacts(factStatement);
				if(!factMatch.hasMatches()) {
					final List<RuleStatement> rules = mapping.findRelevantRules(factStatement);
					if(rules.size() > 0) {
						boolean hasValidConclusion = false;
						for (final RuleStatement rule : rules) {
							mapping.letsAssume(factStatement);
							if(rule.deriveConclusionsInner(mapping, factStatement)) {
								hasValidConclusion = true;
								break;
							}
						}
						conclusionsAdded = hasValidConclusion;
					} else {
						conclusionsAdded = false;						
						break;
					}
				} 
				if(!conclusionsAdded) {
					break;
				}				
			}
		}
		return conclusionsAdded;
		
//		boolean validConclusion = false;
//		do {
//			final List<RuleStatement> rulesToVerify = mapping.findRelevantRules(factToCheck);
//			if(rulesToVerify.size() > 0) {
//				for (final RuleStatement rule : rulesToVerify) {
//					final MatchResult matchResult = factToCheck.matches(rule.getResultingFact());
//					assert(matchResult != null);
//					
//					final Match match = matchResult.getMatchByFact(factToCheck);
//					
//					final List<FactStatement> predicates = rule.getCondition().applySubstitutions(match);
//	//				for (final FactStatement factStatement : predicates) {
//	//					final MatchResult predicateMatchResult = mapping.findRelevantFacts(factStatement);
//	//					validConclusion = validConclusion && predicateMatchResult.hasMatches();
//	//					if(!validConclusion) {
//	//						break;
//	//					}
//	//				}
//					final FactStatement newFact = rule.getResultingFact().applySubstitutions(match);
//					if(validConclusion && !newFact.usesVariables()) {
//						mapping.letsAssume(newFact);
//					}							
//					statementToCheck.addAll(predicates);
//				}
//			} else {
//				final MatchResult matchResult = mapping.findRelevantFacts(factToCheck);
//				validConclusion = matchResult.hasMatches();
//			}
//		} while (validConclusion && statementToCheck.size() > 0);
//		return validConclusion;
	}	
}
