package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.Program;

public interface Statement<T extends Program> {
	void run(T program);
}
