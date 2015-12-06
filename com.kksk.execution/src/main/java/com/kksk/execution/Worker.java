package com.kksk.execution;

public abstract class Worker<Input, Output> extends Consumer<Lockable<Input, Output>> {
	public abstract Output work(Input input);

	@Override
	public final void consume(Lockable<Input, Output> lockable) {
		synchronized (lockable.lock) {
			lockable.setOutput(work(lockable.input));
			lockable.lock.notify();
		}
	}
}
