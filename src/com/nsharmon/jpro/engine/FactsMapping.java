package com.nsharmon.jpro.engine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.engine.statements.RuleStatement;

public class FactsMapping {
	private final LinkedHashSet<FactStatement> facts = new LinkedHashSet<FactStatement>();
	private final LinkedHashSet<FactStatement> assumedFacts = new LinkedHashSet<FactStatement>();
	private final LinkedHashSet<RuleStatement> rules = new LinkedHashSet<RuleStatement>();
	
	public MatchResult match(final QueryStatement queryStatement) {
		final FactStatement factToCheck = queryStatement.getFactStatement();
		deriveConclusions(factToCheck);
		
		return findRelevantFacts(factToCheck);
	}

	public List<RuleStatement> findRelevantRules(final FactStatement stmt) {
		final List<RuleStatement> foundRules = new ArrayList<RuleStatement>();
		for (final RuleStatement rule : rules) {
			final MatchResult matchResult = rule.getResultingFact().matches(stmt);
			if(matchResult.hasMatches()) {
				foundRules.add(rule);
			}
		}
		return foundRules;
	}
	
	public MatchResult findRelevantFacts(final FactStatement factToCheck) {
		MatchResult result = new MatchResult(false);		
		if (factToCheck.usesVariables()) {
			for (final FactStatement fact : facts) {
				result.accumulate(fact.matches(factToCheck));
			}		
			for (final FactStatement fact : assumedFacts) {
				result.accumulate(fact.matches(factToCheck));
			}				
		} else if (facts.contains(factToCheck)) {
			result = new MatchResult(true);
			result.addMatch(result.new Match(factToCheck));
		}
		return result;
	}
	
	private void deriveConclusions(final FactStatement statement) {
		int factCount;
		do {
			factCount = facts.size();
			for (final RuleStatement conclusion : rules) {
				conclusion.deriveConclusions(this, statement);
			}	
		} while (factCount != facts.size());
	}

	public void addFact(final FactStatement factStatement) {
		facts.add(factStatement);
	}
	
	public void addRule(final RuleStatement ruleStatement) {
		rules.add(ruleStatement);
	}

	public void letsAssume(final FactStatement factStatement) {
		assumedFacts.add(factStatement);
	}
	
	public void letsAssume(final List<FactStatement> predicates) {
		assumedFacts.addAll(predicates);
	}	
	
	public LinkedHashSet<FactStatement> getFacts() {
		return facts;
	}

	public LinkedHashSet<RuleStatement> getRules() {
		return rules;
	}

	public void clean() {
		assumedFacts.clear();
	}

	public void qed() {
		facts.addAll(assumedFacts);
		clean();
	}
}
