package com.rles.simulator;

import java.util.ArrayList;
import java.util.List;

import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.telemetry.Streamer;
import com.rles.simulator.telemetry.Transport;

public class Simulator {
	private final SimulatorContext context;
	private final Streamer streamer;
	private final Transport transport;
	
	private final List<RegisteredSensor> sensors = new ArrayList<RegisteredSensor>();
	private volatile boolean running;
	
	public Simulator(SimulatorContext context, Streamer streamer, Transport transport) {
		this.context = context;
		this.streamer = streamer;
		this.transport = transport;
	}
	
	public void registerSensor(Sensor sensor, long periodMs) {
		sensors.add(new RegisteredSensor(sensor, Math.max((long)1, periodMs)));
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