package com.nsharmon.jpro.engine.statements;

import java.util.ArrayList;
import java.util.List;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.tokenizer.PrologTokenType;

public class ArrayExpression extends Expression<List<Expression<?>>> {
	private boolean calculatedIsVariable = false;
	private boolean isVariable = false;
	private final PrologTokenType openToken;
	private final PrologTokenType closeToken;

	private final List<Expression<?>> list = new ArrayList<Expression<?>>();

	public ArrayExpression(final PrologTokenType openToken, final PrologTokenType closeToken) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		setValue(list);
	}

	public void addExpression(final Expression<?> expression) {
		calculatedIsVariable = false;
		list.add(expression);
		
		expression.setStatement(getStatement());
	}

	
	@Override
	protected Expression<?> clone() throws CloneNotSupportedException {
		final ArrayExpression ae = new ArrayExpression(openToken, closeToken);
		for(final Expression<?> expr : list) {
			ae.addExpression(expr.clone());
		}
		return ae;
	}

	@Override
	public void setStatement(final FactStatement statement) {
		super.setStatement(statement);
		
		for(final Expression<?> expr : list) {
			expr.setStatement(statement);
		}
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (final Expression<?> expr : list) {
			result = prime * result + expr.hashCode();
		}
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
		final ArrayExpression other = (ArrayExpression) obj;
		if (this.getCount() != other.getCount()) {
			return false;
		}

		return match(other, true).hasMatches();
	}

	public MatchResult match(final ArrayExpression other, final boolean exact) {
		if (other == null) {
			return new MatchResult(false);
		}

		final MatchResult result = new MatchResult(false);
		boolean matches = true;
		for (int i = 0; i < getCount(); i++) {
			final Expression<?> mine = list.get(i);
			final Expression<?> theirs = other.list.get(i);

			if (mine instanceof ArrayExpression && theirs instanceof ArrayExpression) {
				// Case 1:  mine and theirs are both ArrayExpressions - recursion call

				final MatchResult aeResult = ((ArrayExpression) mine).match((ArrayExpression) theirs, exact);
				matches = aeResult.hasMatches();

				// This ensures that any recursively obtained VariableSubstitutions are kept
				result.accumulate(aeResult);
			} else if (exact || (!mine.usesVariables() && !theirs.usesVariables())) {
				// Case 2:  neither are variable expressions or exact check, so check for equality
				matches = mine.equals(theirs);
			} else if (mine.usesVariables() || theirs.usesVariables()) {
				// Case 3:  one or the other is a variable expression, so it already matches.  Just note it.
				if (mine.usesVariables()) {
					result.addMatch(theirs.getStatement(), mine, theirs);
				} else {
					result.addMatch(mine.getStatement(), theirs, mine);
				}
			} else {
				// Case 4:  there should be no case 4.
				assert(false);
			}

			// If conflict occurred, remove accumulated information pertaining to VariableSubstitutions
			if (!matches) {
				result.clear();
				break;
			}
		}

		result.setMatches(matches);
		return result;
	}

	@Override
	public boolean usesVariables() {
		if (!calculatedIsVariable) {
			isVariable = false;
			for (int i = 0; i < getCount(); i++) {
				if (list.get(i).usesVariables()) {
					isVariable = true;
					break;
				}
			}
		}
		return isVariable;
	}

	public ArrayExpression applySubstitutions(final Match match) {
		final ArrayExpression ae = new ArrayExpression(openToken, closeToken);
		for(final Expression<?> expr : list) {
			Expression<?> exprToAdd;
			if(expr instanceof ArrayExpression) {
				exprToAdd = ((ArrayExpression)expr).applySubstitutions(match);
			} else if (expr.usesVariables() && match.contains(expr)) {
				exprToAdd = match.get(expr);
			} else {
				exprToAdd = expr;
			}
			try {
				ae.addExpression(exprToAdd.clone());
			} catch (final CloneNotSupportedException e) {
			}			
		}
		return ae;
	}
	
	@Override
	public String toString() {
		boolean first = true;
		final StringBuilder sb = new StringBuilder(openToken.getCode());
		for (final Expression<?> expr : list) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(expr);
			first = false;
		}
		sb.append(closeToken.getCode());
		return sb.toString();
	}
}
