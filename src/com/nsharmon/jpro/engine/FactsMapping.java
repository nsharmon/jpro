package com.nsharmon.jpro.engine;

import java.util.LinkedHashSet;
import java.util.Set;

import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.RuleStatement;

public class FactsMapping {
	private final LinkedHashSet<FactStatement> facts = new LinkedHashSet<FactStatement>();
	private final LinkedHashSet<RuleStatement> conclusions = new LinkedHashSet<RuleStatement>();
	
	public MatchResult match(final FactStatement statement) {
		final Set<FactStatement> statementsToCheck = deriveConclusions(statement);
		
		MatchResult result = new MatchResult(false);		
		for(final FactStatement statementToCheck : statementsToCheck) {
			if (statement.usesVariables()) {
				for (final FactStatement fact : facts) {
					result.accumulate(fact.matches(statementToCheck));
				}			
			} else if (facts.contains(statementToCheck)) {
				result = new MatchResult(true);
				result.addFactStatement(statementToCheck);
			}
		}
		return result;
	}

	private Set<FactStatement> deriveConclusions(final FactStatement statement) {
		final Set<FactStatement> statementsToCheck = new LinkedHashSet<FactStatement>();
		statementsToCheck.add(statement);

		int statementsCount;
		do {
			statementsCount = statementsToCheck.size();
			for (final RuleStatement conclusion : conclusions) {
				final MatchResult matchedConclusion = conclusion.getLeft().matches(statement);
				if(matchedConclusion.hasMatches()) {
					final FactStatement fact = conclusion.getRight().applySubstitutions(matchedConclusion.getSubstitutions());
					statementsToCheck.add(fact);
				}
			}	
		} while (statementsCount != statementsToCheck.size());
		return statementsToCheck;
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
	
	public void addConclusion(final RuleStatement ruleStatement) {
		conclusions.add(ruleStatement);
	}	
}
