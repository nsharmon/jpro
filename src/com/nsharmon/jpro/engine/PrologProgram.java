package com.nsharmon.jpro.engine;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.nsharmon.jpro.engine.statements.ReturningStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.parser.PrologParser;

public class PrologProgram implements Program {
	private final List<Statement<PrologProgram>> statements;
	private FactsMapping factsMapping;
	private PrintStream out;
	private boolean verbose = true;

	private final List<Object> returns = new ArrayList<Object>();

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
		returns.clear();
		factsMapping = new FactsMapping();

		for (final Statement<PrologProgram> statement : statements) {
			if (verbose) {
				out.println(statement);
			}
			statement.run(this);

			if (statement instanceof ReturningStatement<?, ?>) {
				final ReturningStatement<?, ?> retStmt = (ReturningStatement<?, ?>) statement;
				returns.add(retStmt.getReturn());
				out.println(getLastReturn());
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
		return returns.size() > 0 ? returns.get(returns.size() - 1) : null;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}
}
