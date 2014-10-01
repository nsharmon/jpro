package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.PrologProgram;
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

	public void deriveConclusions(final Set<FactStatement> facts, final Set<FactStatement> statementsToCheck) {
		final MatchResult matchedConclusion = getLeft().matches(statementsToCheck);
		
		if(matchedConclusion.hasMatches()) {
			final List<FactStatement> newFacts = new ArrayList<FactStatement>();
			for (final FactStatement right : predicate) {
				final FactStatement newFact = right.applySubstitutions(matchedConclusion.getSubstitutions());
				
				final MatchResult result = newFact.matches(facts);
				if(result.hasMatches()) {
					newFacts.add(newFact);
				} else {
					newFacts.clear();
					break;
				}
			}
			
			statementsToCheck.addAll(newFacts);
		}
	}
}
