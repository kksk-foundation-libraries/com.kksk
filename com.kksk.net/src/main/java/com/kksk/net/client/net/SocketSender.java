package com.kksk.net.client.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.kksk.net.client.Sender;

public class SocketSender implements Sender {
	private final Socket socket;
	private byte[] buffer = new byte[Long.BYTES + Integer.BYTES];
	private ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES + Integer.BYTES);

	public SocketSender(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void send(long id, byte[] data) throws IOException {
		byteBuffer.position(0).clear().flip();
		byteBuffer.putLong(id);
		byteBuffer.putInt(data.length);
		byteBuffer.position(0);
		byteBuffer.get(buffer);
		OutputStream os = socket.getOutputStream();
		os.write(buffer);
		os.write(data);
		os.flush();
	}

}
