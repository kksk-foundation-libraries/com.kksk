package com.kksk.net.server;

public interface MessageHandler {
	void handle(Server server, Connection connection, long id, byte[] data);
}
