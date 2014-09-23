package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.Program;

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

	public ArrayExpression getArgumentsExpression() {
		return expression;
	}

	public void run(final Program program) {

	}
}
