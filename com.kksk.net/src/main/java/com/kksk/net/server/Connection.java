package com.kksk.net.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.kksk.net.BinaryTranslator;

public class Connection implements Closeable {
	private Socket socket;
	public long client = -1L;
	private InputStream is;
	private OutputStream os;

	protected Connection(Socket socket) throws IOException {
		this.socket = socket;
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}

	private byte[] handshakeHeader = new byte[Long.BYTES];

	protected boolean handshake() throws IOException {
		byte[] bytes = handshakeHeader;
		if (is.available() >= bytes.length) {
			is.read(bytes);
			client = BinaryTranslator.toLong(bytes);
			return true;
		} else {
			return false;
		}
	}

	private byte[] receiveHeader = new byte[Long.BYTES + Integer.BYTES];

	protected boolean receive(Message message) throws IOException {
		byte[] bytes = receiveHeader;
		int av = is.available();
		if (av >= bytes.length) {
			is.read(bytes);
			long messageId = BinaryTranslator.toLong(bytes, 0, Long.BYTES);
			int length = BinaryTranslator.toInt(bytes, Long.BYTES, Integer.BYTES);
			byte[] data = new byte[length];
			while (is.available() < data.length) {
			}
			is.read(data);
			message.set(this, messageId, data);
			return true;
		} else {
			return false;
		}
	}

	private byte[] sendHeader = new byte[Long.BYTES + Integer.BYTES];

	protected void send(Message message) throws IOException {
		byte[] bytes = sendHeader;
		BinaryTranslator.setBytes(bytes, 0, Long.BYTES, message.getMessageId());
		BinaryTranslator.setBytes(bytes, Long.BYTES, Integer.BYTES, message.getData().length);
		os.write(bytes);
		os.write(message.getData());
		os.flush();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}
}
