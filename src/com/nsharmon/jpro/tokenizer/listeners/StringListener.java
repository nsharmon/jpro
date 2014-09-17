package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;



import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class StringListener implements TokenListener<PrologTokenType, String> {

	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(1);
		int firstByte = bis.read();
		if(firstByte >= 0 && firstByte == '"') {
			canConsume = true;
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, String> consume(BufferedInputStream bis) throws IOException {
		StringBuilder sb = new StringBuilder();
		int nextByte = 0;
		char previousChar;
		boolean valid = true;
		boolean foundEnd = false;
		do {
			previousChar = (char)nextByte;			
			nextByte = bis.read();
			char nextChar = (char)nextByte;
			
			if(nextByte >= 0) {
				if(sb.length() > 0 && nextChar == '"' && previousChar != '\\') {
					foundEnd = true;
				}
				sb.append(nextChar);
			} else {
				if(!foundEnd) {
					valid = false;
				}
				foundEnd = true;
			}
		} while (!foundEnd);
		
		Token<PrologTokenType, String> token;
		if(valid) {
			token = new Token<PrologTokenType, String>(PrologTokenType.STRING);
		} else {
			token = new Token<PrologTokenType, String>(PrologTokenType.UNKNOWN);
		}
		token.setTokenValue(sb.toString());
		return token;
	}
}
