package com.nsharmon.jpro.engine;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import com.nsharmon.jpro.engine.statements.ReturningStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.PrologParser;

public class PrologProgram implements Program {
	private final List<Statement<PrologProgram>> statements;
	private FactsMapping factsMapping = new FactsMapping();
	private PrintStream out;
	private Object lastReturn = null;

	protected PrologProgram(final List<Statement<PrologProgram>> statements, final PrintStream out) {
		this.statements = statements;
		this.out = out != null ? out : System.out;
	}

	public PrologProgram(final PrologParser tokenizer) {
		this(tokenizer.parse(), null);
	}

	public PrologProgram(final InputStream in) {
		this(new PrologParser(in));
	}

	public void run() {
		for (final Statement<PrologProgram> statement : statements) {
			statement.run(this);

			if (statement instanceof ReturningStatement<?, ?>) {
				final ReturningStatement<?, ?> retStmt = (ReturningStatement<?, ?>) statement;
				lastReturn = retStmt.getReturn();
				out.println(lastReturn);
			}
		}
	}

	public FactsMapping getFactsMapping() {
		return factsMapping;
	}

	public PrintStream getOutput() {
		return out;
	}

	public void setOutput(final PrintStream out) {
		this.out = out;
	}

	public Object getLastReturn() {
		return lastReturn;
	}

	public void reset() {
		lastReturn = null;
		factsMapping = new FactsMapping();
	}
}
