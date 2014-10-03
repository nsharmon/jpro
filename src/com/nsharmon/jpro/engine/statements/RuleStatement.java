package com.nsharmon.jpro.engine.statements;

import java.util.Set;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.util.SolutionComposite;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class RuleStatement implements Statement<PrologProgram> {
	private final FactStatement left;
	private final Set<FactStatement> predicate;
	
	public RuleStatement(final FactStatement left, final Set<FactStatement> rights) {
		this.left = left;
		this.predicate = rights;
	}
	
	public void run(final PrologProgram program) {
		program.getFactsMapping().addConclusion(this);
	}

	public FactStatement getLeft() {
		return left;
	}

	public Set<FactStatement> getRights() {
		return predicate;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(left);
		sb.append(" ");
		sb.append(PrologTokenType.HORNOPER.getCode());
		sb.append(" ");
		boolean first = true;
		for(final FactStatement right : predicate) {
			if(!first) {
				sb.append(",\n\t");
			} else if (predicate.size() > 1) {
				sb.append("\n\t");
			}
			sb.append(right);
			first = false;
		}
		sb.append(PrologTokenType.CLOSE.getCode());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		for(final FactStatement right : predicate) {
			result = prime * result + ((right == null) ? 0 : right.hashCode());
		}
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
		if (left == null) {
			if (other.left != null) {
				return false;
			}
		} else if (!left.equals(other.left)) {
			return false;
		}
		if (predicate == null) {
			if (other.predicate != null) {
				return false;
			} else {
				return true;
			}
		} 

		return predicate.equals(other.predicate);
	}

	public void deriveConclusions(final Set<FactStatement> facts, final FactStatement statementToCheck) {
		final MatchResult matchedConclusion = left.matches(statementToCheck);
		
		if(matchedConclusion.hasMatches()) {
			final Match leftMatch = matchedConclusion.getMatchByFact(statementToCheck);
			
			boolean meetsPredicateConditions = true;
			boolean first = true;
			final SolutionComposite possibleSolution = new SolutionComposite(leftMatch);
			for (final FactStatement right : predicate) {
				if(right.usesVariables()) {	
					final FactStatement newFact = right.applySubstitutions(leftMatch);
					
					final MatchResult result = newFact.matches(facts);
					if(!result.hasMatches()) {
						meetsPredicateConditions = false;
						break;
					} else {
						possibleSolution.mergeMatchResult(result);
						
						if(possibleSolution.noSolutionPossible()) {
							meetsPredicateConditions = false;
							break;
						}							
					}				
					first = false;					
				}
			}
			
			if(meetsPredicateConditions) {
				final MatchResult matchResult = possibleSolution.getMatchResult();
				final FactStatement leftStatement = leftMatch.getRelevantStatement();
				final Match matchByFact = matchResult.getMatchByFact(leftStatement);
				final FactStatement newFact = leftStatement.applySubstitutions(matchByFact);
				//statementsToCheck.add(newFact);
				if(!newFact.usesVariables()) {
					facts.add(newFact);
				}
			}
		}
	}
}
