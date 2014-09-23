package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class NewLineListener implements TokenListener<PrologTokenType, Object> {

	public boolean isCapturingNewline() {
		return true;
	}

	public boolean canConsume(final BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(1);
		final int firstByte = bis.read();
		if (firstByte >= 0 && (firstByte == '\r' || firstByte == '\n')) {
			canConsume = true;
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, Object> consume(final BufferedInputStream bis) throws IOException {
		int nextByte = bis.read();

		if (nextByte == '\r') {
			nextByte = bis.read();
		}
		assert (nextByte == '\n');

		return new Token<PrologTokenType, Object>(PrologTokenType.NEWLINE);
	}

}
