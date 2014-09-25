package com.nsharmon.jpro.tokenizer.util;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class TestToken extends Token<PrologTokenType, String> {
	public TestToken(final PrologTokenType type) {
		super(type);
	}

	public TestToken(final PrologTokenType type, final String value) {
		super(type, value);
	}
}