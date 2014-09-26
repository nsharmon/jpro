package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;

public class FactStatement implements Statement<PrologProgram> {
	private final Token<PrologTokenType, ?> atom;
	private final ArrayExpression expression;

	public FactStatement(final Token<PrologTokenType, ?> atom, final ArrayExpression expression) {
		this.atom = atom;
		this.expression = expression;
	}

	public FactStatement(final Token<PrologTokenType, ?> atom) {
		this(atom, null);
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

		return matches(other, true);
	}

	public boolean matches(final FactStatement other, final boolean exact) {
		if (atom == null) {
			if (other.atom != null) {
				return false;
			}
		} else if (!atom.equals(other.atom)) {
			return false;
		}
		if (expression == null) {
			if (other.expression != null) {
				return false;
			}
		} else if (other.expression != null && !expression.match(other.expression, exact)) {
			return false;
		}
		return true;
	}

	public boolean matches(final FactStatement other) {
		return matches(other, false);
	}

	public boolean usesVariables() {
		return expression != null && expression.usesVariables();
	}

	@Override
	public String toString() {
		return atom.getTokenValue() + "=" + expression;
	}

}
