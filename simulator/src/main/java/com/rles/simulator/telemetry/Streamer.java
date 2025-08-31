package com.rles.simulator.telemetry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import com.rles.simulator.sensors.SensorReading;

// Queue's, batches, and sends readings to Encoder and Transport
public class Streamer {
	private final Encoder encoder;
	private final Transport transport;
	private final BlockingQueue<SensorReading> queue;
	
	private final int batchMax;
	private final long batchWindowMs;
	
	private Thread worker;
	private volatile boolean running;
	
	public Streamer(Encoder encoder, Transport transport, int capacity) {
		this(encoder, transport, capacity, 64, 20);
	}
	
	public Streamer(Encoder encoder, Transport transport, int capacity, int batchMax, long batchWindowMs) {
		this.encoder = encoder;
		this.transport = transport;
		this.queue = new ArrayBlockingQueue<SensorReading>(capacity);
		this.batchMax = Math.max(1,  batchMax);
		this.batchWindowMs = Math.max(1, batchWindowMs);
	}
	
	
	// Insert a SensorReading into the queue - return T/F if successful/unsuccessful
	public boolean enqueue(SensorReading reading) {
		return queue.offer(reading);
	}
	
	// Starts a worker thread to help batch and send queue'd SensorReadings
	public synchronized void start() {
		if (running) return;
		running = true;
		worker = new Thread(this::runLoop, "streamer");
		worker.setDaemon(true);
		worker.start();
	}
	
	// Close the worker thread connection and further breakdown
	public synchronized void stop() {
		if (!running) return;
		running = false;
		if (worker != null) {
			worker.interrupt();
			try { 
				worker.join(2000); 
			} catch (InterruptedException e) {}
			worker = null;
		}
		try {
			drainAndSendRemaining();
		} catch (IOException ignore) {}
		try {
			transport.flush();
		} catch (IOException e) {}
	}
	
	private void runLoop() {
		List<SensorReading> batch = new ArrayList<SensorReading>(batchMax);
		try {
			while (running) {
				SensorReading first = queue.poll(batchWindowMs, TimeUnit.MILLISECONDS);
				if (first == null) {
					continue;
				}
				batch.clear();
				batch.add(first);
				
				queue.drainTo(batch, batchMax - 1);
				
				byte[] payload = (batch.size() == 1) ? encoder.encode(first) : encoder.encodeBatch(batch);
				transport.send(payload);
				transport.flush();
			}
		} catch (InterruptedException e) {
			
		} catch (IOException e) {
			running = false;
		}
	}
	
	private void drainAndSendRemaining() throws IOException {
		List<SensorReading> left = new ArrayList<SensorReading>(queue.size());
		queue.drainTo(left);
		if (!left.isEmpty()) {
			byte[] payload = (left.size() == 1) ? encoder.encode(left.get(0)) : encoder.encodeBatch(left);
			transport.send(payload);
		}
	}
	
}
