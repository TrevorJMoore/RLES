package com.rles.simulator.telemetry;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// SERVER Transport - Streams frames to all connected clients
public class ServerTransport implements Transport{
	
	private final int port;
	private final int bufferSize;
	
	private ServerSocket server;
	private Thread acceptor;
	private volatile boolean running;
	
	private final Set<Client> clients = Collections.synchronizedSet(new HashSet<>());
	
	public ServerTransport() { 
		this(7000, 16*1024); 
	}
	
	public ServerTransport(int port) { 
		this(port, 16*1024);
	}
	
	public ServerTransport(int port, int bufferSize) {
		this.port = port;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public synchronized void connect() throws IOException {
		if (running) return;
		server = new ServerSocket(port);
		running = true;
		acceptor = new Thread(this::acceptLoop, "server-transport-acceptor");
		acceptor.setDaemon(true);
		acceptor.start();
		System.out.println("[ServerTransport] Listening on: " + port);
	}
	
	private void acceptLoop() {
		while (running) {
			try {
				Socket s = server.accept();
				s.setTcpNoDelay(true);
				Client c = new Client(s, new BufferedOutputStream(s.getOutputStream(), bufferSize));
				clients.add(c);
				System.out.println("[ServerTransport] Client connected: " + s.getRemoteSocketAddress());
			} catch (IOException e) {
				if (running) {
					System.err.println("[ServerTransport] Accept error: " + e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void send(byte[] frame) throws IOException {
		synchronized (clients) {
			clients.removeIf(client -> {
				try {
					client.stream.write(frame);
					return false;
				} catch (IOException e) {
					dropClient(client);
					return true;
				}
			});
		}
	}
	
	@Override
	public void flush() throws IOException {
		synchronized (clients) {
			clients.removeIf(client -> {
				try {
					client.stream.flush();
					return false;
				} catch (IOException e) {
					dropClient(client);
					return true;
				}
			});
		}
	}
	
	// Teardown server transport
	@Override
	public synchronized void close() {
		running = false;
		try {
			if (server != null)
				server.close();
		} catch (IOException e) {}
		if (acceptor != null) {
			try {
				acceptor.join(1000);
			} catch (InterruptedException e) {}
		}
		synchronized (clients) {
			for (Client client : clients)
				forceClose(client);
			clients.clear();
		}
		server = null;
		acceptor = null;
		System.out.println("[ServerTransport] Terminated.");
	}
	
	private void dropClient(Client client) {
		System.err.println("[ServerTransport] Dropping client: " + client.sock.getRemoteSocketAddress());
		forceClose(client);
	}
	
	private void forceClose(Client client) {
		try {
			client.stream.flush();
		} catch (IOException e) {}
		try {
			client.stream.close();
		} catch (IOException e) {}
		try {
			client.sock.close();
		} catch (IOException e) {}
	}
	
	
	private static final class Client {
		final Socket sock;
		final BufferedOutputStream stream;
		Client(Socket s, BufferedOutputStream stream) {
			this.sock = s;
			this.stream = stream;
		}
	}
	
}
