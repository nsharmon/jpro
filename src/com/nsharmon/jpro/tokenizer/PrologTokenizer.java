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
	public PrologTokenizer(final InputStream in) {
		super(in);

		addTokenListener(new NewLineListener());
		addTokenListener(new WhitespaceListener());
		addTokenListener(new AtomListener());
		addTokenListener(new VariableListener());
		addTokenListener(new NumberListener());
		addTokenListener(new StringListener());
		addTokenListener(new CommentListener());
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.OPENPAREN.getCode(),
				PrologTokenType.OPENPAREN));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.CLOSEPAREN.getCode(),
				PrologTokenType.CLOSEPAREN));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.OPENBRACKET.getCode(),
				PrologTokenType.OPENBRACKET));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.CLOSEBRACKET.getCode(),
				PrologTokenType.CLOSEBRACKET));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.HORNOPER.getCode(),
				PrologTokenType.HORNOPER));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.QUERY.getCode(), PrologTokenType.QUERY));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.CLOSE.getCode(), PrologTokenType.CLOSE));
		addTokenListener(new ConstantListener<PrologTokenType>(PrologTokenType.COMMA.getCode(), PrologTokenType.COMMA));
		addTokenListener(new UnknownListener<PrologTokenType>(getListeners(), PrologTokenType.UNKNOWN));
	}

	@Override
	protected Token<PrologTokenType, ?> next() throws IOException {
		final Token<PrologTokenType, ?> token = super.next();

		if (token != null) {
			token.setLineNumber(getLineNumber());
		}
		return token;
	}
}
