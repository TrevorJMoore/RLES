package com.rles.simulator.sensors.environment;

import com.rles.simulator.SimulatorContext;
import com.rles.simulator.enums.DataType;
import com.rles.simulator.enums.ReadingStatus;
import com.rles.simulator.enums.UnitCode;
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
	private static final double FAULT_THRESHOLD = 100.0;
	
	// State
	private final ReadingGenerator gen;
	private Double lastValue = null;
	private int sequence = 0;
	private final SimulatorContext context;
	private final Integer tempSensorId;
	private final Integer dewpointSensorId;
	
	// Constructors
	public AmbientHumiditySensor(int sensorId, String sensorName, int unitCode) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator();
		this.context = null;
		this.tempSensorId = null;
		this.dewpointSensorId = null;
	}
	
	public AmbientHumiditySensor(int sensorId, String sensorName, int unitCode, SimulatorContext context, Integer tempSensorId, Integer dewpointSensorId) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator();
		this.context = context;
		this.tempSensorId = tempSensorId;
		this.dewpointSensorId = dewpointSensorId;
	}
	
	public AmbientHumiditySensor(int sensorId, String sensorName, int unitCode, SimulatorContext context, Integer tempSensorId, Integer dewpointSensorId, long seed) {
		super(sensorId, sensorName, DataType.FLOAT, unitCode);
		this.gen = new ReadingGenerator(seed);
		this.context = context;
		this.tempSensorId = tempSensorId;
		this.dewpointSensorId = dewpointSensorId;
	}
		

	// Generate our AmbientHumidity reading using the simulator context
	@Override
	public SensorReading generateReading() {
		double value;
		if (tempSensorId == null || dewpointSensorId == null || context == null) {
			double base;
			if (lastValue == null) {
				base = gen.uniform(10.0, 15.0);
			} else {
				base = gen.randomWalkClamped(lastValue,  MAX_STEP,  MIN,  MAX);
				base += gen.gaussian(0, NOISE_SIGMA);
			}
			value = gen.quantize(base, GRAIN);
			lastValue = value;
		}
		else {
			double temp = context.getLatest(tempSensorId).getValue();
			double dewpoint = context.getLatest(dewpointSensorId).getValue();
			
			// These calculations come from the University of Miami: https://bmcnoldy.earth.miami.edu/Humidity.html
			// where they use approximations of Magnus coefficients in their relative humidity equation
			double expDew = (17.625*dewpoint)/(243.04+dewpoint);
			double expTemp = (17.625*temp)/(243.04+temp);
			double percentHumidity = Math.pow(Math.E, expDew)/Math.pow(Math.E,expTemp);
			value = 100*percentHumidity;
			
			
		}
		ReadingStatus status = ReadingStatus.ON;
		if (value >= WARN_THRESHOLD) status = ReadingStatus.WARN;
		if (value >= FAULT_THRESHOLD) status = ReadingStatus.FAULT;	
		
		UnitCode unitcode = UnitCode.PERCENT;
		
		return new SensorReading(getSensorId(), System.currentTimeMillis(), sequence++, value, unitcode , status);
		
	}

}
