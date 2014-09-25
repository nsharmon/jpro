package com.nsharmon.jpro.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.PrologProgram;
import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.QueryStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.engine.statements.VariableExpression;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.util.ListTokenizer;
import com.nsharmon.jpro.tokenizer.util.TestToken;

public class PrologParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFactStatement() {
		// cat(Tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement<PrologProgram>> statements = parser.parse();

		assertEquals(1, statements.size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof FactStatement));
	}

	@Test
	public void testFactStatementInvalidClose1() {
		// cat(Tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
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
		// cat(Tom) cat(Sylvester).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		// tokenList.addToken(new Token<PrologTokenType,
		// String>(PrologTokenType.CLOSE));

		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Sylvester"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		parser.parse();

		assertEquals(1, parser.getReporter().getMessages().size());
		assertEquals("Expected . but found \"cat[type:ATOM]\" instead", parser.getReporter().getMessages().get(0)
				.getMessage());
	}

	@Test
	public void testFactStatementMultipleArguments() {
		// cat(Tom, Sylvester, Bill).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.COMMA));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Sylvester"));
		tokenList.addToken(new TestToken(PrologTokenType.COMMA));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Bill"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSE));

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
		// cat([Tom, [[Sylvester], Bill]]).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.COMMA));
		tokenList.addToken(new TestToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.OPENBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Sylvester"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.COMMA));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Bill"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEBRACKET));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSE));

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
				assertTrue(expression != null && expression instanceof ArrayExpression); 								// [Tom, [[Sylvester], Bill]]

				if (expression != null && expression instanceof ArrayExpression) {
					final ArrayExpression ae = (ArrayExpression) expression;
					assertEquals(2, ae.getCount());

					if (ae.getCount() > 0) {
						expression = ae.getValue().get(0);
						assertTrue(expression != null && expression instanceof VariableExpression); 					// Tom

						expression = ae.getValue().get(1);
						assertTrue(expression != null && expression instanceof ArrayExpression); 						// [[Sylvester], Bill]

						if (expression != null && expression instanceof ArrayExpression) {
							final ArrayExpression ae2 = (ArrayExpression) expression;
							assertEquals(2, ae2.getCount());

							assertTrue(ae2.getCount() >= 2 && ae2.getValue().get(0) instanceof ArrayExpression); 		// [Sylvester]
							assertTrue(ae2.getCount() >= 2 && ae2.getValue().get(1) instanceof VariableExpression); 	// Bill
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
		tokenList.addToken(new TestToken(PrologTokenType.QUERY));
		tokenList.addToken(new TestToken(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new TestToken(PrologTokenType.OPENPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new TestToken(PrologTokenType.CLOSE));

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
