package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.List;

public class ArrayExpression extends Expression<List<Expression<?>>> {
	private boolean calculatedIsVariable = false;
	private boolean isVariable = false;

	private final List<Expression<?>> list = new ArrayList<Expression<?>>();

	public ArrayExpression() {
		setValue(list);
	}

	public void addExpression(final Expression<?> expression) {
		calculatedIsVariable = false;
		list.add(expression);
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (final Expression<?> expr : list) {
			result = prime * result + expr.hashCode();
		}
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
		final ArrayExpression other = (ArrayExpression) obj;
		if (this.getCount() != other.getCount()) {
			return false;
		}

		return match(other, true);
	}

	public boolean match(final ArrayExpression other, final boolean exact) {
		if (other == null) {
			return false;
		}

		boolean matches = true;
		for (int i = 0; i < getCount(); i++) {
			final Expression<?> mine = list.get(i);
			final Expression<?> theirs = other.list.get(i);
			if (mine instanceof ArrayExpression && theirs instanceof ArrayExpression) {
				matches = ((ArrayExpression) mine).match((ArrayExpression) theirs, exact);
			} else if (exact || (!mine.usesVariables() && !theirs.usesVariables())) {
				matches = mine.equals(theirs);
			}

			if (!matches) {
				break;
			}
		}
		return matches;
	}

	@Override
	public boolean usesVariables() {
		if (!calculatedIsVariable) {
			isVariable = false;
			for (int i = 0; i < getCount(); i++) {
				if (list.get(i).usesVariables()) {
					isVariable = true;
					break;
				}
			}
		}
		return isVariable;
	}

	@Override
	public String toString() {
		final boolean first = true;
		final StringBuilder sb = new StringBuilder("[");
		for (final Expression<?> expr : list) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(expr);
		}
		sb.append("]");
		return sb.toString();
	}
}
