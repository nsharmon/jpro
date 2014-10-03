package com.nsharmon.jpro.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;

public class MatchResult {
	//private final Set<VariableSubstitution> substitutions = new HashSet<VariableSubstitution>();
	//private final Set<FactStatement> relatedFacts = new HashSet<FactStatement>();
	private final Map<FactStatement, Match> matches = new LinkedHashMap<FactStatement, Match>();
	private boolean found;

	public MatchResult(final boolean found) {
		this.found = found;
	}

	public void addMatch(final Match match) {
		for(final Entry<Expression<?>, Expression<?>> substitutionEntry : match.getSubstitutions().entrySet()) {
			addMatch(match.getRelevantStatement(), substitutionEntry.getKey(), substitutionEntry.getValue());
		}
	}
	
	public void addMatch(final FactStatement stmt, final Expression<?> varExpr, final Expression<?> atomExpr) {
		Match fact = getMatchByFact(stmt);
		if(fact == null) {
			fact = new Match(stmt);
			matches.put(stmt, fact);
		}
		fact.addSubstitution(varExpr, atomExpr);
	}

	public Match getMatchByFact(final FactStatement stmt) {
		return matches.get(stmt);
	}
	
	public boolean hasMatches() {
		return found;
	}

	public void setMatches(final boolean found) {
		this.found = found;
	}

	public Map<FactStatement, Match> getMatches() {
		return matches;
	}

	@Override
	public String toString() {
		return found ? "yes." : "no.";
	}

	public void accumulate(final MatchResult matches) {
		found = found || matches.found;
		for(final Match match : matches.getMatches().values()) {
			addMatch(match);
		}
	}

	public void clear() {
		matches.clear();
	}


	public Set<Expression<?>> getSolutions() {
		final Set<Expression<?>> solutions = new HashSet<Expression<?>>();
		for(final Match match : matches.values()) {
			solutions.addAll(match.getSubstitutions().values());
		}
		return solutions;
	}
	
	public class Match {
		private final Map<Expression<?>, Expression<?>> substitutions = new HashMap<Expression<?>, Expression<?>>(1);
		private final FactStatement relevantStatement;

		public Match(final FactStatement relevantStatement) {
			this.relevantStatement = relevantStatement;
		}
		
		public void addSubstitution(final Expression<?> varExpr, final Expression<?> atomExpr) {
			substitutions.put(varExpr, atomExpr);
		}

		public FactStatement getRelevantStatement() {
			return relevantStatement;
		}

		public boolean contains(final Expression<?> expr) {
			return substitutions.containsKey(expr);
		}
		
		public Map<Expression<?>, Expression<?>> getSubstitutions() {
			return substitutions;
		}

		public Expression<?> get(final Expression<?> expr) {
			return substitutions.get(expr);
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Match[");
			boolean first = true;
			for(final Entry<Expression<?>, Expression<?>> entry : substitutions.entrySet()) {
				if(!first) {
					sb.append(", ");
				}
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				
				first = false;
			}
			sb.append("]");
			return sb.toString();
		}
		
		
	}
}
