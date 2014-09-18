package com.nsharmon.jpro.engine.statements;

public class VariableExpression extends Expression<String> {
	public VariableExpression(final String variable) {
		setValue(variable);
	}
}
