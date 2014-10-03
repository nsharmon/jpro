package com.nsharmon.jpro.engine.statements;

import java.text.MessageFormat;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class AtomExpression extends PrologExpression {
	public AtomExpression(final Token<PrologTokenType, ?> atom) {
		if (atom.getType() != PrologTokenType.ATOM) {
			throw new IllegalStateException(MessageFormat.format("Expected type {0}", PrologTokenType.ATOM));
		}
		setValue(atom);
	}

	@Override
	public boolean usesVariables() {
		return false;
	}

	@Override
	protected Expression<?> clone() throws CloneNotSupportedException {
		return new AtomExpression(getValue());
	}	
}
