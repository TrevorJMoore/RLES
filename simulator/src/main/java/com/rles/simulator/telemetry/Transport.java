package com.rles.simulator.telemetry;

import java.io.IOException;

public interface Transport {
	void connect() throws IOException;
	void send(byte[] frame) throws IOException;
	void flush() throws IOException;
	void close();
}
