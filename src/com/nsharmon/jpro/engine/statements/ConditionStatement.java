package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.PrologProgram;

public class ConditionStatement implements ReturningStatement<PrologProgram, Boolean> {
	private final Set<FactStatement> factStatements;
	private final boolean conditionMet = true;
	
	public ConditionStatement(final Set<FactStatement> rights) {
		factStatements = rights;
	}
	
	public ConditionStatement() {
		this(new HashSet<FactStatement>());
	}

	@Override
	public void run(final PrologProgram program) {
		
	}

	@Override
	public Boolean getReturn() {
		return conditionMet;
	}
	
	public void addFact(final FactStatement fact) {
		factStatements.add(fact);
	}


	public Set<FactStatement> getFacts() {
		return factStatements;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(final FactStatement fact : factStatements) {
			if(!first) {
				sb.append(",\n\t");
			} else if (factStatements.size() > 1) {
				sb.append("\n\t");
			}
			sb.append(fact);
			first = false;
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(final FactStatement fact : factStatements) {
			result = prime * result + ((fact == null) ? 0 : fact.hashCode());
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
		final ConditionStatement other = (ConditionStatement) obj;
		if (factStatements == null) {
			if (other.factStatements != null) {
				return false;
			}
		} else if (!factStatements.equals(other.factStatements)) {
			return false;
		}
		return true;
	}

	public List<FactStatement> applySubstitutions(final Match match) {
		final List<FactStatement> facts = new ArrayList<FactStatement>();
		for (final FactStatement fact : factStatements) {
			facts.add(fact.applySubstitutions(match));
		}
		return facts;
	}
}
