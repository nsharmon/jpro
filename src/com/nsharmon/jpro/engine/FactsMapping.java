package com.nsharmon.jpro.engine;

import java.util.LinkedHashSet;

import com.nsharmon.jpro.engine.statements.FactStatement;

public class FactsMapping {
	private final LinkedHashSet<FactStatement> facts = new LinkedHashSet<FactStatement>();

	public MatchResult match(final FactStatement statement) {
		MatchResult result = new MatchResult(false);

		if (statement.usesVariables()) {
			for (final FactStatement fact : facts) {
				result.accumulate(fact.matches(statement));
			}
		} else if (facts.contains(statement)) {
			result = new MatchResult(true);
			result.addFactStatement(statement);
		}
		return result;
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
}
