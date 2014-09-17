package com.nsharmon.jpro.tokenizer.listeners;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class NumberListener implements TokenListener<PrologTokenType, Object> {
	
	public boolean canConsume(BufferedInputStream bis) throws IOException {
		boolean canConsume = false;
		bis.mark(2);
		int nextByte = bis.read();
		if(nextByte >= 0 && (Character.isDigit((char)nextByte) || nextByte == '.')) {
			if(nextByte == '.') {
				nextByte = bis.read();
				
				canConsume = Character.isDigit((char)nextByte);
			} else {
				canConsume = true;
			}
		}
		bis.reset();
		return canConsume;
	}

	public Token<PrologTokenType, Object> consume(BufferedInputStream bis) throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean validNumber = true;
		boolean validChar;
		boolean foundDecimal = false;
		int nextByte;
		do {
			bis.mark(1);
			nextByte = bis.read();
			char nextChar = (char)nextByte;
			validChar = nextByte >= 0 && (Character.isDigit((char)nextByte) || nextByte == '.');
			if(validChar) {
				sb.append(nextChar);			
			}
			if(nextChar == '.' && !foundDecimal) {
				foundDecimal = true;
			} else if (nextChar == '.') {
				validNumber = false;
			}
		} while (validChar);
		
		if(nextByte >= 0) {
			bis.reset();
		}
		
		Token<PrologTokenType, Object> token = null;
		if(validNumber) {
			token = new Token<PrologTokenType, Object>(PrologTokenType.NUMBER);
			String val = sb.toString();
			try {
				NumberFormat numberParser = NumberFormat.getNumberInstance(Locale.US);
				token.setTokenValue(numberParser.parse(val));
			} catch (ParseException e) {
				// This should not happen, but if so, be sure to throw it into UNKNOWN pile
				validNumber = false;
				e.printStackTrace();
			}
		} 
		
		if(!validNumber) {
			token = new Token<PrologTokenType, Object>(PrologTokenType.UNKNOWN);
			token.setTokenValue(sb.toString());			
		}
		return token;
	}
}
