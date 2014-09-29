package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.statements.AtomExpression;
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
		case ATOM:
			canConsume = true;
			break;
		case VARIABLE:
			canConsume = true;
			break;
		case OPENBRACKET:
			buffer.reset();

			final ArrayExpressionListener arrel = new ArrayExpressionListener(reporter);
			canConsume = arrel.canConsume(buffer, reset);

			reset = false;
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
		case ATOM:
			buffer.next();
			expr = new AtomExpression(first);
			break;
		case VARIABLE:
			buffer.next();
			expr = new VariableExpression(first);
			break;
		case OPENBRACKET:
			final ArrayExpressionListener arrel = new ArrayExpressionListener(reporter);
			expr = arrel.consume(buffer);
			break;
		case OPENPAREN:
			final ArrayExpressionListener argel = new ArgumentsExpressionListener(reporter);
			expr = argel.consume(buffer);
			break;
		default:
			assert (false);
			break;
		}

		return expr;
	}
}
