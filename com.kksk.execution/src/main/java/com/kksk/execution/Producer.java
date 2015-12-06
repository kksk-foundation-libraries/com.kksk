package com.kksk.execution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Producer<Input> {
	private final BlockingQueue<Input> queue;
	private final Thread[] threadpool;

	/**
	 * @param threadGroupName
	 * @param threadNamePrefix
	 * @param queueSize
	 * @param threadCount
	 * @param consumer
	 */
	public Producer(final String threadGroupName, final String threadNamePrefix, final int queueSize, final int threadCount, final Consumer<Input> consumer) {
		this.queue = new ArrayBlockingQueue<>(queueSize);
		final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
		final Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						consumer.consume(queue.take());
					}
				} catch (InterruptedException e) {
				}
			}
		};

		threadpool = new Thread[threadCount];
		for (int i = 0; i < threadCount; i++) {
			threadpool[i] = new Thread(threadGroup, thread, threadNamePrefix + i);
		}
	}

	public void produce(Input input) throws InterruptedException {
		queue.put(input);
	}

	public void shutdown() {
		if (threadpool == null)
			return;
		for (int i = 0; i < threadpool.length; i++) {
			threadpool[i].interrupt();
		}
	}
}
