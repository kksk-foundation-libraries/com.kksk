package com.kksk.net.server;

public class Task {
	public static enum TaskType {
		SPIN(0), HANDSHAKE(1), RECEIVE(2), SEND(3);

		public final int priority;

		private TaskType(int priority) {
			this.priority = priority;
		}
	}

	public TaskType type;
	public Connection connection;
	public long messageId;
	public byte[] data;

	public void set(TaskType type, Connection connection, long messageId, byte[] data) {
		this.type = type;
		this.connection = connection;
		this.messageId = messageId;
		this.data = data;
	}
}
