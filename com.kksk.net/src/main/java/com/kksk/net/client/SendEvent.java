package com.kksk.net.client;

import com.kksk.execution.Event;

public class SendEvent implements Event {
	private long id;
	private Connection connection;
	private boolean withReceive;
	private boolean withSync;
	private byte[] data;
	public final Object lock = new Object();

	public SendEvent() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isWithReceive() {
		return withReceive;
	}

	public void setWithReceive(boolean withReceive) {
		this.withReceive = withReceive;
	}

	public boolean isWithSync() {
		return withSync;
	}

	public void setWithSync(boolean withSync) {
		this.withSync = withSync;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
