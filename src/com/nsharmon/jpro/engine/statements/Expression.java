package com.nsharmon.jpro.engine.statements;

public abstract class Expression<T> implements Cloneable {
	private T value = null;
	private FactStatement statement = null;

	public Expression() {
	}

	protected void setValue(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setStatement(final FactStatement statement) {
		this.statement = statement;
	}
	
	public FactStatement getStatement() {
		return statement;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public abstract boolean usesVariables();

	@Override
	protected Expression<?> clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
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
		final Expression<?> other = (Expression<?>) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
