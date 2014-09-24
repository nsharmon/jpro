package com.nsharmon.jpro.engine.statements;

import java.text.MessageFormat;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class VariableExpression extends Expression<Token<PrologTokenType, ?>> {
	public VariableExpression(final Token<PrologTokenType, ?> variable) {
		if (variable.getType() != PrologTokenType.VARIABLE) {
			throw new IllegalStateException(MessageFormat.format("Expected type {0}", PrologTokenType.VARIABLE));
		}
		setValue(variable);

	}
}
