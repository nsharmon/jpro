package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class VariableListener implements TokenListener<PrologTokenType, String> {
	
	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(1);
		int firstByte = bis.read();
		if(firstByte >= 0 && (Character.isUpperCase((char)firstByte))) {
			canConsume = true;
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, String> consume(BufferedInputStream bis) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean validChar;
		int nextByte;
		do {
			bis.mark(1);
			nextByte = bis.read();
			char nextChar = (char)nextByte;
			validChar = nextByte >= 0 && (Character.isUpperCase(nextChar) || 
					(sb.length() > 0 && (Character.isLowerCase(nextChar) || nextChar == '_')));
			if(validChar) {
				sb.append(nextChar);
			}
		} while (validChar);
		
		if(nextByte >= 0) {
			bis.reset();
		}
		
		Token<PrologTokenType, String> token = new Token<PrologTokenType, String>(PrologTokenType.VARIABLE);
		token.setTokenValue(sb.toString());
		return token;
	}
	
}
