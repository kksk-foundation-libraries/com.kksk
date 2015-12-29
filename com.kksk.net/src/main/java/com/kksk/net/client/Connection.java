package com.kksk.net.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import com.kksk.net.BinaryTranslator;

public class Connection implements Closeable {
	private static final long BIT_MASK = (1L << 31) - 1;
	private static final long INT_MASK = (1L << 32) - 1;
	private final Socket socket;
	private final InputStream is;
	private final OutputStream os;
	protected final long id;
	private static final AtomicLong sequence = new AtomicLong();
	private static final long epoch;

	static {
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 0, 1, 0, 0, 0);
		epoch = cal.getTimeInMillis();
	}

	public static long generateId() {
		long localId = 0;
		long tm = System.currentTimeMillis() - epoch;
		if (sequence.compareAndSet(Integer.MAX_VALUE, 0)) {
			tm += 1;
		}
		localId = (tm & BIT_MASK) << 32;
		localId |= (sequence.getAndIncrement() & INT_MASK);
		return localId;
	}

	protected Connection(Socket socket) throws IOException {
		this.socket = socket;
		is = socket.getInputStream();
		os = socket.getOutputStream();
		this.id = generateId();
	}

	private byte[] handshakeHeader = new byte[Long.BYTES];

	protected void handshake() throws IOException {
		byte[] bytes = handshakeHeader;
		BinaryTranslator.setBytes(bytes, 0, Long.BYTES, this.id);
		os.write(bytes);
		os.flush();
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
			message.set(this, false, messageId, data);
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
