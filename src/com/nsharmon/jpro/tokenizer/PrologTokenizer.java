package com.nsharmon.jpro.tokenizer;

import java.io.IOException;
import java.io.InputStream;

import com.nsharmon.jpro.tokenizer.listeners.AtomListener;
import com.nsharmon.jpro.tokenizer.listeners.CommentListener;
import com.nsharmon.jpro.tokenizer.listeners.ConstantListener;
import com.nsharmon.jpro.tokenizer.listeners.NewLineListener;
import com.nsharmon.jpro.tokenizer.listeners.NumberListener;
import com.nsharmon.jpro.tokenizer.listeners.StringListener;
import com.nsharmon.jpro.tokenizer.listeners.UnknownListener;
import com.nsharmon.jpro.tokenizer.listeners.VariableListener;
import com.nsharmon.jpro.tokenizer.listeners.WhitespaceListener;

public class PrologTokenizer extends AbstractTokenizer<PrologTokenType> {
	private int lineNumber = 1;

	public PrologTokenizer(final InputStream in) {
		super(in);

		addTokenListener(new NewLineListener());
		addTokenListener(new WhitespaceListener());
		addTokenListener(new AtomListener());
		addTokenListener(new VariableListener());
		addTokenListener(new NumberListener());
		addTokenListener(new StringListener());
		addTokenListener(new CommentListener());
		addTokenListener(new ConstantListener<PrologTokenType>("(", PrologTokenType.OPENPAREN));
		addTokenListener(new ConstantListener<PrologTokenType>(")", PrologTokenType.CLOSEPAREN));
		addTokenListener(new ConstantListener<PrologTokenType>("[", PrologTokenType.OPENBRACKET));
		addTokenListener(new ConstantListener<PrologTokenType>("]", PrologTokenType.CLOSEBRACKET));
		addTokenListener(new ConstantListener<PrologTokenType>(":-", PrologTokenType.HORNOPER));
		addTokenListener(new ConstantListener<PrologTokenType>(".", PrologTokenType.CLOSE));
		addTokenListener(new ConstantListener<PrologTokenType>(",", PrologTokenType.COMMA));
		addTokenListener(new UnknownListener<PrologTokenType>(getListeners(), PrologTokenType.UNKNOWN));
	}

	@Override
	protected Token<PrologTokenType, ?> next() throws IOException {
		Token<PrologTokenType, ?> token = super.next();

		if (token != null) {
			token.setLineNumber(lineNumber);

			if (token.getType() == PrologTokenType.NEWLINE) {
				lineNumber++;

				token = next();
			}
		}
		return token;
	}
}
