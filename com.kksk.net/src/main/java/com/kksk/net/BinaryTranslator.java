package com.kksk.net;

public final class BinaryTranslator {
	private BinaryTranslator() {
	}

	private static final long BYTE_MASK = (1 << Byte.SIZE) - 1;

	public static byte[] toBytes(byte input) {
		return new byte[] { input };
	}

	public static byte[] toBytes(short input) {
		byte[] result = new byte[Short.BYTES];
		for (int i = 0; i < result.length; i++) {
			result[result.length - i - 1] = (byte) ((input >> (i * Byte.SIZE)) & BYTE_MASK);
		}
		return result;
	}

	public static byte[] toBytes(int input) {
		byte[] result = new byte[Integer.BYTES];
		for (int i = 0; i < result.length; i++) {
			result[result.length - i - 1] = (byte) ((input >> (i * Byte.SIZE)) & BYTE_MASK);
		}
		return result;
	}

	public static byte[] toBytes(long input) {
		byte[] result = new byte[Long.BYTES];
		for (int i = 0; i < result.length; i++) {
			result[result.length - i - 1] = (byte) ((input >> (i * Byte.SIZE)) & BYTE_MASK);
		}
		return result;
	}

	public static void setBytes(byte[] output, byte input) {
		setBytes(output, 0, Byte.BYTES, input);
	}

	public static void setBytes(byte[] output, int off, int len, byte input) {
		for (int i = 0; i < len; i++) {
			output[i + off] = input;
		}
	}

	public static void setBytes(byte[] output, short input) {
		setBytes(output, 0, Short.BYTES, input);
	}

	public static void setBytes(byte[] output, int off, int len, short input) {
		for (int i = 0; i < len; i++) {
			output[i + off] = (byte) ((input >> ((Short.BYTES - i - 1) * Byte.SIZE)) & BYTE_MASK);
		}
	}

	public static void setBytes(byte[] output, int input) {
		setBytes(output, 0, Short.BYTES, input);
	}

	public static void setBytes(byte[] output, int off, int len, int input) {
		for (int i = 0; i < len; i++) {
			output[i + off] = (byte) ((input >> ((Integer.BYTES - i - 1) * Byte.SIZE)) & BYTE_MASK);
		}
	}

	public static void setBytes(byte[] output, long input) {
		setBytes(output, 0, Short.BYTES, input);
	}

	public static void setBytes(byte[] output, int off, int len, long input) {
		for (int i = 0; i < len; i++) {
			output[i + off] = (byte) ((input >> ((Long.BYTES - i - 1) * Byte.SIZE)) & BYTE_MASK);
		}
	}

	public static short toShort(byte[] input) {
		return toShort(input, 0, Math.min(input.length, Short.BYTES));
	}

	public static short toShort(byte[] input, int off, int len) {
		short result = 0;
		for (int i = 0; i < len; i++) {
			result = (short) ((result << Byte.SIZE) | (input[i + off] & BYTE_MASK));
		}
		return result;
	}

	public static int toInt(byte[] input) {
		return toInt(input, 0, Math.min(input.length, Integer.BYTES));
	}

	public static int toInt(byte[] input, int off, int len) {
		int result = 0;
		for (int i = 0; i < len; i++) {
			result = (result << Byte.SIZE) | (int) (input[i + off] & BYTE_MASK);
		}
		return result;
	}

	public static long toLong(byte[] input) {
		return toLong(input, 0, Math.min(input.length, Long.BYTES));
	}

	public static long toLong(byte[] input, int off, int len) {
		long result = 0L;
		for (int i = 0; i < len; i++) {
			result = (result << Byte.SIZE) | (input[i + off] & BYTE_MASK);
		}
		return result;
	}
}
