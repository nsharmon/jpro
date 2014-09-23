package com.nsharmon.jpro.engine.statements;

public interface ReturningStatement<T> extends Statement {
	T getReturn();
}
