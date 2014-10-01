package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.parser.ConsumableBuffer;
import com.nsharmon.jpro.parser.errors.ErrorCode;
import com.nsharmon.jpro.parser.errors.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class FactStatementListener implements StatementListener<PrologProgram, PrologTokenType, FactStatement> {
	private final ErrorReporter reporter;
	private final boolean standalone;

	public FactStatementListener(final ErrorReporter reporter, final boolean standalone) {
		this.reporter = reporter;
		this.standalone = standalone;
	}

	public FactStatementListener(final ErrorReporter reporter) {
		this(reporter, true);
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer, boolean reset) {
		buffer.mark(1);

		final Token<PrologTokenType, ?> first = buffer.next();
		final Token<PrologTokenType, ?> second = buffer.peek();

		final ArgumentsExpressionListener expr = new ArgumentsExpressionListener(reporter);
		boolean canConsume = first.getType() == PrologTokenType.ATOM;
		if(canConsume && second != null && second.getType() != PrologTokenType.CLOSE) {
			canConsume = expr.canConsume(buffer, false);
			
			// While this ensures fact statement is entirely valid and closed, better to handle unclosed
			// fact statements in the context of FactStatementListener, so let it slide for now.
//			if(canConsume && buffer.peek() != null) {
//				canConsume = (!standalone || buffer.peek().getType() == PrologTokenType.CLOSE);
//			}
			
			buffer.consolidate(2);
		} else if (!canConsume) {
			reset = true;
		}

		if(reset) {
			buffer.reset();
		}

		return canConsume;
	}

	public boolean canConsume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {
		return canConsume(buffer, true);
	}
	
	public FactStatement consume(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer) {

		final Token<PrologTokenType, ?> first = buffer.next();

		FactStatement statement = null;
		final Token<PrologTokenType, ?> next = buffer.peek();
		if (next != null && next.getType() == PrologTokenType.CLOSE) {
			buffer.next();
			statement = new FactStatement(first, standalone);
		} else {
			statement = extractArgumentsExpression(buffer, first, statement);
		}
		return statement;
	}

	private FactStatement extractArgumentsExpression(final ConsumableBuffer<Token<PrologTokenType, ?>> buffer,
			final Token<PrologTokenType, ?> first, FactStatement statement) {
		final ArgumentsExpressionListener expr = new ArgumentsExpressionListener(reporter);
		if (buffer.hasNext() && expr.canConsume(buffer)) {

			statement = new FactStatement(first, expr.consume(buffer, standalone), standalone);

			final Token<PrologTokenType, ?> close = buffer.peek();
			if (close == null || (standalone && close.getType() != PrologTokenType.CLOSE)) {
				reporter.reportError(ErrorCode.NotClosed, close != null ? close : "END OF FILE");
			} else if (standalone) {				
				buffer.next();
			}
		} else {
			reporter.reportError(ErrorCode.UnexpectedToken, first);
		}
		return statement;
	}
}
