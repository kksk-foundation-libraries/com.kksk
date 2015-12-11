package com.kksk.net;

import java.io.IOException;
import java.net.InetAddress;

import com.kksk.net.client.Connection;
import com.kksk.net.client.socket.SocketConnection;

public final class Client {
	private Client() {
	}

	public static enum ConnectionType {
		SOCKET
	}

	private static final ConnectionType DEFAULT = ConnectionType.SOCKET;

	public static Connection open(InetAddress inetAddress, int port) throws IOException {
		return open(DEFAULT, inetAddress, port);
	}

	public static Connection open(ConnectionType connectionType, InetAddress inetAddress, int port) throws IOException {
		switch (connectionType) {
		case SOCKET:
			return new SocketConnection(inetAddress, port);
		default:
			return null;
		}
	}
}
