package com.nsharmon.jpro.engine;

import java.io.InputStream;
import java.util.List;

import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.PrologParser;

public class PrologProgram implements Program {
	private final List<Statement> statements;
	private final FactsMapping factsMapping = new FactsMapping();

	public PrologProgram(final List<Statement> statements) {
		this.statements = statements;
	}

	public PrologProgram(final PrologParser tokenizer) {
		this(tokenizer.parse());
	}

	public PrologProgram(final InputStream in) {
		this(new PrologParser(in));
	}

	public void run() {
		for (final Statement statement : statements) {
			statement.run(this);
		}
	}

	public FactsMapping getFactsMapping() {
		return factsMapping;
	}
}
