package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.tokenizer.Token;

public interface ExpressionListener<T extends Enum<T>, U extends Expression<?>> {
	boolean canConsume(ConsumableBuffer<Token<T, ?>> buffer);

	Expression<?> consume(ConsumableBuffer<Token<T, ?>> buffer);
}
