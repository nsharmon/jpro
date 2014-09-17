package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class AtomListener implements TokenListener<PrologTokenType, String> {

	public boolean canConsume(final BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(1);
		final int firstByte = bis.read();
		if (firstByte >= 0 && (Character.isLowerCase((char) firstByte) || firstByte == '_')) {
			canConsume = true;
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, String> consume(final BufferedInputStream bis) throws IOException {
		final StringBuilder sb = new StringBuilder();
		boolean validChar;
		int nextByte;
		do {
			bis.mark(1);
			nextByte = bis.read();
			final char nextChar = (char) nextByte;
			validChar = nextByte >= 0
					&& (Character.isLowerCase(nextChar) || nextChar == '_' || (sb.length() > 0 && Character
							.isUpperCase(nextChar)));
			if (validChar) {
				sb.append(nextChar);
			}
		} while (validChar);

		if (nextByte >= 0) {
			bis.reset();
		}

		final Token<PrologTokenType, String> token = new Token<PrologTokenType, String>(PrologTokenType.ATOM);
		token.setTokenValue(sb.toString());
		return token;
	}
}
