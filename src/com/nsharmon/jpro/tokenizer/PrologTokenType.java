package com.nsharmon.jpro.tokenizer;

public enum PrologTokenType {
	UNKNOWN, ATOM, VARIABLE, STRING, NUMBER, OPENPAREN("("), CLOSEPAREN(")"), OPENBRACKET("["), CLOSEBRACKET("]"), HORNOPER(
			":-"), CLOSE("."), COMMA(","), WHITESPACE, COMMENT, NEWLINE, QUERY("?-");

	private String code;

	PrologTokenType(final String code) {
		this.code = code;
	}

	PrologTokenType() {
		this(null);
	}

	public String getCode() {
		return code;
	}
}
