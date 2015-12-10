package com.kksk.net.client;

import java.io.IOException;

public interface Receiver {
	boolean received(long id) throws IOException;

	byte[] receive(long id) throws IOException;
}
