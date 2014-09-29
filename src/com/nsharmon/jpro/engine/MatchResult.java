package com.nsharmon.jpro.engine;

import java.util.HashSet;
import java.util.Set;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;

public class MatchResult {
	private final Set<VariableSubstitution> substitutions = new HashSet<VariableSubstitution>();
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

	public Set<VariableSubstitution> getSubstitutions() {
		return substitutions;
	}

	public Set<FactStatement> getRelatedFacts() {
		return relatedFacts;
	}

	@Override
	public String toString() {
		return found ? "yes." : "no.";
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((atomExpr == null) ? 0 : atomExpr.hashCode());
			result = prime * result + ((varExpr == null) ? 0 : varExpr.hashCode());
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
			final VariableSubstitution other = (VariableSubstitution) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (atomExpr == null) {
				if (other.atomExpr != null) {
					return false;
				}
			} else if (!atomExpr.equals(other.atomExpr)) {
				return false;
			}
			if (varExpr == null) {
				if (other.varExpr != null) {
					return false;
				}
			} else if (!varExpr.equals(other.varExpr)) {
				return false;
			}
			return true;
		}

		private MatchResult getOuterType() {
			return MatchResult.this;
		}

		public Expression<?> getVariableExpression() {
			return varExpr;
		}

		public Expression<?> getSubstitutionExpression() {
			return atomExpr;
		}
	}

}
