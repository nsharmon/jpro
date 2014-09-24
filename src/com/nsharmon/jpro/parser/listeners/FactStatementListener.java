package com.nsharmon.jpro.parser.listeners;

import java.text.MessageFormat;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class FactStatementListener implements StatementListener<PrologProgram, PrologTokenType, FactStatement> {
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

		FactStatement statement = null;
		final ArgumentsExpressionListener expr = new ArgumentsExpressionListener(reporter);
		if (expr.canConsume(buffer)) {

			statement = new FactStatement(first, expr.consume(buffer));

			buffer.mark(1);
			final Token<PrologTokenType, ?> close = buffer.hasNext() ? buffer.next() : null;
			if (close == null || close.getType() != PrologTokenType.CLOSE) {
				reporter.reportError(MessageFormat.format("Expected . but found \"{0}\" instead", close != null ? close
						: "END OF FILE"), null);
				buffer.reset();
			} else {
				buffer.reset();
				buffer.next();
			}
		} else {
			reporter.reportError(MessageFormat.format("Unable to continue parsing after point \"{0}(\"", first), null);
		}
		return statement;
	}
}
