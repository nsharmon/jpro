package com.nsharmon.jpro.tokenizer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nsharmon.jpro.tokenizer.listeners.TokenListener;

public abstract class Tokenizer<T extends Enum<T>> implements Iterable<Token<T, ?>> {
	private boolean exhaustedInput = false;
	private boolean ignoreWhitespace = true;
	private final BufferedInputStream in;
	private final List<TokenListener<T, ?>> listeners = new ArrayList<TokenListener<T, ?>>();

	public Tokenizer(final InputStream in) {
		this.in = new BufferedInputStream(in);
	}

	protected Token<T, ?> next() throws IOException {
		Token<T, ?> next = null;
		TokenListener<T, ?> capturingListener = null;

		if (!exhaustedInput) {
			for (final TokenListener<T, ?> listener : getListeners()) {
				if (listener.canConsume(in)) {
					capturingListener = listener;
					next = listener.consume(in);
					break;
				}
			}
			if (next == null) {
				close();
			}
		}

		if (next != null && ignoreWhitespace && capturingListener.isCapturingWhitespace()) {
			next = next();
		}

		return next;
	}

	public boolean getIgnoreWhitespace() {
		return ignoreWhitespace;
	}

	public void setIgnoreWhitespace(final boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}

	public void close() throws IOException {
		if (!exhaustedInput) {
			in.close();
			exhaustedInput = true;
		}
	}

	protected List<TokenListener<T, ?>> getListeners() {
		return listeners;
	}

	protected void addTokenListener(final TokenListener<T, ?> listener) {
		getListeners().add(listener);
	}

	@Override
	public Iterator<Token<T, ?>> iterator() {
		return new TokenIterator(this);
	}

	public class TokenIterator implements Iterator<Token<T, ?>> {
		private final Tokenizer<T> tokenizer;
		private Token<T, ?> next = null;
		private boolean pulledFirst = false;

		public TokenIterator(final Tokenizer<T> tokenizer) {
			this.tokenizer = tokenizer;
		}

		public boolean hasNext() {
			if (!pulledFirst && next == null) {
				next = getNext();
				pulledFirst = true;
			}
			return next != null;
		}

		public Token<T, ?> next() {
			if (!pulledFirst && next == null) {
				next = getNext();
				pulledFirst = true;
			}

			final Token<T, ?> current = next;
			next = getNext();
			return current;
		}

		private Token<T, ?> getNext() {
			try {
				return tokenizer.next();
			} catch (final IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}
