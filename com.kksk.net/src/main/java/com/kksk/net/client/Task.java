package com.kksk.net.client;

public class Task {
	public static enum TaskType {
		SPIN(0), RECEIVE(2), SEND(3);

		public final int priority;

		private TaskType(int priority) {
			this.priority = priority;
		}
	}

	public TaskType type;
	public Connection connection;

	public void set(TaskType type, Connection connection) {
		this.type = type;
		this.connection = connection;
	}
}
