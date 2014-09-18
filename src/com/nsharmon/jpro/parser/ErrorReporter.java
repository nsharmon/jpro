package com.nsharmon.jpro.parser;

import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {
	public enum ErrorType {
		NOTICE, WARNING, ERROR
	}

	private final List<Error> errors = new ArrayList<Error>();

	public void report(final Error error) {
		errors.add(error);
	}

	public void reportError(final String message, final Throwable throwable) {
		report(new Error(ErrorType.ERROR, message, throwable));
	}

	public void reportWarning(final String message) {
		report(new Error(ErrorType.WARNING, message, null));
	}

	public class Error {

		private final ErrorType type;
		private final String message;
		private final Throwable throwable;

		public Error(final ErrorType type, final String message, final Throwable throwable) {
			this.type = type;
			this.message = message;
			this.throwable = throwable;
		}

		public Error(final ErrorType type, final String message) {
			this(type, message, null);
		}

		public ErrorType getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}

		public Throwable getThrowable() {
			return throwable;
		}
	}
}
