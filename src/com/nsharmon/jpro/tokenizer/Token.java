package com.nsharmon.jpro.tokenizer;

public class Token<T extends Enum<T>, U> {
	private U tokenValue;
	private final T type;

	public Token(final T type, final U value) {
		this.type = type;
		this.tokenValue = value;
	}

	public Token(final T type) {
		this(type, null);
	}

	public U getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(final U tokenValue) {
		this.tokenValue = tokenValue;
	}

	public T getType() {
		return type;
	}

	@Override
	public String toString() {
		if (tokenValue != null) {
			return tokenValue + "[" + type.toString() + "]";
		} else {
			return type.toString();
		}
	}
}
