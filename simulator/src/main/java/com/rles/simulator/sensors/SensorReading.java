package com.rles.simulator.sensors;

import com.rles.simulator.enums.ReadingStatus;

// Represents a reading from a sensor
public class SensorReading{

	private final int sensorId;
	private final long timestamp;
	private final int sequence;
	private final double value;
	private ReadingStatus readingStatus;
	
	public SensorReading(int sensorId, long timestamp, int sequence, double value, ReadingStatus readingStatus) {
		this.sensorId = sensorId;
		this.timestamp = timestamp;
		this.sequence = sequence;
		this.value = value;
		this.readingStatus = readingStatus;
	}
	
	// Accessor Methods
	public int getSensorId() { return sensorId;	}
	public long getTimestamp() { return timestamp; }
	public int getSequence() { return sequence; }
	public double getValue() { return value; }
	public ReadingStatus getReadingStatus() { return readingStatus; }
	
}
