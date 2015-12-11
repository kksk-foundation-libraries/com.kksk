package com.kksk.net;

import java.io.IOException;

public interface Sender {
	void send(long id, byte[] data) throws IOException;
}
