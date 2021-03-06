package com.nsharmon.jpro.parser;

import java.io.InputStream;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.parser.listeners.FactStatementListener;
import com.nsharmon.jpro.parser.listeners.QueryStatementListener;
import com.nsharmon.jpro.parser.listeners.RuleStatementListener;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.PrologTokenizer;
import com.nsharmon.jpro.tokenizer.Tokenizer;

public class PrologParser extends Parser<PrologProgram, PrologTokenType> {
	protected PrologParser(final Tokenizer<PrologTokenType> tokenizer) {
		super(tokenizer);

		addTokenListener(new RuleStatementListener(getReporter()));
		addTokenListener(new FactStatementListener(getReporter()));
		addTokenListener(new QueryStatementListener(getReporter()));
	}

	@SuppressWarnings("unchecked")
	public PrologParser(final PrologParser tokenizer) {
		this((Tokenizer<PrologTokenType>) tokenizer);
	}

	public PrologParser(final InputStream in) {
		this(new PrologTokenizer(in));
	}
}
