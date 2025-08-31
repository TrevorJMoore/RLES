package com.rles.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;
import com.rles.simulator.telemetry.Streamer;
import com.rles.simulator.telemetry.Transport;

public class Simulator {
	public static enum OutputMode { STREAM, LOG, BOTH }
	
	private final SimulatorContext context;
	private final Streamer streamer;
	private final Transport transport;
	private final OutputMode outputMode;
	
	private final List<RegisteredSensor> sensors = new ArrayList<RegisteredSensor>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Math.max(1,  Runtime.getRuntime().availableProcessors() / 4));
	private final List<ScheduledFuture<?>> scheduled = new ArrayList<ScheduledFuture<?>>();
	
	private volatile boolean running;
	
	public Simulator(SimulatorContext context, Streamer streamer, Transport transport) {
		this.context = context;
		this.streamer = streamer;
		this.transport = transport;
		this.outputMode = OutputMode.STREAM;
	}
	
	public Simulator(SimulatorContext context, Streamer streamer, Transport transport, OutputMode mode) {
		this.context = context;
		this.streamer = streamer;
		this.transport = transport;
		this.outputMode = mode;
	}
	
	public void registerSensor(Sensor sensor, long periodMs) {
		sensors.add(new RegisteredSensor(sensor, Math.max((long)1, periodMs)));
	}
	
	public void registerSensors(Map<Sensor, Long> list) {
		for (Map.Entry<Sensor, Long> entry : list.entrySet()) {
			registerSensor(entry.getKey(), entry.getValue());
		}
	}
	
	// Connect to transport and start stream (if needed), then schedule all sensors
	public synchronized void start() throws IOException {
		if (running) return;
		
		if (outputMode == OutputMode.STREAM || outputMode == OutputMode.BOTH) {
			transport.connect();
			streamer.start();
		}
		
		
		// Schedule all sensors through ScheduledExecutorService
		for (RegisteredSensor rs : sensors) {
			ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
				try {
					SensorReading r = rs.sensor.generateAndRecord();
					if (r != null) {
						context.publish(r);
						
						// LOG
						if (outputMode == OutputMode.LOG || outputMode == OutputMode.BOTH) {
							System.out.printf(
								"[Log] %s | Sensor %d | Seq %d | Time %d | Status=%s | Value=%.3f%s%n",
								rs.sensor.getSensorName(),
								r.getSensorId(),
								r.getSequence(),
								r.getTimestamp(),
								r.getReadingStatus(),
								r.getValue(),
								r.getUnitCodeSymbol()
							);
						}
						
						// STREAM
						if (outputMode == OutputMode.STREAM || outputMode == OutputMode.BOTH) {
							boolean enqueue = streamer.enqueue(r);
							if (!enqueue) {
								// Queue full. Maybe add logging here
							}
						}
					}
				} catch (Throwable t) {
					
				}
			}, (long)0, rs.periodMs, TimeUnit.MILLISECONDS);
			scheduled.add(future);
		}
		running = true;
	}
	
	// Stop everything (scheduling, streaming, and transporting)
	public synchronized void stop() {
		if (!running) return;
		for (ScheduledFuture<?> f : scheduled) {
			f.cancel(false);
		}
		scheduled.clear();
		scheduler.shutdown();
		try {
			scheduler.awaitTermination(2, TimeUnit.SECONDS);
		} 
		catch (InterruptedException ex) {
			
		}
		if (outputMode == OutputMode.STREAM || outputMode == OutputMode.BOTH) {
			streamer.stop();
			transport.close();
		}
		running = false;
	}
	
	public SimulatorContext getContext() {
		return context;
	}	
	
	
	
	private static final class RegisteredSensor {
		final Sensor sensor;
		final long periodMs;
		
		RegisteredSensor(Sensor sensor, long periodMs) {
			this.sensor = sensor;
			this.periodMs = periodMs;
		}
	}
	
}