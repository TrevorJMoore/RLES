package com.rles.simulator.sensors.environment;

import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.enums.UnitCode;
import com.rles.simulator.sensors.ReadingGenerator;
import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;

public class DewPointSensor extends Sensor {
	
	// CONFIG
	private static final double MIN = -60.0; // Celsius
	private static final double MAX = 100.0; // Celsius
	private static final double GRAIN = 0.01;
	private static final double MAX_STEP = 0.01;
	private static final double NOISE_SIGMA = 0.2;
	private static final double WARN_THRESHOLD = 50.0;
	private static final double FAULT_THRESHOLD = 60.0;
		
	// State
	private final ReadingGenerator gen;
	private Double lastValue = null;
	private int sequence = 0;
	
	public DewPointSensor(int sensorId, String sensorName, int unitCode) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator();
	}
	
	@Override
	public SensorReading generateReading() {
		double base;
		if (lastValue == null) {
			base = gen.uniform(0.0, 5.0);
		} else {
			base = gen.randomWalkClamped(lastValue,  MAX_STEP,  MIN,  MAX);
			base += gen.gaussian(0, NOISE_SIGMA);
		}
		double value = gen.quantize(base, GRAIN);
		lastValue = value;
		
		ReadingStatus status = ReadingStatus.ON;
		if (value >= WARN_THRESHOLD) status = ReadingStatus.WARN;
		if (value >= FAULT_THRESHOLD) status = ReadingStatus.FAULT;
		
		UnitCode unitcode = UnitCode.CELSIUS;
		
		return new SensorReading(getSensorId(), System.currentTimeMillis(), sequence++, value, unitcode, status);
	}
	
}
