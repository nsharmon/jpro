package com.nsharmon.jpro.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.AtomExpression;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.NumberExpression;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.engine.statements.VariableExpression;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.util.NumberToken;
import com.nsharmon.jpro.tokenizer.util.StringToken;

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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_raining"));
		statements.add(factStatement);

		// ?- it_is_raining.
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_raining"));
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_pouring"));
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		program.run();
		assertFalse(program.getLastReturn().hasMatches());

	}

	@Test
	public void testVeryVerySimpleProgram1() {
		/*
		 * cat(tom).
		 * ?- cat(tom).
		 * yes.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		ArrayExpression expr;

		// cat(Tom).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);

		final QueryStatement queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}

	@Test
	public void testVeryVerySimpleProgram2() {
		/*
		 * cat(tom).
		 * ?- cat(jerry).
		 * no.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		ArrayExpression expr;

		// cat(tom).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		final QueryStatement queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());
	}

	@Test
	public void testVeryVerySimpleProgram3() {
		/*
		 * cat(tom, bill).
		 * ?- cat(tom, jerry).
		 * no.
		 * cat(tom, jerry).
		 * ?- cat(tom, jerry).
		 * yes.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		QueryStatement queryStatement;
		FactStatement factStatement;
		ArrayExpression expr;

		// cat(Tom, Bill).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "bill")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(Tom, Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());

		// cat(Tom, Jerry).
		statements.add(factStatement);
		// ?- cat(Tom, Jerry).
		statements.add(queryStatement);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}

	@Test
	public void testComplexMatches() {
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		QueryStatement queryStatement;
		FactStatement factStatement;
		ArrayExpression expr, exprInner;

		// cat(tom, [bill]).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));

		exprInner = new ArrayExpression(PrologTokenType.OPENBRACKET, PrologTokenType.CLOSEBRACKET);
		exprInner.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "bill")));
		expr.addExpression(exprInner);

		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(tom, Another).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new VariableExpression(new StringToken(PrologTokenType.VARIABLE, "Another")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}

	@Test
	public void testVariableQuery() {
		/*
		 * cat(tom, bill).
		 * ?- cat(tom, jerry).
		 * no.
		 * ?- cat(tom, Jerry).
		 * Jerry=bill
		 * yes.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		QueryStatement queryStatement;
		FactStatement factStatement;
		ArrayExpression expr;

		// cat(tom, bill).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "bill")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);
		statements.add(factStatement);

		// ?- cat(tom, jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());

		// ?- cat(tom, Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new VariableExpression(new StringToken(PrologTokenType.VARIABLE, "Jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		program.run();

		assertTrue(program.getLastReturn().hasMatches());
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

		assertTrue(program.getLastReturn().hasMatches());
	}

	@Test
	public void testNumberEquality() {
		/*
		 * answerToLifeTheUniverseAndEverything(42).
		 * ?- answerToLifeTheUniverseAndEverything(42.0).
		 * yes.
		 * ?- answerToLifeTheUniverseAndEverything(42).
		 * yes.
		 * ?- answerToLifeTheUniverseAndEverything(42.1).
		 * no.
		 */
		final List<Statement<PrologProgram>> statements = new ArrayList<Statement<PrologProgram>>();

		FactStatement factStatement;
		ArrayExpression expr;
		QueryStatement queryStatement;

		// answerToLifeTheUniverseAndEverything(42).		
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr);
		statements.add(factStatement);

		// ?- answerToLifeTheUniverseAndEverything(42.0).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42.0)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42.1)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());
	}
}
