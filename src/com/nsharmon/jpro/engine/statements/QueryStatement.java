package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.PrologProgram;

public class QueryStatement implements ReturningStatement<PrologProgram, String> {
	private final FactStatement factStatement;

	public QueryStatement(final FactStatement factStatement) {
		this.factStatement = factStatement;
	}

	public FactStatement getFactStatement() {
		return factStatement;
	}

	public String getReturn() {
		return "yes.";
	}

	public void run(final PrologProgram program) {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((factStatement == null) ? 0 : factStatement.hashCode());
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
