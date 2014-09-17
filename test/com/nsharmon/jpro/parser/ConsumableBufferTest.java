package com.nsharmon.jpro.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsumableBufferTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {
		final ArrayList<Integer> test = new ArrayList<Integer>();

		final ConsumableBuffer<Integer> cb = new ConsumableBuffer<Integer>(test);

		assertTrue(!cb.hasNext() && cb.next() == null);
	}

	@Test
	public void testFarMark() {
		final ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);

		final ConsumableBuffer<Integer> cb = new ConsumableBuffer<Integer>(test);
		cb.mark(3);

		assertEquals(1, cb.next().intValue());
		assertEquals(2, cb.next().intValue());

		assertTrue(!cb.hasNext() && cb.next() == null);
	}

	@Test
	public void testMark() {
		final ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		test.add(5);

		final ConsumableBuffer<Integer> cb = new ConsumableBuffer<Integer>(test);

		assertEquals(1, cb.next().intValue());

		cb.mark(2);
		assertEquals(2, cb.next().intValue());
		assertEquals(3, cb.next().intValue());
		cb.reset();

		assertEquals(2, cb.next().intValue());

		cb.mark(1);
		assertEquals(3, cb.next().intValue());
		cb.reset();

		assertEquals(3, cb.next().intValue());
		assertEquals(4, cb.next().intValue());
		assertEquals(5, cb.next().intValue());

		assertTrue(!cb.hasNext() && cb.next() == null);
	}
}
