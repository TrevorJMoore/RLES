package com.rles.simulator.sensors;

import java.util.List;
import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.DeviceStatus;

// This class will represent the base class for all sensors.
public abstract class Sensor {

	private final int sensorId;	
	private String sensorName;
	private DataType measurementType;
	private int unitCode;
	private DeviceStatus deviceStatus;
	private List<SensorReading> history;
	
	public Sensor(int sensorId, String sensorName, DataType measurementType, int unitCode) {
		this.sensorId = sensorId;
		this.sensorName = sensorName;
		this.measurementType = measurementType;
		this.unitCode = unitCode;
		this.deviceStatus = DeviceStatus.ACTIVE;
		
	}
	
	// Accessor methods
	public int getSensorId() { return sensorId; }
	public String getSensorName() { return sensorName; }
	public DataType getMeasurementType() { return measurementType; }
	public int getUnitCode() { return unitCode; }
	public DeviceStatus getDeviceStatus() { return deviceStatus; }
	public List<SensorReading> getHistory() { return history; }
	
	// Mutator methods
	public void setSensorName(String sensorName) { this.sensorName = sensorName; }
	public void setMeasurementType(DataType measurementType) { this.measurementType = measurementType; }
	public void setUnitCode(int unitCode) { this.unitCode = unitCode; }
	public void setDeviceStatus(DeviceStatus deviceStatus) { this.deviceStatus = deviceStatus; }
	public void setHistory(List<SensorReading> history) { this.history = history; }
	
	// Every subclass must implement a generateReading method.
	public abstract SensorReading generateReading();
	
}