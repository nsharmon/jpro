package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.List;

public class ArrayExpression extends Expression<List<Expression<?>>> {
	List<Expression<?>> list = new ArrayList<Expression<?>>();

	public ArrayExpression() {
		setValue(list);
	}

	public void addExpression(final Expression<?> expression) {
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

		return true;
	}

}
