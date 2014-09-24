package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.Program;

public interface ReturningStatement<T extends Program, U> extends Statement<T> {
	U getReturn();
}
