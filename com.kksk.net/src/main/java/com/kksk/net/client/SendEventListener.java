package com.kksk.net.client;

import static com.kksk.assertion.Assert.assertTrue;

import java.io.IOException;

import com.kksk.execution.Event;
import com.kksk.execution.EventListener;
import com.kksk.net.Sender;

public class SendEventListener implements EventListener {

	@Override
	public void handleEvent(Event event) {
		assertTrue(event instanceof SendEvent);
		SendEvent sendEvent = (SendEvent) event;
		Connection connection = sendEvent.getConnection();
		Sender sender = connection.getSender();
		try {
			sender.send(sendEvent.getId(), sendEvent.getData());
		} catch (IOException e) {
		}
		if (!sendEvent.isWithReceive() && sendEvent.isWithSync()) {
			synchronized (sendEvent.lock) {
				sendEvent.lock.notify();
			}
		}
	}

}
