package com.kksk.identify;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

	/**
	 * epoch.<br>
	 * our default ‘epoch’ begins on January 1st, 2015.
	 */
	private static final long EPOCH = 1420038000000L;
	private static final long LEN_TIMESTAMP = 41L;
	private static final long LEN_SHARD_ID = 12L;
	private static final long LEN_SEQUENCE = 10L;
	private static final long SHIFT_TIMESTAMP = 22L;
	private static final long SHIFT_SHARD_ID = 10L;
	private static final long MASK_TIMESTAMP = (1L << LEN_TIMESTAMP) - 1L;
	private static final long MASK_SHARD_ID = (1L << LEN_SHARD_ID) - 1L;
	private static final long MASK_SEQUENCE = (1L << LEN_SEQUENCE) - 1L;
	private static final AtomicLong sequence = new AtomicLong(0L);

	private IdGenerator() {
	}

	public static final long generate(final long shardId) {
		if ((shardId & MASK_SHARD_ID) != shardId) {
			throw new RuntimeException("Invalid shard ID... : " + shardId);
		}
		long timestamp = System.currentTimeMillis() - EPOCH;
		if ((timestamp & MASK_TIMESTAMP) != timestamp) {
			throw new RuntimeException("Invalid timestamp... : " + timestamp);
		}
		if (sequence.compareAndSet(MASK_SEQUENCE, 0)) {
			timestamp++;
		}
		long result = timestamp << SHIFT_TIMESTAMP;
		result |= (shardId << SHIFT_SHARD_ID);
		result |= sequence.getAndIncrement();
		return result;
	}

	public static final long getIdByTimestamp(final Date date) {
		long timestamp = date.getTime() - EPOCH;
		if ((timestamp & MASK_TIMESTAMP) != timestamp) {
			throw new RuntimeException("Invalid timestamp... : " + timestamp);
		}
		long result = timestamp << SHIFT_TIMESTAMP;
		return result;
	}

}
