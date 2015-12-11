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

	public static Connection open(InetAddress inetAddress, int sendPort, int receivePort) throws IOException {
		return open(DEFAULT, inetAddress, sendPort, receivePort);
	}

	public static Connection open(ConnectionType connectionType, InetAddress inetAddress, int sendPort, int receivePort) throws IOException {
		switch (connectionType) {
		case SOCKET:
			return new SocketConnection(inetAddress, sendPort, receivePort);
		default:
			return null;
		}
	}
}
