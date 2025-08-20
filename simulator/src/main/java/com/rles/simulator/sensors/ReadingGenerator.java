package com.rles.simulator.sensors;

import java.util.Random;

/*
 * ReadingGenerator - Used for generating sensor readings based on the measurement type.
 * Think of this related more so to the simulator than to either the Sensor or SensorReading classes.
 * By pushing for a class separate from the SensorReading class, it keeps the SensorReading just for describing what should be in a sensor's data.
 */

public class ReadingGenerator {

	// Fields
	private final Random generator;
	
	
	// Constructors
	public ReadingGenerator() {
		this.generator = new Random();
	}
	
	// Preferred way of instantiation for "deterministic" outputs
	public ReadingGenerator(long seed) {
		this.generator = new Random(seed);
	}
	
	
	
	// Return a random number between min and max with equal probability
	public double uniform(double min, double max) {
		return min + generator.nextDouble() * (max - min);
	}
	
	// Box-Muller transform (random number into normal random)
	// z = sqrt(-2ln(u1))*cos(2*pi*u2)
	public double gaussian(double mean, double sigma) {
		// Generate our 2 random u1 and u2
		// Avoid taking the log of 0 (undefined) use a clamp of 1e^-15
		double u1 = Math.max(1e-15,  generator.nextDouble());
		double u2 = Math.max(1e-15, generator.nextDouble());
		// Box-Muller transform
		double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
		
		// Shift center of distribution using mean and spread using standard deviation
		return mean + sigma * z;
	}
	
	// Random-walk
	public double randomWalkClamped(double prev, double maxStep, double min, double max) {
		// Step from prev with maxStep
		double step = (generator.nextDouble() * 2.0 - 1.0) * maxStep;
		double v = prev + step;
		
		// Clamp
		if (v < min) v = min;
		if (v > max) v = max;
		
		return v;
	}
	
	// Quantize to grain
	// grain = 0 -> no snapping
	// grain = 0.1 - allow one decimal place
	// grain = 0.5 - allow halves
	// grain = 1 - allow whole numbers
	// quantize(49.24, 0.5) -> 49.0
	public double quantize(double value, double grain) {
		if (grain <= 0) return value;
		return Math.round(value / grain) * grain;
	}
	
	// Flip a coin with probability 'p'
	public boolean coinFlip(double p) {
		if (p <= 0) return false;	// Our probability is <=0%
		if (p >= 1) return true;	// Our probability is >=100%
		return generator.nextDouble() < p;	// Coin flip
	}
	
	
	// Methods
	public double generateValue(double min, double max, double grain) {
		return generator.nextDouble();
	}
	
}