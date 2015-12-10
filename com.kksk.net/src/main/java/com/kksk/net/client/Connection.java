package com.kksk.net.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kksk.execution.EventTarget;
import com.kksk.identify.IdGenerator;

public abstract class Connection implements Closeable {
	private static AtomicBoolean initialized = new AtomicBoolean(false);
	public static int SEND_QUEUE_SIZE = 100;
	public static int RECEIVE_QUEUE_SIZE = 100;

	public Connection(InetAddress inetAddress, int port) throws IOException {
		synchronized (initialized) {
			if (!initialized.get()) {
				initialize(SEND_QUEUE_SIZE, RECEIVE_QUEUE_SIZE);
			}
		}
		receiver = createReceiver(inetAddress, port);
		sender = createSender(inetAddress, port);
	}

	private static EventTarget sendEventTarget = null;
	private static BlockingQueue<SendEvent> sendEventPool;
	private static BlockingQueue<SendEventListener> sendEventListenerPool;
	private static EventTarget receiveEventTarget = null;
	private static BlockingQueue<ReceiveEvent> receiveEventPool;
	private static BlockingQueue<ReceiveEventListener> receiveEventListenerPool;

	private static void initialize(int sendQueueSize, int receiveQueueSize) {
		sendEventTarget = new EventTarget(sendQueueSize);
		sendEventPool = new ArrayBlockingQueue<>(sendQueueSize);
		sendEventListenerPool = new ArrayBlockingQueue<>(sendQueueSize);
		SendEvent[] sendEvents = new SendEvent[sendQueueSize];
		SendEventListener[] sendEventListeners = new SendEventListener[sendQueueSize];
		for (int i = 0; i < sendQueueSize; i++) {
			sendEvents[i] = new SendEvent();
			sendEventListeners[i] = new SendEventListener();
		}
		sendEventPool.addAll(Arrays.asList(sendEvents));
		sendEventListenerPool.addAll(Arrays.asList(sendEventListeners));
		receiveEventTarget = new EventTarget(receiveQueueSize);
		receiveEventPool = new ArrayBlockingQueue<>(receiveQueueSize);
		receiveEventListenerPool = new ArrayBlockingQueue<>(receiveQueueSize);
		ReceiveEvent[] receiveEvents = new ReceiveEvent[receiveQueueSize];
		ReceiveEventListener[] receiveEventListeners = new ReceiveEventListener[receiveQueueSize];
		for (int i = 0; i < receiveQueueSize; i++) {
			receiveEvents[i] = new ReceiveEvent();
			receiveEventListeners[i] = new ReceiveEventListener();
		}
		receiveEventPool.addAll(Arrays.asList(receiveEvents));
		receiveEventListenerPool.addAll(Arrays.asList(receiveEventListeners));
		initialized.set(true);
	}

	private Receiver receiver;

	protected abstract Receiver createReceiver(InetAddress inetAddress, int port) throws IOException;

	protected Receiver getReceiver() {
		return receiver;
	}

	private Sender sender;

	protected abstract Sender createSender(InetAddress inetAddress, int port) throws IOException;

	protected Sender getSender() {
		return sender;
	}

	protected void retry(SendEvent receiveEvent) {
		sendEventTarget.fire(receiveEvent);
	}

	protected void retry(ReceiveEvent receiveEvent) {
		receiveEventTarget.fire(receiveEvent);
	}

	private void finish(SendEventListener sendEventListener, SendEvent receiveEvent) {
		try {
			sendEventPool.put(receiveEvent);
			sendEventListenerPool.put(sendEventListener);
		} catch (InterruptedException e) {
		}
	}

	private void finish(ReceiveEventListener receiveEventListener, ReceiveEvent receiveEvent) {
		try {
			receiveEventPool.put(receiveEvent);
			receiveEventListenerPool.put(receiveEventListener);
		} catch (InterruptedException e) {
		}
	}

	private SendEventListener getSendEventListener() {
		try {
			SendEventListener sendEventListener = sendEventListenerPool.take();
			return sendEventListener;
		} catch (InterruptedException e) {
			return null;
		}
	}

	private ReceiveEventListener getReceiveEventListener(SendEvent sendEvent) {
		try {
			ReceiveEventListener receiveEventListener = receiveEventListenerPool.take();
			receiveEventListener.setSendEvent(sendEvent);
			return receiveEventListener;
		} catch (InterruptedException e) {
			return null;
		}
	}

	private byte[] send(byte[] data, boolean sync, boolean receive) {

		try {
			long id = IdGenerator.generate(0);
			SendEvent sendEvent = sendEventPool.take();
			sendEvent.setId(id);
			sendEvent.setConnection(this);
			sendEvent.setData(data);
			sendEvent.setWithReceive(receive);
			sendEvent.setWithSync(sync);
			SendEventListener sendEventListener = getSendEventListener();
			if (sendEventListener == null)
				return null;
			ReceiveEvent receiveEvent = null;
			ReceiveEventListener receiveEventListener = null;
			if (receive) {
				receiveEvent = receiveEventPool.take();
				receiveEvent.setId(id);
				receiveEvent.setConnection(this);
				receiveEventListener = getReceiveEventListener(sendEvent);
				if (receiveEventListener == null)
					return null;
				receiveEventTarget.addListener(receiveEventListener);
				receiveEventTarget.fire(receiveEvent);
			}
			sendEventTarget.addListener(sendEventListener);
			sendEventTarget.fire(sendEvent);
			if (sync) {
				synchronized (sendEvent.lock) {
					sendEvent.lock.wait();
				}
			}
			byte[] result = null;
			if (receive) {
				result = receiveEventListener.getData();
				finish(receiveEventListener, receiveEvent);
			}
			finish(sendEventListener, sendEvent);
			return result;
		} catch (InterruptedException e) {
			return null;
		}

	}

	public byte[] requestReply(byte[] data) {
		return send(data, true, true);
	}

	public void request(byte[] data) {
		send(data, true, false);
	}

	public void requestAsync(byte[] data) {
		send(data, false, false);
	}
}
