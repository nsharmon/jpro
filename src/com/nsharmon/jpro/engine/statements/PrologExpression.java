package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public abstract class PrologExpression extends Expression<Token<PrologTokenType, ?>> {
	public PrologExpression() {
	}

	@Override
	public String toString() {
		return getValue().getTokenValue().toString();
	}
}
