package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.Token;

public class ConstantListener<T extends Enum<T>> implements TokenListener<T, String> {
	private String matchingString;
	private T type;
	private boolean isCapturingWhitespace;
	
	public ConstantListener(String matchingString, T type, boolean isCapturingWhitespace) {
		if(matchingString.length() == 0) {
			throw new IllegalArgumentException("Matching string must be at least 1 character in length!");
		}
		this.matchingString = matchingString;
		this.type = type;
		this.isCapturingWhitespace = isCapturingWhitespace;
	}
	
	public ConstantListener(String matchingChar, T type) {
		this(matchingChar, type, false);
	}
	
	public boolean isCapturingWhitespace() {
		return isCapturingWhitespace;
	}
	
	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(matchingString.length());

		byte [] bString = new byte[matchingString.length()];
		if(bis.read(bString) >= 0) {
			String string = new String(bString);
			
			canConsume = (matchingString.equals(string));
		}
		bis.reset();

		return canConsume;
	}

	public Token<T, String> consume(BufferedInputStream bis) throws IOException {
		byte [] bString = new byte[matchingString.length()];
		bis.read(bString);
		
		Token<T, String> token = new Token<T, String>(type);
		token.setTokenValue(matchingString);
		return token;
	}
}
