package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.errors.ErrorCode;
import com.nsharmon.jpro.parser.errors.ErrorReporter;
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

		final QueryStatement stmt = new QueryStatement(factListener.consume(buffer));
		
		final Token<PrologTokenType, ?> close = buffer.peek();
		if(close == null || close.getType() != PrologTokenType.CLOSE) {
			reporter.reportError(ErrorCode.NotClosed, close != null ? close : "END OF FILE");
		} else if (close != null) {
			buffer.next();
		}
		
		return stmt;
	}
}
