package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;



import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class CommentListener implements TokenListener<PrologTokenType, String> {

	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(2);
		int firstByte = bis.read();
		int secondByte = bis.read();
		if(firstByte >= 0 && firstByte == '/' && secondByte == '*') {
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
				if(sb.length() > 2 && nextChar == '/' && previousChar == '*') {
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
			token = new Token<PrologTokenType, String>(PrologTokenType.COMMENT);
		} else {
			token = new Token<PrologTokenType, String>(PrologTokenType.UNKNOWN);
		}
		token.setTokenValue(sb.toString());
		return token;
	}
}
