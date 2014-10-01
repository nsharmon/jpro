package com.nsharmon.jpro.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.AtomExpression;
import com.nsharmon.jpro.engine.statements.CompositeStatement;
import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.util.ListTokenizer;
import com.nsharmon.jpro.tokenizer.util.StringToken;

public class PrologParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleFactStatement() {
		// cat.
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(1, statements.size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof FactStatement));
	}

	@Test
	public void testFactStatement() {
		// cat(tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "tom"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(1, statements.size());
		assertEquals(0, parser.getReporter().getMessages().size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof FactStatement));
	}

	@Test
	public void testRuleStatement() {
		// mortal(X) :- human(X).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "mortal"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.HORNOPER));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "human"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));		
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(1, statements.size());
		assertEquals(0, parser.getReporter().getMessages().size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof CompositeStatement));
	}
	
	@Test
	public void testRuleStatementMultipleArgs() {
		// mortal(X) :- human(X), will_die(X), is_born(X).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "mortal"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.HORNOPER));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "human"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "will_die"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));		
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "is_born"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "X"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));		
		
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(1, statements.size());
		assertEquals(0, parser.getReporter().getMessages().size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof CompositeStatement));
		
		if(statements.size() == 0 || (statements.get(0) instanceof CompositeStatement)) {
			final CompositeStatement<PrologProgram> compStmt = (CompositeStatement<PrologProgram>)statements.get(0);
			
			assertEquals(3, compStmt.size());	
		}
	}
	
	
	@Test
	public void testFactStatementInvalidClose1() {
		// cat(tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "tom"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		// tokenList.addToken(new Token<PrologTokenType,
		// String>(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		parser.parse();

		assertEquals(1, parser.getReporter().getMessages().size());
		assertEquals("Expected . but found \"END OF FILE\" instead", parser.getReporter().getMessages().get(0)
				.getMessage());
	}

	@Test
	public void testFactStatementInvalidClose2() {
		// cat(tom) cat(sylvester).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "tom"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		// tokenList.addToken(new Token<PrologTokenType,
		// String>(PrologTokenType.CLOSE));

		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "sylvester"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		parser.parse();

		assertEquals(1, parser.getReporter().getMessages().size());
		assertEquals("Expected . but found \"cat[type:ATOM]\" instead", parser.getReporter().getMessages().get(0)
				.getMessage());
	}

	@Test
	public void testFactStatementMultipleArguments() {
		// cat(tom, sylvester, bill).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "tom"));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "sylvester"));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "bill"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(0, parser.getReporter().getMessages().size());
		assertEquals(1, statements.size());

		final Statement<PrologProgram> statement = statements.size() != 0 ? statements.get(0) : null;
		assertTrue(statement != null && statement instanceof FactStatement);
		if (statement != null && statement instanceof FactStatement) {
			final FactStatement factStatement = (FactStatement) statement;
			assertEquals(3, factStatement.getArgumentsExpression().getCount());
		}
	}

	@Test
	public void testFactStatementMultipleNestedArguments() {
		// cat([tom, [[sylvester], bill]]).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "tom"));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));
		tokenList.addToken(new StringToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "sylvester"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.COMMA));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "bill"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(0, parser.getReporter().getMessages().size());
		assertEquals(1, statements.size());

		final Statement<PrologProgram> statement = statements.size() != 0 ? statements.get(0) : null;
		assertTrue(statement != null && statement instanceof FactStatement);
		if (statement != null && statement instanceof FactStatement) {
			final FactStatement factStatement = (FactStatement) statement;
			final ArrayExpression argumentsExpression = factStatement.getArgumentsExpression();
			assertEquals(1, argumentsExpression.getCount());
			if (argumentsExpression.getCount() > 0) {
				Expression<?> expression;
				expression = argumentsExpression.getValue().get(0);
				assertTrue(expression != null && expression instanceof ArrayExpression); 								// [tom, [[sylvester], bill]]

				if (expression != null && expression instanceof ArrayExpression) {
					final ArrayExpression ae = (ArrayExpression) expression;
					assertEquals(2, ae.getCount());

					if (ae.getCount() > 0) {
						expression = ae.getValue().get(0);
						assertTrue(expression != null && expression instanceof AtomExpression); 					// tom

						expression = ae.getValue().get(1);
						assertTrue(expression != null && expression instanceof ArrayExpression); 						// [[sylvester], bill]

						if (expression != null && expression instanceof ArrayExpression) {
							final ArrayExpression ae2 = (ArrayExpression) expression;
							assertEquals(2, ae2.getCount());

							assertTrue(ae2.getCount() >= 2 && ae2.getValue().get(0) instanceof ArrayExpression); 		// [sylvester]
							assertTrue(ae2.getCount() >= 2 && ae2.getValue().get(1) instanceof AtomExpression); 	// bill
						}
					}
				}
			}
		}
	}

	@Test
	public void testQueryStatement() {
		// ?- cat(Tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new StringToken(PrologTokenType.QUERY));
		tokenList.addToken(new StringToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new StringToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new StringToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(0, parser.getReporter().getMessages().size());
		assertEquals(1, statements.size());

		final Statement<PrologProgram> statement = statements.size() != 0 ? statements.get(0) : null;
		assertTrue(statement != null && statement instanceof QueryStatement);
		if (statement != null && statement instanceof QueryStatement) {
			final QueryStatement queryStatement = (QueryStatement) statement;
			assertEquals(1, queryStatement.getFactStatement().getArgumentsExpression().getCount());
		}
	}
}
