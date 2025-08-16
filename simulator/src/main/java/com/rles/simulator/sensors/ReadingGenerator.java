package com.rles.simulator.sensors;

import java.util.Random;

/*
 * ReadingGenerator - Used for generating sensor readings based on the measurement type.
 * 
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