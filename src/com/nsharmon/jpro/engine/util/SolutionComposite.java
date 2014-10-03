package com.nsharmon.jpro.engine.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nsharmon.jpro.engine.MatchResult;
import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;

public class SolutionComposite {
	private boolean first = true;
	private boolean noSolution = false;
	private final FactStatement relevantStmt;
	private Map<Expression<?>, Set<Expression<?>>> possibleSolutions;
	final MatchResult match = new MatchResult(true);
	
	public SolutionComposite(final Match leftMatch) {
		this.relevantStmt = leftMatch.getRelevantStatement();
	}
	
	public void mergeMatchResult(final MatchResult matchResult) {
		final Map<Expression<?>, Set<Expression<?>>> matchedSolutions = new HashMap<Expression<?>, Set<Expression<?>>>();		
		for(final Match match : matchResult.getMatches().values()) {
			for(final Entry<Expression<?>, Expression<?>> entry : match.getSubstitutions().entrySet()) {	
				Set<Expression<?>> set = matchedSolutions.get(entry.getKey());
				if(set == null) {
					set = new HashSet<Expression<?>>();
					matchedSolutions.put(entry.getKey(), set);
				}
				set.add(entry.getValue());
			}
		}
		
		if(first) {
			possibleSolutions = matchedSolutions;
		} else {
			for(final Entry<Expression<?>, Set<Expression<?>>> entry : matchedSolutions.entrySet()) {
				final Set<Expression<?>> solutionSet = possibleSolutions.get(entry.getKey());				
				if (solutionSet != null) {
					solutionSet.retainAll(entry.getValue());
				}
	
				if(solutionSet == null || solutionSet.size() == 0) {
					noSolution = true;
					break;
				}				
			}
		}
		first = false;
	}
	
	public MatchResult getMatchResult() {
		if(noSolutionPossible()) {
			match.setMatches(false);
		} else {
			for(final Entry<Expression<?>, Set<Expression<?>>> solutionSet : possibleSolutions.entrySet()) {
				for (final Expression<?> solution : solutionSet.getValue()) {
					solution.setStatement(relevantStmt);
					match.addMatch(relevantStmt, solutionSet.getKey(), solution);
				}
			}
		}
		
		return match;
	}

	public boolean noSolutionPossible() {
		return !first && noSolution;
	}
}
