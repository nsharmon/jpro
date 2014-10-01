package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.List;

import com.nsharmon.jpro.engine.Program;

public class CompositeStatement<T extends Program> implements Statement<T> {
	private final List<Statement<T>> statements = new ArrayList<Statement<T>>();
	
	public void run(final T program) {
		for(final Statement<T> stmt : statements) {
			stmt.run(program);
		}
	}

	public void addStatement(final Statement<T> stmt) {
		statements.add(stmt);
	}

	public int size() {
		return statements.size();
	}
	
	public Statement<T> get(final int index) {
		return statements.get(index);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Statement<T> statement : statements) {
			sb.append(statement.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
}
