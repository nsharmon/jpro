package com.nsharmon.jpro.parser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.listeners.StatementListener;
import com.nsharmon.jpro.tokenizer.Token;
import com.nsharmon.jpro.tokenizer.Tokenizer;

public abstract class Parser<T extends Enum<T>> {
	private final ErrorReporter reporter = new ErrorReporter();
	private final Tokenizer<T> tokenizer;
	private final List<StatementListener<T, ?>> listeners = new ArrayList<StatementListener<T, ?>>();

	public Parser(final Tokenizer<T> tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<Statement> parse() {
		final List<Statement> statements = new ArrayList<Statement>();

		final ConsumableBuffer<Token<T, ?>> buffer = new ConsumableBuffer<Token<T, ?>>(tokenizer);
		while (buffer.hasNext()) {
			Statement next = null;

			for (final StatementListener<T, ?> listener : getListeners()) {
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

	protected List<StatementListener<T, ?>> getListeners() {
		return listeners;
	}

	protected void addTokenListener(final StatementListener<T, ?> listener) {
		getListeners().add(listener);
	}
}
