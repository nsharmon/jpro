package com.nsharmon.jpro.engine.statements;

import com.nsharmon.jpro.engine.PrologProgram;

public class RuleStatement implements Statement<PrologProgram> {
	private final FactStatement left, right;
	
	public RuleStatement(final FactStatement left, final FactStatement right) {
		this.left = left;
		this.right = right;
	}
	
	public void run(final PrologProgram program) {
		
	}
}
