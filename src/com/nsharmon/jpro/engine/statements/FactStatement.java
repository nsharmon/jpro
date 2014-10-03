package com.nsharmon.jpro.engine.statements;

import java.util.Set;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class FactStatement implements Statement<PrologProgram> {
	private final Token<PrologTokenType, ?> atom;
	private final ArrayExpression expression;
	private final boolean standalone;
	
	public FactStatement(final Token<PrologTokenType, ?> atom, final ArrayExpression expression, final boolean standalone) {
		this.atom = atom;
		this.expression = expression;
		this.standalone = standalone;
		
		if(expression != null) {
			expression.setStatement(this);
		}
	}

	public FactStatement(final Token<PrologTokenType, ?> atom, final boolean standalone) {
		this(atom, null, standalone);
	}

	public Token<PrologTokenType, ?> getAtom() {
		return atom;
	}

	public ArrayExpression getArgumentsExpression() {
		return expression;
	}

	public void run(final PrologProgram program) {
		program.getFactsMapping().addFact(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atom == null) ? 0 : atom.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
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
		final FactStatement other = (FactStatement) obj;

		return matches(other, true, true).hasMatches();
	}

	private MatchResult matches(final FactStatement other, final boolean exact, final boolean fromEquals) {
		if (atom == null) {
			if (other.atom != null) {
				return new MatchResult(false);
			}
		} else if (!atom.equals(other.atom)) {
			return new MatchResult(false);
		}
		if (expression == null) {
			if (other.expression != null) {
				return new MatchResult(false);
			} else {
				return new MatchResult(true);
			}
		} else if (other.expression == null) {
			return new MatchResult(false);
		}

		final MatchResult result = expression.match(other.expression, exact);
		
		return result;
	}

	public MatchResult matches(final FactStatement other, final boolean exact) {
		return matches(other, exact, false);
	}

	public MatchResult matches(final FactStatement other) {
		return matches(other, false, false);
	}

	public MatchResult matches(final Set<FactStatement> factStatements) {
		final MatchResult result = new MatchResult(false);
		for(final FactStatement fact : factStatements) {
			result.accumulate(this.matches(fact));
		}
		return result;
	}
	
	public FactStatement applySubstitutions(final Match match) {
		FactStatement statement = this;
		if(usesVariables()) {
			statement = new FactStatement(atom, expression.applySubstitutions(match), true);
		}
		
		return statement;
	}
	
	public boolean usesVariables() {
		return expression != null && expression.usesVariables();
	}

	@Override
	public String toString() {
		return atom.getTokenValue() + (expression != null ? expression.toString() : "")
				+ (standalone ? PrologTokenType.CLOSE.getCode() : "");
	}
}
