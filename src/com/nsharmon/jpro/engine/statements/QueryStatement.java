package com.nsharmon.jpro.engine.statements;

public class QueryStatement implements Statement {
	private final FactStatement factStatement;

	public QueryStatement(final FactStatement factStatement) {
		this.factStatement = factStatement;
	}

	public FactStatement getFactStatement() {
		return factStatement;
	}
}
