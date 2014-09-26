package com.nsharmon.jpro.engine;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
		/*
		 * it_is_raining.
		 * ?- it_is_raining.
		 * yes.
		 * ?- it_is_pouring.
		 * no.
		 */
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

		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "it_is_pouring"));
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		program.run();
		assertEquals("no.", program.getLastReturn());

	}

	@Test
	public void testVeryVerySimpleProgram1() {
		/*
		 * cat(Tom).
		 * ?- cat(Tom).
		 * yes.
		 */
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
		/*
		 * cat(Tom).
		 * ?- cat(Jerry).
		 * no.
		 */
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

	@Test
	public void testVeryVerySimpleProgram3() {
		/*
		 * cat(Tom, Bill).
		 * ?- cat(Tom, Jerry).
		 * no.
		 * cat(Tom, Jerry).
		 * ?- cat(Tom, Jerry).
		 * yes.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		QueryStatement queryStatement;
		FactStatement factStatement;
		ArrayExpression expr;

		// cat(Tom, Bill).		
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Tom")));
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Bill")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(Tom, Jerry).
		expr = new ArrayExpression();
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Tom")));
		expr.addExpression(new VariableExpression(new TestToken(PrologTokenType.VARIABLE, "Jerry")));
		factStatement = new FactStatement(new TestToken(PrologTokenType.ATOM, "cat"), expr);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertEquals("no.", program.getLastReturn());

		// cat(Tom, Jerry).
		statements.add(factStatement);
		// ?- cat(Tom, Jerry).
		statements.add(queryStatement);
		program.run();

		assertEquals("yes.", program.getLastReturn());

	}

	@Test
	public void testParsedProgram() {
		/*
		 * cat(Tom).
		 * ?- cat(Tom).
		 * yes.
		 */
		final String testString = "cat(Tom).\n?- cat(Tom).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertEquals("yes.", program.getLastReturn());
	}
}
