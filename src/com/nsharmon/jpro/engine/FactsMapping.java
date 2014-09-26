package com.nsharmon.jpro.engine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.nsharmon.jpro.engine.statements.FactStatement;

public class FactsMapping {
	private final LinkedHashSet<FactStatement> facts = new LinkedHashSet<FactStatement>();

	public List<FactStatement> match(final FactStatement statement) {
		final List<FactStatement> trueFactStatements = new ArrayList<FactStatement>();
		if (statement.usesVariables()) {
			for (final FactStatement fact : facts) {
				if (fact.matches(statement)) {
					trueFactStatements.add(fact);
				}
			}
		} else if (facts.contains(statement)) {
			trueFactStatements.add(statement);
		}
		return trueFactStatements;
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
}
