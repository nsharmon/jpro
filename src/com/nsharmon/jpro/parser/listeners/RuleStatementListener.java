package com.nsharmon.jpro.parser.listeners;

import java.text.MessageFormat;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.RuleStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.errors.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class RuleStatementListener implements StatementListener<PrologProgram, PrologTokenType, RuleStatement> {
	private final ErrorReporter reporter;
	private final boolean standalone;

	public RuleStatementListener(final ErrorReporter reporter, final boolean standalone) {
		this.reporter = reporter;
		this.standalone = standalone;
	}

	public RuleStatementListener(final ErrorReporter reporter) {
		this(reporter, true);
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		final FactStatementListener listener = new FactStatementListener(reporter, false);

		boolean canConsume = listener.canConsume(buffer, false) && buffer.hasNext();
		
		if(canConsume) {
			buffer.mark(1);
			
			canConsume = buffer.next().getType() == PrologTokenType.HORNOPER;

			if(canConsume) {
				canConsume = listener.canConsume(buffer, false);
				
				buffer.reset();
			}
			buffer.reset();
		}
		buffer.reset();
		
		return canConsume;
	}

	public RuleStatement consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {		
		final FactStatementListener listener = new FactStatementListener(reporter, false);

		final FactStatement left, right;
		left = listener.consume(buffer);
		
		final Token<PrologTokenType, ?> next = buffer.next();			
		assert(next != null && next.getType() == PrologTokenType.HORNOPER);
		
		right = listener.consume(buffer);
		
		final Token<PrologTokenType, ?> close = buffer.peek();
		if(close == null || close.getType() != PrologTokenType.CLOSE) {
			reporter.reportError(MessageFormat.format("Expected . but found \"{0}\" instead", close != null ? close
					: "END OF FILE"), null);
		} else {
			buffer.next();
		}

		return new RuleStatement(left, right);
	}
}
