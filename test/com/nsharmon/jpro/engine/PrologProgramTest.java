package com.nsharmon.jpro.engine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.engine.statements.VariableExpression;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.util.TestToken;

public class PrologProgramTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExtremelySimpleProgram() {
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		QueryStatement queryStatement;

		// it_is_raining.		
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "it_is_raining"));
		statements.add(factStatement);

		// ?- it_is_raining.
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "it_is_raining"));
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertEquals("yes.", program.getLastReturn());

		program.reset();

		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "it_is_pouring"));
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		program.run();
		assertEquals("no.", program.getLastReturn());

	}

	@Test
	public void testVeryVerySimpleProgram1() {
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		ArrayExpression expr;

		// cat(Tom).		
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Tom")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(Jerry).
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Tom")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);

		final QueryStatement queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertEquals("yes.", program.getLastReturn());
	}

	@Test
	public void testVeryVerySimpleProgram2() {
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		ArrayExpression expr;

		// cat(Tom).		
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Tom")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(Jerry).
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Jerry")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);

		final QueryStatement queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertEquals("no.", program.getLastReturn());
	}
}
