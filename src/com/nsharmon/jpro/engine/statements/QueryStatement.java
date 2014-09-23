package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.Program;

public class QueryStatement implements ReturningStatement<String> {
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

	public void run(final Program program) {

	}
}
