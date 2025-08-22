package com.rles.simulator.sensors.environment;

import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.sensors.ReadingGenerator;
import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;

/*	AmbientHumiditySensor - Generates relative humidity readings
 * 
 *	Relative humidity is a percentage between 0-100%.
 *	Relative humidity is reliant on ambient temperature and dewpoint.
 *	TODO: Link AmbientHumiditySensor to AmbientTemperatureSensor reading results through some context to generate meaningful readings.
 */
public class AmbientHumiditySensor extends Sensor {
	
	// Config options
	private static final double MIN = 0.0;
	private static final double MAX = 100.0;
	private static final double GRAIN = 0.1;
	private static final double MAX_STEP = 1.0;
	private static final double NOISE_SIGMA = 0.3;
	private static final double WARN_THRESHOLD = 90.0;
	private static final double FAULT_THRESHOLD = 98.0;
	
	// State
	private final ReadingGenerator gen;
	private Double lastValue = null;
	private int sequence = 0;
	
	// Constructors
	public AmbientHumiditySensor(int sensorId, String sensorName, int unitCode) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator();
	}
	
	public AmbientHumiditySensor(int sensorId, String sensorName, int unitCode, long seed) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator(seed);
	}
	

	// Generate our AmbientHumidity reading
	@Override
	public SensorReading generateReading() {
		long time = System.currentTimeMillis();
		
		// Pick a base (mid on first tick) and random-walk
		double base = (lastValue == null) ? 50.0 : lastValue;
		double v = gen.randomWalkClamped(base, MAX_STEP, MIN, MAX);
		
		// Add noise and clamp
		v += gen.gaussian(0.0, NOISE_SIGMA);
		if (v < MIN) v = MIN;
		if (v > MAX) v = MAX;
		
		// Quantize
		v = gen.quantize(v, GRAIN);
		
		ReadingStatus status;
		if (v >= FAULT_THRESHOLD) {
			status = ReadingStatus.FAULT;
		}
		else if (v >= WARN_THRESHOLD) {
			status = ReadingStatus.WARN;
		}
		else if (v == lastValue) {
			status = ReadingStatus.STALE;
		}
		else {
			status = ReadingStatus.ON;
		}
		
		lastValue = v;
		int seq = ++sequence;
		return new SensorReading(getSensorId(), time, seq, v, status);
	}

}
