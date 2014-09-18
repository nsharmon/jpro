package com.nsharmon.jpro.parser.listeners;

import com.nsharmon.jpro.parser.ErrorReporter;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class ArgumentsExpressionListener extends ArrayExpressionListener {
	public ArgumentsExpressionListener(final ErrorReporter reporter) {
		super(reporter);
	}

	protected PrologTokenType getOpenToken() {
		return PrologTokenType.OPENPAREN;
	}

	protected PrologTokenType getCloseToken() {
		return PrologTokenType.CLOSEPAREN;
	}
}
