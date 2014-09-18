package com.nsharmon.jpro.engine.statements;

public class FactStatement implements Statement {
	private final String atom;
	private final ArrayExpression expression;

	public FactStatement(final String atom, final ArrayExpression expression) {
		this.atom = atom;
		this.expression = expression;
	}

	public String getAtom() {
		return atom;
	}

	public ArrayExpression getExpression() {
		return expression;
	}
}
