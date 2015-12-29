package com.kksk.net.server;

public class Message {
	private Connection connection;
	private long messageId;
	private byte[] data;

	public Message() {
	}

	public void set(Connection connection, long messageId, byte[] data) {
		this.connection = connection;
		this.messageId = messageId;
		this.data = data;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
