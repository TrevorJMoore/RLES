package com.rles.simulator.sensors;
// This class will represent the base class for all sensors.
// Future note: primitive data type sizes in java are of fixed size (unlike c). So feel comfortable using a stream on these types.
import java.util.Random;




public abstract class Sensor {
	
	// Unique numeric ID
	protected int sensorId;
	protected String unit;
	protected Random random;
	
	
}