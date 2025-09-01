package com.rles.simulator.sensors.environment;

import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.enums.UnitCode;
import com.rles.simulator.sensors.ReadingGenerator;
import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;

public class WindDirectionSensor extends Sensor {

	// CONFIG
	private static final double MIN = 0.0; // Degrees (N-0/360, E-90, S-180, W-270)
	private static final double MAX = 360.0; // Degrees
	private static final double GRAIN = 1;	// Round to whole degree
	private static final double MAX_STEP = 5;
	private static final double NOISE_SIGMA = 0.2;
	
	// State
	private final ReadingGenerator gen;
	private Double lastValue = null;
	private int sequence = 0;
	
	public WindDirectionSensor(int sensorId, String sensorName) {
		super(sensorId, sensorName, DataType.FLOAT);
		gen = new ReadingGenerator();
	}

	@Override
	public SensorReading generateReading() {
		double base;
		if (lastValue == null) {
			base = gen.uniform(0.0,360.0);
		} else {
			base = gen.randomWalk(lastValue, MAX_STEP);
			base += gen.gaussian(0, NOISE_SIGMA);
		}
		double value = gen.quantize(base, GRAIN);
		
		// circular wrap
		if (value < MIN) value += MAX;	// If < 0, add 360
		if (value >= MAX) value -= MAX; // If >= 360, subtract 360
		lastValue = value;
		
		return new SensorReading(getSensorId(), System.currentTimeMillis(), sequence++, value, UnitCode.DEGREE, ReadingStatus.ON);
	}

}
