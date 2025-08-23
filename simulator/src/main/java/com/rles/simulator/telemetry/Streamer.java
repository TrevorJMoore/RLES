package com.rles.simulator.telemetry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import com.rles.simulator.sensors.SensorReading;

// Queue's, batches, and sends readings to Encoder and Transport
public class Streamer {
	private final Encoder encoder;
	private final Transport transport;
	private final BlockingQueue<SensorReading> queue;
	
	public Streamer(Encoder encoder, Transport transport, int capacity) {
		this.encoder = encoder;
		this.transport = transport;
		this.queue = new ArrayBlockingQueue<SensorReading>(capacity);
	}
	
	
	// Insert a SensorReading into the queue - return T/F if successful/unsuccessful
	public boolean enqueue(SensorReading reading) {
		return queue.offer(reading);
	}
	
	// Starts a worker thread to help batch and send queue'd SensorReadings
	public void start() {
		
	}
	
	// Close the transport layer's connection and further breakdown
	public void stop() {
		
	}
	
}
