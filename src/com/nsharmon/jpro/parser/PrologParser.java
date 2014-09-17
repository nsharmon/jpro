package com.nsharmon.jpro.parser;

import java.io.InputStream;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.PrologTokenizer;

public class PrologParser extends Parser<PrologTokenType> {

	public PrologParser(final PrologTokenizer tokenizer) {
		super(tokenizer);
	}

	public PrologParser(final InputStream in) {
		this(new PrologTokenizer(in));
	}
}
