package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.tokenizer.Token;

public interface StatementListener<T extends Enum<T>, U extends Statement> {
	boolean canConsume(ConsumableBuffer<Token<T, ?>> buffer);

	U consume(ConsumableBuffer<Token<T, ?>> buffer);
}
