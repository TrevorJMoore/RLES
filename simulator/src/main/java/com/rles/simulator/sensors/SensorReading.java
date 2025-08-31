package com.rles.simulator.sensors;

import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.enums.UnitCode;

// Represents a reading from a sensor
public class SensorReading{

	private final int sensorId;
	private final long timestamp;
	private final int sequence;
	private final double value;
	private final UnitCode unitCode;
	private final ReadingStatus readingStatus;
	
	public SensorReading(int sensorId, long timestamp, int sequence, double value, UnitCode unitCode, ReadingStatus readingStatus) {
		this.sensorId = sensorId;
		this.timestamp = timestamp;
		this.sequence = sequence;
		this.value = value;
		this.unitCode = unitCode;
		this.readingStatus = readingStatus;
	}
	
	// Accessor Methods
	public int getSensorId() { return sensorId;	}
	public long getTimestamp() { return timestamp; }
	public int getSequence() { return sequence; }
	public double getValue() { return value; }
	public UnitCode getUnitCode() { return unitCode; }
	public byte getUnitCodeByte() { return unitCode.getCode(); }
	public String getUnitCodeSymbol() { return unitCode.getSymbol(); }
	public ReadingStatus getReadingStatus() { return readingStatus; }
	public byte getStatusByte() { return readingStatus.getCode(); }
	
}
