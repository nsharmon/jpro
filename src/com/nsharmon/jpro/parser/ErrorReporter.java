package com.nsharmon.jpro.parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

	public List<Error> getMessages() {
		return errors;
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

		@Override
		public String toString() {
			String msg = type + ": " + message;
			if (throwable != null) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final PrintStream ps = new PrintStream(baos);

				throwable.printStackTrace(ps);

				msg += "\n" + baos.toString();
			}
			return msg;
		}
	}
}
