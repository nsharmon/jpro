package com.nsharmon.jpro.tokenizer;

public class Token<T extends Enum<T>, U> {
	private U tokenValue;
	private final T type;
	private int lineNumber = -1;

	public Token(final T type, final U value) {
		if (type == null) {
			throw new IllegalArgumentException("Expected type to be a non-null value.");
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokenValue == null) ? 0 : tokenValue.hashCode());
		result = prime * result + type.hashCode();
		return result;
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
		final Token other = (Token) obj;
		if (tokenValue == null) {
			if (other.tokenValue != null) {
				return false;
			}
		} else if (!tokenValue.equals(other.tokenValue)) {
			return false;
		}

		if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
}
