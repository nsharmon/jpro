package com.nsharmon.jpro.tokenizer.util;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class NumberToken extends Token<PrologTokenType, Number> {
	public NumberToken(final Number value) {
		super(PrologTokenType.NUMBER, value);
	}
}