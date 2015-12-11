package com.kksk.net.client;

import static com.kksk.assertion.Assert.*;

import java.io.IOException;

import com.kksk.execution.Event;
import com.kksk.execution.EventListener;
import com.kksk.net.Receiver;

public class ReceiveEventListener implements EventListener {
	private byte[] data;

	private SendEvent sendEvent;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public SendEvent getSendEvent() {
		return sendEvent;
	}

	public void setSendEvent(SendEvent sendEvent) {
		this.sendEvent = sendEvent;
	}

	@Override
	public final void handleEvent(Event event) {
		assertTrue(event instanceof ReceiveEvent);
		ReceiveEvent receiveEvent = (ReceiveEvent) event;
		Connection connection = receiveEvent.getConnection();
		Receiver receiver = connection.getReceiver();
		try {
			if (receiver.received(receiveEvent.getId())) {
				data = receiver.receive(receiveEvent.getId());
				synchronized (sendEvent.lock) {
					sendEvent.lock.notify();
				}
			} else {
				connection.retry(receiveEvent);
			}
		} catch (IOException e) {
			// FIXME
		}
	}
}
