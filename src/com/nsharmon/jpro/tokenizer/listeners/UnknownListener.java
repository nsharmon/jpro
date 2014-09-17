package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

import com.nsharmon.jpro.tokenizer.Token;

public class UnknownListener<T extends Enum<T>> implements TokenListener<T, String> {
	private List<TokenListener<T, ?>> listeners;
	private T unknownType;
	
	public UnknownListener(List<TokenListener<T, ?>> listeners, T unknownType) {
		this.listeners = listeners;
		this.unknownType = unknownType;
	}
	
	public boolean isCapturingWhitespace() {
		return false;
	}
	
	public boolean canConsume(BufferedInputStream bis) throws IOException {
		bis.mark(1);
		int firstByte = bis.read();
		bis.reset();
		
		return firstByte > 0;
	}

	public Token<T, String> consume(BufferedInputStream bis) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean validChar;
		int nextByte;
		do {
			nextByte = bis.read();
			char nextChar = (char)nextByte;
			
			validChar = true;			
			if(nextByte > 0) {		
				for(TokenListener<T, ?> listener : listeners) {
					if(listener != this && listener.canConsume(bis)) {
						validChar = false;
						break;
					}
				}

				sb.append(nextChar);
			}
		} while (nextByte > 0 && validChar);
		
		Token<T, String> token = new Token<T, String>(unknownType);
		token.setTokenValue(sb.toString());
		return token;
	}
	
}
