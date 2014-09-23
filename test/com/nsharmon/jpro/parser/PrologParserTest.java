package com.nsharmon.jpro.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.statements.ArrayExpression;
import com.nsharmon.jpro.engine.statements.Expression;
import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.Statement;
import com.nsharmon.jpro.engine.statements.VariableExpression;
import com.nsharmon.jpro.tokenizer.PrologTokenType;
import com.nsharmon.jpro.tokenizer.Token;
import com.nsharmon.jpro.tokenizer.Tokenizer;

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

		final List<Statement> statements = parser.parse();

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

		final List<Statement> statements = parser.parse();

		assertEquals(0, parser.getReporter().getMessages().size());
		assertEquals(1, statements.size());

		final Statement statement = statements.size() != 0 ? statements.get(0) : null;
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

		final List<Statement> statements = parser.parse();

		assertEquals(0, parser.getReporter().getMessages().size());
		assertEquals(1, statements.size());

		final Statement statement = statements.size() != 0 ? statements.get(0) : null;
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

	public class ListTokenizer implements Tokenizer<PrologTokenType> {
		private final List<Token<PrologTokenType, ?>> items = new ArrayList<Token<PrologTokenType, ?>>();

		public Iterator<Token<PrologTokenType, ?>> iterator() {
			return items.iterator();
		}

		public void addToken(final Token<PrologTokenType, ?> token) {
			items.add(token);
		}
	}

	public class TestToken extends Token<PrologTokenType, String> {
		public TestToken(final PrologTokenType type) {
			super(type);
		}

		public TestToken(final PrologTokenType type, final String value) {
			super(type, value);
		}
	}
}