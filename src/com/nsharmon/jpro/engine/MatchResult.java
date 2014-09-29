package com.nsharmon.jpro.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;

public class MatchResult {
	private final List<VariableSubstitution> substitutions = new ArrayList<VariableSubstitution>();
	private final Set<FactStatement> relatedFacts = new HashSet<FactStatement>();
	private boolean found;

	public MatchResult(final boolean found) {
		this.found = found;
	}

	public void addVariableSubstitution(final Expression<?> varExpr, final Expression<?> atomExpr) {
		substitutions.add(new VariableSubstitution(varExpr, atomExpr));
	}

	public void addFactStatement(final FactStatement stmt) {
		relatedFacts.add(stmt);
	}

	public boolean hasMatches() {
		return found;
	}

	public void setMatches(final boolean found) {
		this.found = found;
	}

	public List<VariableSubstitution> getSubstitutions() {
		return substitutions;
	}

	public Set<FactStatement> getRelatedFacts() {
		return relatedFacts;
	}

	public void accumulate(final MatchResult matches) {
		found = found || matches.found;
		substitutions.addAll(matches.substitutions);
		relatedFacts.addAll(relatedFacts);
	}

	public void clear() {
		substitutions.clear();
		relatedFacts.clear();
	}

	public class VariableSubstitution {
		private final Expression<?> varExpr;
		private final Expression<?> atomExpr;

		public VariableSubstitution(final Expression<?> varExpr, final Expression<?> atomExpr) {
			this.varExpr = varExpr;
			this.atomExpr = atomExpr;
		}

		@Override
		public String toString() {
			return varExpr + "=" + atomExpr;
		}
	}

}
