package com.nsharmon.jpro.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nsharmon.jpro.engine.statements.FactStatement;
import com.nsharmon.jpro.engine.statements.Statement;
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
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.OPENPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSE));

		final PrologParser parser = new PrologParser(tokenList);

		final List<Statement> statements = parser.parse();

		assertEquals(1, statements.size());
		assertTrue(statements.size() == 0 || (statements.get(0) instanceof FactStatement));
	}

	@Test
	public void testFactStatementInvalidClose1() {
		// cat(Tom).
		final ListTokenizer tokenList = new ListTokenizer();
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.OPENPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSEPAREN));
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
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.OPENPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSEPAREN));
		// tokenList.addToken(new Token<PrologTokenType,
		// String>(PrologTokenType.CLOSE));

		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.OPENPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Sylvester"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSE));

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
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.ATOM, "cat"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.OPENPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Tom"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.COMMA));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Sylvester"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.COMMA));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "Bill"));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSEPAREN));
		tokenList.addToken(new Token<PrologTokenType, String>(PrologTokenType.CLOSE));

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

	public class ListTokenizer implements Tokenizer<PrologTokenType> {
		private final List<Token<PrologTokenType, ?>> items = new ArrayList<Token<PrologTokenType, ?>>();

		public Iterator<Token<PrologTokenType, ?>> iterator() {
			return items.iterator();
		}

		public void addToken(final Token<PrologTokenType, ?> token) {
			items.add(token);
		}
	}
}
