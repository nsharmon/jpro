package com.nsharmon.jpro.tokenizer;

public class Token<T extends Enum<T>, U> {
	private U tokenValue;
	private T type;
	
	public Token(T type) {
		this.type = type;
	}

	public U getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(U tokenValue) {
		this.tokenValue = tokenValue;
	}

	public T getType() {
		return type;
	}

	@Override
	public String toString() {
		if(tokenValue != null) {
			return tokenValue + "[" + type.toString() + "]";
		} else {
			return type.toString();
		}
	}
}
