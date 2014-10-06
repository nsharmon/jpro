package com.nsharmon.jpro.engine.statements;

import java.util.Map.Entry;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class QueryStatement implements ReturningStatement<PrologProgram, MatchResult> {
	private final FactStatement factStatement;
	private MatchResult returnVal;

	public QueryStatement(final FactStatement factStatement) {
		this.factStatement = factStatement;
	}

	public FactStatement getFactStatement() {
		return factStatement;
	}

	public MatchResult getReturn() {
		return returnVal;
	}

	public void run(final PrologProgram program) {
		final MatchResult matchResult = program.getFactsMapping().match(this);

		if (factStatement.usesVariables()) {
			for (final Match match : matchResult.getMatches().values()) {
				for (final Entry<Expression<?>, Expression<?>> substitution : match.getSubstitutions().entrySet()) {
					program.getOutput().println(substitution.getKey() + "=" + substitution.getValue());
				}
			}
		}
		returnVal = matchResult;
	}

	@Override
	public int hashCode() {
		return factStatement.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final QueryStatement other = (QueryStatement) obj;
		if (factStatement == null) {
			if (other.factStatement != null) {
				return false;
			}
		} else if (!factStatement.equals(other.factStatement)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return PrologTokenType.QUERY.getCode() + " " + factStatement;
	}
}
