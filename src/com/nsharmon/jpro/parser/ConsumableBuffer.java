package com.nsharmon.jpro.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class ConsumableBuffer<T> implements Iterator<T> {
	private final List<T> buffer = new ArrayList<T>();
	private final Iterator<T> source;
	private final Stack<Integer> markStack = new Stack<Integer>();
	private int markIndex = 0;
	private boolean isMarked = false;

	public ConsumableBuffer(final Iterable<T> sourceObject) {
		this.source = sourceObject.iterator();
	}

	public void mark(final int read) {
		if (isMarked && read > 0) {
			markStack.push(markIndex);
		}
		for (int i = buffer.size(); i < (read + markIndex) && source.hasNext(); i++) {
			buffer.add(source.next());
		}
		isMarked = (read > 0);
	}

	public void reset() {
		markIndex = 0;
		if (markStack.size() > 0) {
			markIndex = markStack.pop();
		}
		isMarked = (markStack.size() > 0);
	}

	@Override
	public boolean hasNext() {
		return markIndex < buffer.size() || source.hasNext();
	}

	@Override
	public T next() {
		T obj = null;
		if (markIndex < buffer.size()) {
			obj = buffer.get(markIndex);

			if (!isMarked) {
				buffer.remove(0);
			} else {
				markIndex++;
			}
		} else if (source.hasNext()) {
			obj = source.next();
		}
		return obj;
	}
}
