package com.kksk.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class EventTarget {
	private static final AtomicInteger sequence = new AtomicInteger();
	private final BlockingQueue<Event> events;
	private final List<EventListener> eventListeners;

	public EventTarget() {
		this(1024);
	}

	public EventTarget(int eventQueueSize) {
		events = new ArrayBlockingQueue<>(eventQueueSize);
		eventListeners = new ArrayList<>(10);
		final String tgName = "EventTarget_" + sequence.getAndIncrement();
		Thread thread = new Thread(new ThreadGroup(tgName), tgName + "_Thread") {
			@Override
			public void run() {
				try {
					while (true) {
						Event event = events.take();
						for (EventListener listener : eventListeners) {
							listener.handleEvent(event);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		};

		thread.start();
	}

	public void fire(Event event) {
		try {
			events.put(event);
		} catch (InterruptedException e) {
		}
	}
}
