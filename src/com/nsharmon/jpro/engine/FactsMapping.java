package com.nsharmon.jpro.engine;

import java.util.LinkedHashSet;

import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.RuleStatement;

public class FactsMapping {
	private final LinkedHashSet<FactStatement> facts = new LinkedHashSet<FactStatement>();
	private final LinkedHashSet<RuleStatement> conclusions = new LinkedHashSet<RuleStatement>();
	
	public MatchResult match(final FactStatement statement) {
		deriveConclusions(statement);
		
		MatchResult result = new MatchResult(false);		
		if (statement.usesVariables()) {
			for (final FactStatement fact : facts) {
				result.accumulate(fact.matches(statement));
			}			
		} else if (facts.contains(statement)) {
			result = new MatchResult(true);
			result.addMatch(result.new Match(statement));
		}
		return result;
	}

	private void deriveConclusions(final FactStatement statement) {
		int factCount;
		do {
			factCount = facts.size();
			for (final RuleStatement conclusion : conclusions) {
				conclusion.deriveConclusions(facts, statement);
			}	
		} while (factCount != facts.size());
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
	
	public void addConclusion(final RuleStatement ruleStatement) {
		conclusions.add(ruleStatement);
	}	
}
