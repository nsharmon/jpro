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
}
