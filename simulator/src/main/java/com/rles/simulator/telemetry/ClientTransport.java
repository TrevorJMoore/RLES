package com.rles.simulator.telemetry;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

// CLIENT Transport - Pushes frames to a specific server (DEFAULT: 127.0.0.1)
public class ClientTransport implements Transport {
	
	private final String host;
	private final int port;
	private final int timeoutMs;
	private final int bufferSize;
	
	private Socket socket;
	private BufferedOutputStream stream;
	private volatile boolean connected;

	// Default Constructor - localhost:7000, 5000ms timeout, 16kb buff
	public ClientTransport() {
		this("127.0.0.1", 7000, 5000, 16 * 1024);
	}
	
	// 5000ms timeout, 16kb buff
	public ClientTransport(String host, int port) {
		this(host, port, 5000, 16 * 1024);
	}
	
	public ClientTransport(String host, int port, int timeoutMs, int bufferSize) {
		this.host = host;
		this.port = port;
		this.timeoutMs = timeoutMs;
		this.bufferSize = bufferSize;
	}
	
	// Establish a connection
	public synchronized void connect() throws IOException {
		if (connected) return;
		// Create and connect to socket
		socket = new Socket();
		socket.connect(new InetSocketAddress(host,port), timeoutMs);
		socket.setTcpNoDelay(true);
		stream = new BufferedOutputStream(socket.getOutputStream(), bufferSize);
		connected = true;
	}
	
	// Send a frame of bytes to the destination
	public synchronized void send(byte[] frame) throws IOException {
		ensureConnected(); 
		// If connected, write frame to stream
		stream.write(frame);
	}
	
	// Force write any in-buffer data
	public synchronized void flush() throws IOException {
		if (!connected) return;
		stream.flush();
	}
	
	// Close the connection
	public synchronized void close() {
		try {
			// If we have a current stream, try flushing and closing
			if (stream != null) {
				try { 
					stream.flush(); 
				} catch (IOException e) {}
				try {
					stream.close();
				} catch (IOException e) {}
			}
		} finally {
			// Set stream and socket to null
			stream = null;
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
			socket = null;
			connected = false;
		}
	}
	
	private void ensureConnected() throws IOException {
		if (!connected || socket == null || stream == null) {
			throw new IOException("Transport layer not connected");
		}
	}
	
}
