package com.nsharmon.jpro.tokenizer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;
import com.nsharmon.jpro.tokenizer.Tokenizer;

public class ListTokenizer implements Tokenizer<PrologTokenType> {
	private final List<Token<PrologTokenType, ?>> items = new ArrayList<Token<PrologTokenType, ?>>();

	public Iterator<Token<PrologTokenType, ?>> iterator() {
		return items.iterator();
	}

	public void addToken(final Token<PrologTokenType, ?> token) {
		items.add(token);
	}
}