package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.Token;

public interface TokenListener<T extends Enum<T>, U> {
	default boolean isCapturingWhitespace() {
		return false;
	}

	default boolean isCapturingNewline() {
		return false;
	}

	boolean canConsume(BufferedInputStream bis) throws IOException;

	Token<T, U> consume(BufferedInputStream bis) throws IOException;
}
