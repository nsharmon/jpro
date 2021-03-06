package com.nsharmon.jpro.parser.listeners;

import java.util.LinkedHashSet;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.RuleStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.errors.ErrorCode;
import com.nsharmon.jpro.parser.errors.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class RuleStatementListener implements StatementListener<PrologProgram, PrologTokenType, RuleStatement> {
	private final ErrorReporter reporter;

	public RuleStatementListener(final ErrorReporter reporter) {
		this.reporter = reporter;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		final FactStatementListener listener = new FactStatementListener(reporter, false);

		boolean canConsume = listener.canConsume(buffer, false) && buffer.hasNext();
		
		if(canConsume) {
			buffer.mark(1);
			
			canConsume = buffer.next().getType() == PrologTokenType.HORNOPER;

			if(canConsume) {
				int consumptions = 0;				
				boolean end = false;
				do {
					canConsume = listener.canConsume(buffer, false);
					
					if(canConsume) {
						consumptions++;
					}
					final Token<?, ?> peek = buffer.peek();
					end = !canConsume || peek == null || peek.getType() != PrologTokenType.COMMA;
					if(peek != null && (peek.getType() == PrologTokenType.COMMA || peek.getType() == PrologTokenType.CLOSE)) {
						buffer.mark(1);
						buffer.next();
						
						consumptions++;
					}
				} while (!end);
				
				buffer.consolidate(consumptions);
				buffer.reset();
			}
			buffer.reset();
		}
		buffer.reset();
		
		return canConsume;
	}

	public RuleStatement consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {		
		final FactStatementListener listener = new FactStatementListener(reporter, false);

		final FactStatement left;
		
		left = listener.consume(buffer);
		
		final Token<PrologTokenType, ?> next = buffer.next();			
		assert(next != null && next.getType() == PrologTokenType.HORNOPER);
		
		final LinkedHashSet<FactStatement> rights = new LinkedHashSet<FactStatement>();
		boolean end = false;
		boolean valid = true;
		do {
			rights.add(listener.consume(buffer));
		
			final Token<PrologTokenType, ?> close = buffer.peek();
			if(close == null || (close.getType() != PrologTokenType.CLOSE && close.getType() != PrologTokenType.COMMA)) {
				reporter.reportError(ErrorCode.NotClosed, close != null ? close : "END OF FILE");
				valid = false;
			} else {
				buffer.next();
				
				if(close.getType() == PrologTokenType.CLOSE) {
					end = true;
				}
			}
		} while (!end && valid);
		
		return new RuleStatement(left, rights);
	}
}
