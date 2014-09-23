package com.nsharmon.jpro.parser.listeners;

import java.text.MessageFormat;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.VariableExpression;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class GenericExpressionListener implements ExpressionListener<PrologTokenType, Expression<?>> {

	private final ErrorReporter reporter;

	public GenericExpressionListener(final ErrorReporter reporter) {
		this.reporter = reporter;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer, boolean reset) {
		buffer.mark(1);

		final Token<PrologTokenType, ?> first = buffer.next();

		boolean canConsume;
		switch (first.getType()) {
		case VARIABLE:
			canConsume = true;
			break;
		case OPENBRACKET:
			buffer.reset();

			final ArrayExpressionListener ael = new ArrayExpressionListener(reporter);
			canConsume = ael.canConsume(buffer, reset);

			reset = false;
			break;
		case OPENPAREN:
			canConsume = canConsume(buffer, false);

			buffer.mark(1);
			final Token<PrologTokenType, ?> end = buffer.next();
			if (end.getType() != PrologTokenType.CLOSEPAREN) {
				canConsume = false;
			}
			buffer.reset(); // Call reset for close paren
			buffer.reset(); // Call reset for marked in canConsume inner call
			break;
		default:
			canConsume = false;
			break;
		}

		if (reset) {
			buffer.reset();
		}

		return canConsume;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		return canConsume(buffer, true);
	}

	public Expression<?> consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		final Token<PrologTokenType, ?> first = buffer.peek();

		Expression<?> expr = null;
		switch (first.getType()) {
		case VARIABLE:
			buffer.next();
			expr = new VariableExpression((String) first.getTokenValue());
			break;
		case OPENBRACKET:
			final ArrayExpressionListener ael = new ArrayExpressionListener(reporter);
			expr = ael.consume(buffer);
			break;
		case OPENPAREN:
			expr = consume(buffer);

			final Token<PrologTokenType, ?> closeparen = buffer.next(); // Close
			// paren
			if (closeparen.getType() != PrologTokenType.CLOSEPAREN) {
				reporter.reportError(MessageFormat.format("Expecting ) but found \"{0}\" instead", closeparen), null);
			}
			break;
		default:
			assert (false);
			break;
		}

		return expr;
	}
}
