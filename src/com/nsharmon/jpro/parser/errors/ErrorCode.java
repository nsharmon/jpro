package com.nsharmon.jpro.parser.errors;

public enum ErrorCode {
	NotClosed("Expected . but found \"{0}\" instead"), 
	UnexpectedToken("Unable to continue parsing after point \"{0}\"");
	
	private final String errorMsg;
	
	private ErrorCode(final String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getMessage() {
		return errorMsg;
	}
}
