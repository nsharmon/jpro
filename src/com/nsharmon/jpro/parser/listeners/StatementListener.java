package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.Program;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.tokenizer.Token;

public interface StatementListener<T extends Program, U extends Enum<U>, V extends Statement<T>> {
	boolean canConsume(ConsumableBuffer<Token<U, ?>> buffer);

	Statement<T> consume(ConsumableBuffer<Token<U, ?>> buffer);
}
