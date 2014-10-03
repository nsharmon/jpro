package com.nsharmon.jpro.engine.statements;

import java.text.MessageFormat;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class NumberExpression extends PrologExpression {
	final Token<PrologTokenType, Number> variable;

	public NumberExpression(final Token<PrologTokenType, Number> variable) {
		if (variable.getType() != PrologTokenType.NUMBER) {
			throw new IllegalStateException(MessageFormat.format("Expected type {0} got \\\"{1}\\\" instead.",
					PrologTokenType.NUMBER, variable));
		}
		this.variable = variable;
		setValue(variable);

	}

	@Override
	protected Expression<?> clone() throws CloneNotSupportedException {
		return new NumberExpression(variable);
	}

	@Override
	public int hashCode() {
		final Double val = getValue().getTokenValue().doubleValue();
		return val.hashCode();
	}

	@Override
	public Token<PrologTokenType, Number> getValue() {
		return variable;
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
		@SuppressWarnings("unchecked")
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

	@Override
	public boolean usesVariables() {
		return false;
	}
}
