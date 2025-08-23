package com.rles.simulator.telemetry;

import java.util.List;
import com.rles.simulator.sensors.SensorReading;

// Turn SensorReading objects into bytes
public class Encoder {

	private static final String VERSION = "0.0.1";
	
	public Encoder() {
		
	}
	
	// Encode a single SensorReading
	// Sensor Reading Schema:
	// sensorId - Integer
	// timestamp - Long
	// sequence - Integer
	// value - Double
	// status - Byte (ReadingStatus code)
	public byte[] encode(SensorReading reading) {
		return new byte[0];
	}
	
	// Encode multiple SensorReadings
	public byte[] encodeBatch(List<SensorReading> batch) {
		return new byte[0];
	}
	
	// Get current version of the frame schema
	public String getVersion() {
		return this.VERSION;
	}
	
}
