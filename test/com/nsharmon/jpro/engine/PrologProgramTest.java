package com.nsharmon.jpro.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.MatchResult.Match;
import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.AtomExpression;
import com.nsharmon.jpro.engine.statements.Expression;
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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_raining"), true);
		statements.add(factStatement);

		// ?- it_is_raining.
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_raining"), true);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "it_is_pouring"), true);
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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
		statements.add(factStatement);

		// ?- cat(Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);

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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
		statements.add(factStatement);

		// ?- cat(jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
		statements.add(factStatement);

		// ?- cat(Tom, Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);

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

		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
		statements.add(factStatement);

		// ?- cat(tom, Another).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new VariableExpression(new StringToken(PrologTokenType.VARIABLE, "Another")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);

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
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);
		statements.add(factStatement);

		// ?- cat(tom, jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());

		// ?- cat(tom, Jerry).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new AtomExpression(new StringToken(PrologTokenType.ATOM, "tom")));
		expr.addExpression(new VariableExpression(new StringToken(PrologTokenType.VARIABLE, "Jerry")));
		factStatement = new FactStatement(new StringToken(PrologTokenType.ATOM, "cat"), expr, true);

		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}

	@Test
	public void testParsedProgram1() {
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
	public void testParsedProgram2() {
		/*
		 * tape(1,van_morrison,astral_weeks,madam_george).
		 * tape(2,beatles,sgt_pepper,a_day_in_the_life).
		 * tape(3,beatles,abbey_road,something).
		 * tape(4,rolling_stones,sticky_fingers,brown_sugar).
		 * tape(5,eagles,hotel_california,new_kid_in_town).
		 * ?- tape(5,Artist,Album,Fave_Song).
		 * Artist=eagles
		 * Fave_Song=new_kid_in_town
		 * Album=hotel_california
		 * yes.
		 */
		final String testString = 
				"tape(1,van_morrison,astral_weeks,madam_george).\n" + 
				"tape(2,beatles,sgt_pepper,a_day_in_the_life).\n" + 
				"tape(3,beatles,abbey_road,something).\n" +
				"tape(4,rolling_stones,sticky_fingers,brown_sugar).\n" +
				"tape(5,eagles,hotel_california,new_kid_in_town).\n" +
				"?- tape(5,Artist,Album,Fave_Song).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
		assertEquals(1, program.getLastReturn().getMatches().size());
		if(program.getLastReturn().getMatches().size() >= 1) {
			final Match match = program.getLastReturn().getMatches().values().iterator().next();
			
			assertEquals(3, match.getSubstitutions().size());
			for (final Entry<Expression<?>, Expression<?>> substitution : match.getSubstitutions().entrySet()) {
				final Object variableExpr = substitution.getKey();
				final Object substitutionExpr = substitution.getValue();
				if (variableExpr.equals(new StringToken(PrologTokenType.VARIABLE, "Artist"))) {
					assertEquals(new StringToken(PrologTokenType.ATOM, "eagles"), substitutionExpr);
				} else if (variableExpr.equals(new StringToken(PrologTokenType.VARIABLE, "Album"))) {
					assertEquals(new StringToken(PrologTokenType.ATOM, "hotel_california"), substitutionExpr);
				} else if (variableExpr.equals(new StringToken(PrologTokenType.VARIABLE, "Fave_Song"))) {
					assertEquals(new StringToken(PrologTokenType.ATOM, "new_kid_in_town"), substitutionExpr);
				}
			}
		}

	}

	@Test
	public void testParsedRuleProgram1() {
		/*
		 * mortal(X) :- human(X).
		 * human(socrates).
		 * ?- mortal(socrates).
		 * yes.
		 */
		final String testString = 
				"mortal(X) :- human(X).\n" + 
				"human(socrates).\n" + 
				"?- mortal(socrates).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}
	
	@Test
	public void testParsedRuleProgram2() {
		/*
		 * fun(X) :- red(X), car(X)
		 * fun(X) :- blue(X), bike(X)
		 * fun(ice_cream).
		 * 
		 * car(vw_beatle).
		 * car(ford_escort).
		 * bike(harley_davidson).
		 * red(vw_beatle).
		 * red(ford_escort).
		 * blue(harley_davidson).
		 * ?- fun(ford_escort).
		 * no.
		 * ?- fun(harley_davidson).
		 * yes.
		 * ?- fun(ice_cream).
		 * yes.
		 */
		final String testString = 
				"fun(X) :- red(X), car(X)\n" + 
				"fun(X) :- blue(X), bike(X)\n" +
				"fun(ice_cream).\n" +
				"car(vw_beatle).\n" +
				"car(ford_escort).\n" +
				"bike(harley_davidson).\n" +
				"red(vw_beatle).\n"+
				"blue(ford_escort).\n"+
				"blue(harley_davidson).\n"+
//				"?- fun(ford_escort).\n" +
//				"?- fun(harley_davidson).\n"+
//				"?- fun(ice_cream).\n"+
				"?- fun(What).\n";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getReturns().get(0).hasMatches());		
//		assertFalse(program.getReturns().get(0).hasMatches());
//		assertTrue(program.getReturns().get(1).hasMatches());
//		assertTrue(program.getReturns().get(2).hasMatches());
	}
	
	@Test
	public void testParsedRuleProgram3() {
		/*
		 * a(X) :- b(X).
		 * b(X) :- c(X).
		 * c(X) :- d(X).
		 * d(test).
		 * ?- a(test).
		 * yes.
		 */
		final String testString = 
				"a(X) :- b(X).\n" + 
				"b(X) :- c(X).\n" +
				"c(X) :- d(X).\n" +
				"d(test).\n" +
				"?- a(test).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());
	}
	
	
	@Test
	public void testParsedRuleProgram4() {
		/*
		 * a(X) :- b(X).
		 * b(X) :- c(X).
		 * c(X) :- d(X), e(X).
		 * d(test).
		 * ?- a(test).
		 * no.
		 * c(X) :- d(X).
		 * ?- a(test).
		 * yes.
		 */
		final String testString = 
				"a(X) :- b(X).\n" + 
				"b(X) :- c(X).\n" +
				"c(X) :- d(X), e(X).\n" +
				"d(test).\n" +
				"?- a(test).\n" + 
				"c(X) :- d(X).\n" +
				"?- a(test).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertFalse(program.getReturns().get(0).hasMatches());
		assertTrue(program.getReturns().get(1).hasMatches());
	}
	
	@Test
	public void testParsedRuleProgram5() {
		/*
		 * likes(john,mary).
		 * likes(john,trains).
		 * likes(peter,fast_cars). 
		 * likes(Person1,Person2):-
		 * hobby(Person1,Hobby), 
		 * hobby(Person2,Hobby).
		 * hobby(john,trainspotting). 
		 * hobby(tim,sailing). 
		 * hobby(helen,trainspotting). 
		 * hobby(simon,sailing).
		*/
		final String testString = 
				"likes(john,mary).\r\n" + 
				"likes(john,trains).\r\n" + 
				"likes(peter,fast_cars). \r\n" + 
				"likes(Person1,Person2) :-\r\n" + 
				"	hobby(Person1,Hobby), \r\n" + 
				"	hobby(Person2,Hobby).\r\n" + 
				"hobby(john,trainspotting). \r\n" + 
				"hobby(tim,sailing). \r\n" + 
				"hobby(helen,trainspotting). \r\n" + 
				"hobby(simon,sailing). \r\n" +
				"?- likes(john,trains). \r\n" +
				"?- likes(helen,john). \r\n" +
				"?- likes(tim,helen). \r\n" +
				"?- likes(john,helen). \r\n";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getReturns().get(0).hasMatches());
		assertTrue(program.getReturns().get(1).hasMatches());
		assertFalse(program.getReturns().get(2).hasMatches());
		assertTrue(program.getReturns().get(3).hasMatches());
	}
	
	@Test
	public void testParsedRuleProgram6() {
		/*
		 * on_route(rome).
		 * on_route(Place):-
		 * move(Place,Method,NewPlace),
		 * on_route(NewPlace).
		 * move(home,taxi,halifax).
		 * move(halifax,train,gatwick).
		 * move(gatwick,plane,rome).
		 * ?- on_route(home).
		 * yes.
		*/
		final String testString = 
				"on_route(rome).\r\n" + 
				"on_route(Place) :- \r\n" + 
				"	move(Place,Method,NewPlace),\r\n" + 
				"	on_route(NewPlace).\r\n" + 
				"move(home,taxi,halifax).\r\n" + 
				"move(halifax,train,gatwick).\r\n" + 
				"move(gatwick,plane,rome).\r\n" +
				"?- on_route(home).";
		
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

		final PrologProgram program = new PrologProgram(in);
		program.run();

		assertTrue(program.getReturns().get(0).hasMatches());
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
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr, true);
		statements.add(factStatement);

		// ?- answerToLifeTheUniverseAndEverything(42.0).
		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42.0)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr, true);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);

		final PrologProgram program = new PrologProgram(statements, System.out);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr, true);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);
		program.run();

		assertTrue(program.getLastReturn().hasMatches());

		expr = new ArrayExpression(PrologTokenType.OPENPAREN, PrologTokenType.CLOSEPAREN);
		expr.addExpression(new NumberExpression(new NumberToken(42.1)));
		factStatement = new FactStatement(
				new StringToken(PrologTokenType.ATOM, "answerToLifeTheUniverseAndEverything"), expr, true);
		queryStatement = new QueryStatement(factStatement);
		statements.add(queryStatement);
		program.run();

		assertFalse(program.getLastReturn().hasMatches());
	}
}
