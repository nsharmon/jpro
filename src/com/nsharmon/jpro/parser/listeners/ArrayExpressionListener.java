package com.nsharmon.jpro.parser.listeners;

import java.text.MessageFormat;

import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class ArrayExpressionListener implements StatementListener<PrologTokenType, ArrayExpression> {
	private final ErrorReporter reporter;

	public ArrayExpressionListener(final ErrorReporter reporter) {
		this.reporter = reporter;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer, final boolean reset) {

		buffer.mark(1);

		final Token<PrologTokenType, ?> first = buffer.next();

		boolean canConsume = false;
		if (first.getType() == getOpenToken()) {
			canConsume = true;

			final ExpressionListener el = new ExpressionListener(reporter);

			int itemsInList = 0;
			boolean valid = true;
			boolean end = false;
			do {
				valid = el.canConsume(buffer, false);

				if (valid) {
					itemsInList++;

					buffer.mark(1);
					final Token<PrologTokenType, ?> next = buffer.next();
					if (next.getType() == getCloseToken()) {
						end = true;
					} else if (next.getType() != PrologTokenType.COMMA) {
						reporter.reportError(MessageFormat.format(
								"Unexpected token \"{0}\" found in list!  Expected {1} or {2} instead.", next,
								PrologTokenType.COMMA.getCode(), getCloseToken().getCode()), null);
						valid = false;
					}
				}
			} while (valid && !end);

			buffer.consolidate(itemsInList*2 + (valid ? 1 : 0));
		}
		if(reset) {
			buffer.reset();
			buffer.reset();
		}
		return canConsume;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		return canConsume(buffer, true);
	}
	
	public ArrayExpression consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		final Token<PrologTokenType, ?> first = buffer.next();

		final ArrayExpression ae = new ArrayExpression();

		assert (first.getType() == getOpenToken());

		final ExpressionListener el = new ExpressionListener(reporter);

		boolean valid = true;
		boolean end = false;
		do {
			final Token<PrologTokenType, ?> next = buffer.peek();
			end = next.getType() == getCloseToken();

			if (!end) {
				ae.addExpression(el.consume(buffer));

				final Token<PrologTokenType, ?> comma = buffer.peek();

				if (comma != null) {
					if (comma.getType() == PrologTokenType.COMMA) {
						buffer.next();
					} else if (comma.getType() != getCloseToken()) {
						valid = false;
						reporter.reportError(MessageFormat.format("Unexpected token \"{0}\" found", comma), null);
					}
				} else {
					end = true;
				}
			} else {
				// Consume array close
				buffer.next();
			}
		} while (valid && !end);

		return ae;
	}

	protected PrologTokenType getOpenToken() {
		return PrologTokenType.OPENBRACKET;
	}

	protected PrologTokenType getCloseToken() {
		return PrologTokenType.CLOSEBRACKET;
	}
}
