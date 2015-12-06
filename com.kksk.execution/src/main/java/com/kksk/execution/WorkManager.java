package com.kksk.execution;

public class WorkManager<Input, Output> extends Producer<Lockable<Input, Output>> {
	/**
	 * @param threadGroupName
	 * @param thredNamePrefix
	 * @param queueSize
	 * @param threadCount
	 * @param worker
	 */
	public WorkManager(final String threadGroupName, final String thredNamePrefix, final int queueSize, final int threadCount, final Worker<Input, Output> worker) {
		super(threadGroupName, thredNamePrefix, queueSize, threadCount, worker);
	}

	public Output execute(Input input) throws InterruptedException {
		Lockable<Input, Output> lockable = new Lockable<>(input);
		synchronized (lockable.lock) {
			super.produce(lockable);
			lockable.lock.wait();
			return lockable.getOutput();
		}
	}

	@Override
	public void produce(Lockable<Input, Output> input) throws InterruptedException {
		throw new UnsupportedOperationException("Cannot call this method.");
	}
}
