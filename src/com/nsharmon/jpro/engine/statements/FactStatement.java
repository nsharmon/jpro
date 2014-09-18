package com.nsharmon.jpro.engine.statements;

public class FactStatement implements Statement {
	private final String atom;
	private final Expression expression;

	public FactStatement(final String atom, final Expression expression) {
		this.atom = atom;
		this.expression = expression;
	}

	public String getAtom() {
		return atom;
	}

	public Expression getExpression() {
		return expression;
	}
}
