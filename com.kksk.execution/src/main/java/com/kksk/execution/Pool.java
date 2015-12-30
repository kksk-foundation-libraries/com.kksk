package com.kksk.execution;

import static com.kksk.assertion.Assert.assertGreaterEqual;
import static com.kksk.assertion.Assert.assertNonNull;
import static com.kksk.assertion.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pool<E> {
	private final LinkedList<E> used;
	private final LinkedList<E> unused;
	private final Lock lock;
	private final Condition notEmpty;

	public Pool(E[] elements) {
		assertNonNull(elements);
		assertGreaterEqual(elements.length, 1);
		this.used = new LinkedList<E>();
		this.unused = new LinkedList<E>();
		this.lock = new ReentrantLock(true);
		this.unused.addAll(Arrays.asList(elements));
		this.notEmpty = lock.newCondition();
	}

	public E take() {
		lock.lock();
		try {
			while (unused.size() == 0)
				notEmpty.awaitUninterruptibly();
			E element = unused.poll();
			used.add(element);
			return element;
		} finally {
			lock.unlock();
		}
	}

	public void release(E element) {
		assertTrue(used.contains(element));
		lock.lock();
		try {
			unused.add(element);
			used.remove(element);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public int used() {
		return used.size();
	}

	public int usable() {
		return unused.size();
	}
}
