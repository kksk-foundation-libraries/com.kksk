package com.kksk.net.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.kksk.net.client.Connection;
import com.kksk.net.client.Receiver;
import com.kksk.net.client.Sender;

public class SocketConnection extends Connection {
	private Socket receiverSocket;
	private Socket senderSocket;

	public SocketConnection(InetAddress inetAddress, int port) throws IOException {
		super(inetAddress, port);
	}

	@Override
	protected Receiver createReceiver(InetAddress inetAddress, int port) throws IOException {
		receiverSocket = new Socket(inetAddress, port);
		return new SocketReceiver(receiverSocket);
	}

	@Override
	protected Sender createSender(InetAddress inetAddress, int port) throws IOException {
		senderSocket = new Socket(inetAddress, port);
		return new SocketSender(senderSocket);
	}

	@Override
	public void close() throws IOException {
		IOException ioException = null;
		try {
			receiverSocket.close();
		} catch (IOException e) {
			if (ioException == null)
				ioException = e;
		}
		try {
			senderSocket.close();
		} catch (IOException e) {
			if (ioException == null)
				ioException = e;
		}
		if (ioException != null)
			throw ioException;
	}

}
