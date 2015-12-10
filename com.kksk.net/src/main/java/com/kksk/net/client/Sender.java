package com.kksk.net.client;

import java.io.IOException;

public interface Sender {
	void send(long id, byte[] data) throws IOException;
}
