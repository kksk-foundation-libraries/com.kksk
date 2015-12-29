package com.kksk.net.client;

public final class ReceiveMessageHandler {

	public ReceiveMessageHandler() {
	}

	private long id;
	private byte[] data;
	protected final Object lock = new Object();

	public void set(long id) {
		this.id = id;
		this.data = null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void handle(Connection connection, long id, byte[] data) {
		if (this.id == id) {
			setData(data);
			synchronized (lock) {
				lock.notify();
			}
		}
	}

}
