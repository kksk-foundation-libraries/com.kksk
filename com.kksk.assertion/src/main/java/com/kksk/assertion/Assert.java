package com.kksk.assertion;

public final class Assert {
	private Assert() {
	}

	public static void assertTrue(boolean condition) {
		if (!condition) {
			throw new AssertionException(getExceptionLine(2) + " argument is not true.");
		}
	}

	public static void assertFalse(boolean condition) {
		if (condition) {
			throw new AssertionException(getExceptionLine(2) + " argument is not false.");
		}
	}

	public static void assertNull(Object object) {
		if (null != object) {
			throw new AssertionException(getExceptionLine(2) + " argument is not null.");
		}
	}

	public static void assertNonNull(Object object) {
		if (null == object) {
			throw new AssertionException(getExceptionLine(2) + " argument is null.");
		}
	}

	public static <T> void assertGreater(Comparable<T> lhs, T rhs) {
		if (lhs.compareTo(rhs) <= 0) {
			throw new AssertionException(getExceptionLine(2) + " argument1 is not greater than argument2.");
		}
	}

	public static <T> void assertGreaterEqual(Comparable<T> lhs, T rhs) {
		if (lhs.compareTo(rhs) < 0) {
			throw new AssertionException(getExceptionLine(2) + " argument1 is not greater than or equals to argument2.");
		}
	}

	public static <T> void assertLess(Comparable<T> lhs, T rhs) {
		if (lhs.compareTo(rhs) >= 0) {
			throw new AssertionException(getExceptionLine(2) + " argument1 is not less than argument2.");
		}
	}

	public static <T> void assertLessEqual(Comparable<T> lhs, T rhs) {
		if (lhs.compareTo(rhs) > 0) {
			throw new AssertionException(getExceptionLine(2) + " argument1 is not less than or equals to argument2.");
		}
	}

	public static <T> void assertEqual(Comparable<T> lhs, T rhs) {
		if (lhs.compareTo(rhs) != 0) {
			throw new AssertionException(getExceptionLine(2) + " argument1 is not equals to argument2.");
		}
	}

	private static String getExceptionLine(int level) {
		Throwable throwable = new Throwable();
		StackTraceElement stackTraceElement = throwable.getStackTrace()[level];
		return stackTraceElement.toString();
	}
}
