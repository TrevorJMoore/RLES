package com.rles.simulator.sensors;

import java.util.Random;

/*
 * ReadingGenerator - Used for generating sensor readings based on the measurement type.
 * Think of this related more so to the simulator than to either the Sensor or SensorReading classes.
 * By pushing for a class separate from the SensorReading class, it keeps the SensorReading just for describing what should be in a sensor's data.
 * The ReadingGenerator will create different generations based on measurement types.
 * I don't think this warrants a Strategy design pattern as I believe I can get away with just using one method with a parameter. (Subject to change I might use Strategy...)
 */

public class ReadingGenerator {

	// Fields
	private final Random generator;
	
	
	// Constructors
	public ReadingGenerator() {
		this.generator = new Random();
	}
	
	// Preferred way of instantiation for deterministic outputs
	public ReadingGenerator(long seed) {
		this.generator = new Random(seed);
	}
	
	
	// Methods
	public double generateValue(double min, double max, double grain) {
		return generator.nextDouble();
	}
	
}