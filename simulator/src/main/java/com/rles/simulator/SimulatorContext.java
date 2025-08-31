package com.rles.simulator;

import java.time.Clock;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rles.simulator.sensors.SensorReading;

/*	SimulatorContext - Stores sensor readings, provides time/units, and lets any sensors derived of other sensor readings read those readings.
 * 	Should be used for derived computations and in-sim decisions. Ex: AmbientHumiditySensor using temp and dew point readings.
 * 
 */
public class SimulatorContext {
	
	private final Clock clock;
	private final Map<Integer, SensorReading> latestById = new ConcurrentHashMap<>();
	
	public SimulatorContext() {
		this(Clock.systemUTC());
	}
	
	public SimulatorContext(Clock clock) {
		this.clock = clock;
	}
	
	public long nowMillis() {
		return clock.millis();
	}
	
	public void publish(SensorReading reading) {
		if (reading != null) {
			latestById.put(reading.getSensorId(), reading);
		}
	}
	
	public SensorReading getLatest(int sensorId) {
		return latestById.get(sensorId);
	}
	
	public Collection<SensorReading> getAllLatest() {
		return latestById.values();
	}
	
}
