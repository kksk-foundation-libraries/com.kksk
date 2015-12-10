package com.kksk.net.client.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.kksk.net.client.Receiver;

public class SocketReceiver implements Receiver {
	private final Socket socket;
	private final Map<Long, byte[]> received;
	private byte[] buffer = new byte[Long.BYTES + Integer.BYTES];
	private ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES + Integer.BYTES);

	public SocketReceiver(Socket socket) {
		this.socket = socket;
		received = new HashMap<>();
	}

	@Override
	public boolean received(long id) throws IOException {
		InputStream is = socket.getInputStream();
		if (is.available() >= Long.BYTES + Integer.BYTES) {
			is.read(buffer);
			byteBuffer.position(0).clear().flip();
			byteBuffer.put(buffer);
			byteBuffer.position(0);
			long receivedId = byteBuffer.asLongBuffer().get();
			int length = byteBuffer.asIntBuffer().get();
			while (is.available() < length) {
			}
			byte[] data = new byte[length];
			is.read(data);
			received.put(receivedId, data);
		}
		return received.containsKey(id);
	}

	@Override
	public byte[] receive(long id) {
		return received.remove(id);
	}

}
