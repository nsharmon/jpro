package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.Statement;
import com.nsharmon.jpro.tokenizer.Token;

public interface StatementListener<T extends Enum<T>> {
	boolean canConsume(ConsumableBuffer<Token<T, ?>> buffer);

	Statement consume(ConsumableBuffer<Token<T, ?>> buffer);
}
