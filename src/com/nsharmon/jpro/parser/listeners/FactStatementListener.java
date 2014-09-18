package com.nsharmon.jpro.parser.listeners;

import java.text.MessageFormat;

import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class FactStatementListener implements StatementListener<PrologTokenType, FactStatement> {
	private final ErrorReporter reporter;

	public FactStatementListener(final ErrorReporter reporter) {
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

	public FactStatement consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {

		final Token<PrologTokenType, ?> first = buffer.next();
		final String atom = (String) first.getTokenValue();

		FactStatement statement = null;
		final ExpressionListener expr = new ExpressionListener(reporter);
		if (expr.canConsume(buffer)) {

			statement = new FactStatement(atom, expr.consume(buffer));
		} else {
			reporter.reportError(MessageFormat.format("Unable to continue parsing after point \"{0}(\"", atom), null);
		}
		return statement;
	}
}
