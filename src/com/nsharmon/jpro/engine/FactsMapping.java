package com.nsharmon.jpro.engine;

import java.util.HashSet;
import java.util.Set;

import com.nsharmon.jpro.engine.statements.FactStatement;

public class FactsMapping {
	private final Set<FactStatement> facts = new HashSet<FactStatement>();

	public boolean isTrue(final FactStatement statement) {
		return facts.contains(statement);
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
}
