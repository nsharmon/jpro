package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class QueryStatementListener implements StatementListener<PrologProgram, PrologTokenType, QueryStatement> {
	private final ErrorReporter reporter;
	private final FactStatementListener factListener;

	public QueryStatementListener(final ErrorReporter reporter) {
		this.reporter = reporter;
		this.factListener = new FactStatementListener(this.reporter, false);
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		buffer.mark(1);

		final Token<PrologTokenType, ?> first = buffer.next();

		boolean canConsume = first.getType() == PrologTokenType.QUERY;

		if (canConsume) {
			canConsume = factListener.canConsume(buffer);
		}
		buffer.reset();
		return canConsume;
	}

	public QueryStatement consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {

		final Token<PrologTokenType, ?> first = buffer.next();

		assert (first.getType() == PrologTokenType.QUERY);

		return new QueryStatement(factListener.consume(buffer));
	}
}
