package com.nsharmon.jpro.tokenizer;

public class Token<T extends Enum<T>, U> {
	private U tokenValue;
	private final T type;
	private int lineNumber = -1;

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

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(final int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		String stringVal;
		if (tokenValue != null) {
			stringVal = tokenValue + "[type:" + type.toString() + "]";
		} else {
			stringVal = type.toString();
		}
		if (lineNumber >= 0) {
			stringVal += "[line:" + lineNumber + "]";
		}
		return stringVal;
	}
}
