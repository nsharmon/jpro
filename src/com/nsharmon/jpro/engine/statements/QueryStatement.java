package com.nsharmon.jpro.engine.statements;

import java.util.List;

import com.nsharmon.jpro.engine.PrologProgram;

public class QueryStatement implements ReturningStatement<PrologProgram, String> {
	private final FactStatement factStatement;
	private String returnVal = "invalid.";

	public QueryStatement(final FactStatement factStatement) {
		this.factStatement = factStatement;
	}

	public FactStatement getFactStatement() {
		return factStatement;
	}

	public String getReturn() {
		return returnVal;
	}

	public void run(final PrologProgram program) {
		final List<FactStatement> trueStatements = program.getFactsMapping().match(factStatement);
		if (factStatement.usesVariables()) {
			for (final FactStatement fact : trueStatements) {
				program.getOutput().println(fact);
			}
		}
		if (trueStatements.size() > 0) {
			returnVal = "yes.";
		} else {
			returnVal = "no.";
		}
	}

	@Override
	public int hashCode() {
		return factStatement.hashCode();
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
		final QueryStatement other = (QueryStatement) obj;
		if (factStatement == null) {
			if (other.factStatement != null) {
				return false;
			}
		} else if (!factStatement.equals(other.factStatement)) {
			return false;
		}
		return true;
	}
}
