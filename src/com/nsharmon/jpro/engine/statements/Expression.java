package com.nsharmon.jpro.engine.statements;

public abstract class Expression<T> implements Statement {
	private T value = null;

	public Expression() {
	}

	protected void setValue(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
