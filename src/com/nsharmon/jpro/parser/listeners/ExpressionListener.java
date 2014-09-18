package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class ExpressionListener implements StatementListener<PrologTokenType, Expression> {
	private final ErrorReporter reporter;

	public ExpressionListener(final ErrorReporter reporter) {
		this.reporter = reporter;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		buffer.mark(2);

		final Token<PrologTokenType, ?> first = buffer.next();
		final Token<PrologTokenType, ?> second = buffer.next();

		final boolean canConsume = first.getType() == PrologTokenType.ATOM
				&& second.getType() == PrologTokenType.OPENPAREN;

		buffer.reset();

		return canConsume;
	}

	public Expression consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {

		return null;
	}
}
