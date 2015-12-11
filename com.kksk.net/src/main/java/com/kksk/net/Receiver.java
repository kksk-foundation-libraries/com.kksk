package com.kksk.net;

import java.io.IOException;

public interface Receiver {
	boolean received(long id) throws IOException;

	byte[] receive(long id) throws IOException;
}
