package com.rles.simulator.sensors.gse;

import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.enums.UnitCode;
import com.rles.simulator.sensors.ReadingGenerator;
import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;

public class VoltageSensor extends Sensor {

	// CONFIG
	private static final double AVG = 31.0; // Expected voltage reading
	private static final double GRAIN = 0.01;
	private static final double MAX_STEP = 0.05;
	private static final double NOISE_SIGMA = 0.2; // With sigma of 0.2 we expect 99% of values around 0.6
	
	// Thresholds
	private static final double LOW_WARN_THRESHOLD = 30.0;
	private static final double HIGH_WARN_THRESHOLD = 32.0;
	private static final double LOW_FAULT_THRESHOLD = 27.5;
	private static final double HIGH_FAULT_THRESHOLD = 35.0;
	
	// State
	private final ReadingGenerator gen;
	private Double lastValue = null;
	private int sequence = 0;
	
	public VoltageSensor(int sensorId, String sensorName) {
		super(sensorId, sensorName, DataType.FLOAT);
		this.gen = new ReadingGenerator();
	}

	// Generate a reading that closely hovers MAX volts.
	@Override
	public SensorReading generateReading() {
		// We expect the voltage sensor to read AVG
		double base;
		if (lastValue == null) {
			base = gen.uniform(AVG-1, AVG+1);
		} else {
			// Random walk CLOSE to AVG
			base = gen.randomWalkClamped(lastValue, MAX_STEP, AVG-NOISE_SIGMA, AVG+NOISE_SIGMA);
			// Shift calculation with noise
			base += gen.gaussian(0, NOISE_SIGMA);
		}
		double value = gen.quantize(base, GRAIN);
		lastValue = value;
		
		ReadingStatus status = ReadingStatus.ON;
		if (value >= HIGH_WARN_THRESHOLD || value <= LOW_WARN_THRESHOLD) status = ReadingStatus.WARN;
		if (value >= HIGH_FAULT_THRESHOLD || value <= LOW_FAULT_THRESHOLD) status = ReadingStatus.FAULT;
		return new SensorReading(getSensorId(), System.currentTimeMillis(), sequence++, value, UnitCode.VOLT, status);
	}

}
