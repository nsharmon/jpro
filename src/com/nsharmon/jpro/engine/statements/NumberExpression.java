package com.nsharmon.jpro.engine.statements;

import java.text.MessageFormat;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class NumberExpression extends Expression<Token<PrologTokenType, Number>> {
	public NumberExpression(final Token<PrologTokenType, Number> variable) {
		if (variable.getType() != PrologTokenType.NUMBER) {
			throw new IllegalStateException(MessageFormat.format("Expected type {0}", PrologTokenType.VARIABLE));
		}
		setValue(variable);

	}

	@Override
	public int hashCode() {
		final Double val = getValue().getTokenValue().doubleValue();
		return val.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Expression<Token<PrologTokenType, Number>> other = (Expression<Token<PrologTokenType, Number>>) obj;
		if (getValue() == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (getValue().getTokenValue().doubleValue() != (other.getValue().getTokenValue().doubleValue())) {
			return false;
		}
		return true;
	}
}
