package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class WhitespaceListener implements TokenListener<PrologTokenType, Object> {
	
	public boolean isCapturingWhitespace() {
		return true;
	}
	
	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(1);
		int firstByte = bis.read();
		if(firstByte >= 0 && Character.isWhitespace((char)firstByte)) {
			canConsume = true;
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, Object> consume(BufferedInputStream bis) throws IOException {
		int nextByte;
		do {
			bis.mark(1);
			nextByte = bis.read();
		} while (nextByte >= 0 && Character.isWhitespace((char)nextByte));
		
		if(nextByte != 0) {
			bis.reset();
		}
		
		return new Token<PrologTokenType, Object>(PrologTokenType.WHITESPACE);
	}
	
}
