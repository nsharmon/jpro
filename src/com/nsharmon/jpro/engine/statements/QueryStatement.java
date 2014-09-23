package com.nsharmon.jpro.engine.statements;

public class QueryStatement implements Statement {
	private final String atom;
	private final ArrayExpression expression;

	public QueryStatement(final String atom, final ArrayExpression expression) {
		this.atom = atom;
		this.expression = expression;
	}

	public String getAtom() {
		return atom;
	}

	public ArrayExpression getArgumentsExpression() {
		return expression;
	}
}
