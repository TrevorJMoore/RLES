package com.rles.simulator.sensors;

import java.util.ArrayList;
import java.util.List;

import com.rles.simulator.datastructures.CircularBuffer;
import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.DeviceStatus;
import com.rles.simulator.enums.UnitCode;

// This class will represent the base class for all sensors.
public abstract class Sensor {

	private final int sensorId;	
	private String sensorName;
	private final DataType measurementType;
	private DeviceStatus deviceStatus;
	private final CircularBuffer<SensorReading> history;
	// TODO: Move magic numbers into config.
	private static final int HISTORY_CAPACITY = 256;
	
	// Constructor uses default capacity for buffer
	public Sensor(int sensorId, String sensorName, DataType measurementType) {
		this(sensorId, sensorName, measurementType, HISTORY_CAPACITY);
	}
	
	// Constructor uses parameter capacity for buffer
	public Sensor(int sensorId, String sensorName, DataType measurementType, int historyCapacity) {
		this.sensorId = sensorId;
		this.sensorName = sensorName;
		this.measurementType = measurementType;
		this.deviceStatus = DeviceStatus.ACTIVE;
		this.history = new CircularBuffer<SensorReading>(historyCapacity);
	}
	
	
	// Accessor methods
	public int getSensorId() { return sensorId; }
	public String getSensorName() { return sensorName; }
	public DataType getMeasurementType() { return measurementType; }
	public DeviceStatus getDeviceStatus() { return deviceStatus; }
	
	// Mutator methods
	public void setSensorName(String sensorName) { this.sensorName = sensorName; }
	public void setDeviceStatus(DeviceStatus deviceStatus) { this.deviceStatus = deviceStatus; }
	
	
	// Circular buffer helper methods
	protected void record(SensorReading reading) {
		history.add(reading);
	}
	
	public SensorReading getLatestReading() {
		return history.isEmpty() ? null : history.getNewest(0);
	}
	
	public List<SensorReading> getNRecent(int n) {
		int k = Math.min(n, history.size());
		List<SensorReading> out = new ArrayList<SensorReading>(k);
		for (int i = 0; i < k; i++) {
			out.add(history.getNewest(i));
		}
		return out;
	}
	
	public List<SensorReading> getFullBuffer() {
		int k = history.size();
		List<SensorReading> out = new ArrayList<SensorReading>(k);
		for (int i = 0; i < k; i++) {
			out.add(history.getOldest(i));
		}
		return out;
	}
	
	// A simple generate then record method
	public final SensorReading generateAndRecord() {
		SensorReading r = generateReading();
		record(r);
		// Return the SensorReading just in case caller needs it.
		return r;
	}
	
	
	// Every subclass must implement a generateReading method.
	public abstract SensorReading generateReading();
	
}