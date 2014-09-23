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

		assertEquals(
				"WHITESPACE[line:1] testing[type:ATOM][line:1] WHITESPACE[line:1] whitespace[type:ATOM][line:1] WHITESPACE[line:1] end[type:ATOM][line:1] ",
				sb.toString());
	}

	@Test
	public void testTokenTypes() throws IOException {
		final String testString = " atom Variable () :- ?- . $ 5.2 [] \"test\" , /* commentary */";
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
				"atom[type:ATOM][line:1] Variable[type:VARIABLE][line:1] ([type:OPENPAREN][line:1] )[type:CLOSEPAREN][line:1] :-[type:HORNOPER][line:1] ?-[type:QUERY][line:1] .[type:CLOSE][line:1] $[type:UNKNOWN][line:1] 5.2[type:NUMBER][line:1] [[type:OPENBRACKET][line:1] ][type:CLOSEBRACKET][line:1] \"test\"[type:STRING][line:1] ,[type:COMMA][line:1] /* commentary */[type:COMMENT][line:1] ",
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
				"Number[type:VARIABLE][line:1] 0.5[type:NUMBER][line:1] 0.2[type:NUMBER][line:1] 1[type:NUMBER][line:1] NonNumber[type:VARIABLE][line:1] 2.3.[type:UNKNOWN][line:1] .1.[type:UNKNOWN][line:1] .[type:CLOSE][line:1] .[type:CLOSE][line:1] 0.4.5[type:UNKNOWN][line:1] ",
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

		assertEquals(
				"\"test\"[type:STRING][line:1] \"\"[type:STRING][line:1] \"test break \\\"\"[type:STRING][line:1] \"open [type:UNKNOWN][line:1] ",
				sb.toString());
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

		assertEquals(
				"/*test*/[type:COMMENT][line:1] /**/[type:COMMENT][line:1] /*/ */[type:COMMENT][line:1] /* open[type:UNKNOWN][line:1] ",
				sb.toString());
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
				"[[type:OPENBRACKET][line:1] ][type:CLOSEBRACKET][line:1] [[type:OPENBRACKET][line:1] 1[type:NUMBER][line:1] ][type:CLOSEBRACKET][line:1] [[type:OPENBRACKET][line:1] 2[type:NUMBER][line:1] ,[type:COMMA][line:1] 3.2[type:NUMBER][line:1] ][type:CLOSEBRACKET][line:1] [[type:OPENBRACKET][line:1] \"abc\"[type:STRING][line:1] ,[type:COMMA][line:1] 0[type:NUMBER][line:1] ][type:CLOSEBRACKET][line:1] ",
				sb.toString());
	}

	@Test
	public void testNewline() throws IOException {
		final String testString = "a b\r\nc\n\nd";
		final InputStream in = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		tokenizer = new PrologTokenizer(in);

		final StringBuilder sb = new StringBuilder();
		for (final Token<PrologTokenType, ?> token : tokenizer) {
			if (token != null) {
				sb.append(token.toString());
				sb.append(" ");
			}
		}

		assertEquals("a[type:ATOM][line:1] b[type:ATOM][line:1] c[type:ATOM][line:2] d[type:ATOM][line:4] ",
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
				"cat[type:ATOM][line:1] ([type:OPENPAREN][line:1] Tom[type:VARIABLE][line:1] )[type:CLOSEPAREN][line:1] .[type:CLOSE][line:1] cat[type:ATOM][line:2] ([type:OPENPAREN][line:2] Sylvester[type:VARIABLE][line:2] )[type:CLOSEPAREN][line:2] .[type:CLOSE][line:2] ",
				sb.toString());
	}
}
