package com.nsharmon.jpro.parser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.nsharmon.jpro.engine.Program;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.errors.ErrorReporter;
import com.nsharmon.jpro.parser.listeners.StatementListener;
import com.nsharmon.jpro.tokenizer.Token;
import com.nsharmon.jpro.tokenizer.Tokenizer;

public abstract class Parser<T extends Program, U extends Enum<U>> {
	private final ErrorReporter reporter = new ErrorReporter();
	private final Tokenizer<U> tokenizer;
	private final List<StatementListener<T, U, ?>> listeners = new ArrayList<StatementListener<T, U, ?>>();

	public Parser(final Tokenizer<U> tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<Statement<T>> parse() {
		final List<Statement<T>> statements = new ArrayList<Statement<T>>();

		final ConsumableBuffer<Token<U, ?>> buffer = new ConsumableBuffer<Token<U, ?>>(tokenizer);
		while (buffer.hasNext()) {
			Statement<T> next = null;

			for (final StatementListener<T, U, ?> listener : getListeners()) {
				if (listener.canConsume(buffer)) {
					next = listener.consume(buffer);
					break;
				}
			}
			if (next != null) {
				statements.add(next);
			} else if (buffer.hasNext()) {
				reporter.reportError(MessageFormat.format(
						"Unexpected token \"{0}\" found, and could not continue with parsing!", buffer.next()), null);
				break;
			}
		}

		return statements;
	}

	public ErrorReporter getReporter() {
		return reporter;
	}

	protected List<StatementListener<T, U, ?>> getListeners() {
		return listeners;
	}

	protected void addTokenListener(final StatementListener<T, U, ?> listener) {
		getListeners().add(listener);
	}
}
