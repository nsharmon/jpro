package com.nsharmon.jpro.tokenizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TokenTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEquality() {
		final Token<PrologTokenType, String> a1 = new Token<PrologTokenType, String>(PrologTokenType.ATOM, "a");
		final Token<PrologTokenType, String> a2 = new Token<PrologTokenType, String>(PrologTokenType.ATOM, "a");
		final Token<PrologTokenType, String> b1 = new Token<PrologTokenType, String>(PrologTokenType.ATOM, "b");
		final Token<PrologTokenType, String> b2 = new Token<PrologTokenType, String>(PrologTokenType.VARIABLE, "b");

		assertTrue(a1.equals(a2));
		assertFalse(a1.equals(b1));
		assertFalse(b1.equals(b2));
		assertFalse(a1.equals(b1));
		assertFalse(a2.equals(b2));
	}

}
