package com.kksk.net.client;

public class Message {
	private Connection connection;
	private boolean isSendOnly;
	private long messageId;
	private byte[] data;

	public Message() {
	}

	public void set(Connection connection, boolean isSendOnly, long messageId, byte[] data) {
		this.connection = connection;
		this.isSendOnly = isSendOnly;
		this.messageId = messageId;
		this.data = data;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isSendOnly() {
		return isSendOnly;
	}

	public void setSendOnly(boolean isSendOnly) {
		this.isSendOnly = isSendOnly;
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
