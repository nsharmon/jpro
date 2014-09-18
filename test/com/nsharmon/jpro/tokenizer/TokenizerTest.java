package com.nsharmon.jpro.tokenizer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenizerTest {
	private AbstractTokenizer<PrologTokenType> tokenizer = null;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInputStreamClose() throws IOException {
		final String testString = "   ";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals("", sb.toString());
		assertEquals(-1, in.read());
	}

	@Test
	public void testWhitespace() throws IOException {
		final String testString = " testing whitespace       end";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);
		tokenizer.setIgnoreWhitespace(false);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals("WHITESPACE testing[ATOM] WHITESPACE whitespace[ATOM] WHITESPACE end[ATOM] ", sb.toString());
	}

	@Test
	public void testTokenTypes() throws IOException {
		final String testString = " atom Variable () :- . $ 5.2 [] \"test\" , /* commentary */";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals(
				"atom[ATOM] Variable[VARIABLE] ([OPENPAREN] )[CLOSEPAREN] :-[HORNOPER] .[CLOSE] $[UNKNOWN] 5.2[NUMBER] [[OPENBRACKET] ][CLOSEBRACKET] \"test\"[STRING] ,[COMMA] /* commentary */[COMMENT] ",
				sb.toString());
	}

	@Test
	public void testNumbertoken() throws IOException {
		final String testString = "Number 0.5 .2 1.0 NonNumber 2.3. .1. .. 0.4.5";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals(
				"Number[VARIABLE] 0.5[NUMBER] 0.2[NUMBER] 1[NUMBER] NonNumber[VARIABLE] 2.3.[UNKNOWN] .1.[UNKNOWN] .[CLOSE] .[CLOSE] 0.4.5[UNKNOWN] ",
				sb.toString());
	}

	@Test
	public void testString() throws IOException {
		final String testString = "\"test\"  \"\" \"test break \\\"\" \"open ";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals("\"test\"[STRING] \"\"[STRING] \"test break \\\"\"[STRING] \"open [UNKNOWN] ", sb.toString());
	}

	@Test
	public void testCommentary() throws IOException {
		final String testString = "/*test*/  /**/ /*/ */ /* open";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals("/*test*/[COMMENT] /**/[COMMENT] /*/ */[COMMENT] /* open[UNKNOWN] ", sb.toString());
	}

	@Test
	public void testArrayDeclaration() throws IOException {
		final String testString = "[] [1] [2, 3.2] [\"abc\", 0.]";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals(
				"[[OPENBRACKET] ][CLOSEBRACKET] [[OPENBRACKET] 1[NUMBER] ][CLOSEBRACKET] [[OPENBRACKET] 2[NUMBER] ,[COMMA] 3.2[NUMBER] ][CLOSEBRACKET] [[OPENBRACKET] \"abc\"[STRING] ,[COMMA] 0[NUMBER] ][CLOSEBRACKET] ",
				sb.toString());
	}

	@Test
	public void testFactStatement() throws IOException {
		final String testString = "cat(Tom).\r\ncat(Sylvester).";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals(
				"cat[ATOM] ([OPENPAREN] Tom[VARIABLE] )[CLOSEPAREN] .[CLOSE] cat[ATOM] ([OPENPAREN] Sylvester[VARIABLE] )[CLOSEPAREN] .[CLOSE] ",
				sb.toString());
	}
}
