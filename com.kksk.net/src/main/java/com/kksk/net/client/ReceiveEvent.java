package com.kksk.net.client;

import com.kksk.execution.Event;

public class ReceiveEvent implements Event {
	private long id;
	private Connection connection;

	public ReceiveEvent() {
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
}
