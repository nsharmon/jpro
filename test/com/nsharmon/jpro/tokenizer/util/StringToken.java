package com.nsharmon.jpro.tokenizer.util;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class StringToken extends Token<PrologTokenType, String> {
	public StringToken(final PrologTokenType type) {
		super(type);
	}

	public StringToken(final PrologTokenType type, final String value) {
		super(type, value);
	}
}