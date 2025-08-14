package com.rles.simulator.sensors.gse;

import com.rles.simulator.enums.DataType;
import com.rles.simulator.sensors.Sensor;
import com.rles.simulator.sensors.SensorReading;

public class HydraulicPressureSensor extends Sensor {

	public HydraulicPressureSensor(int sensorId, String sensorName, DataType measurementType, int unitCode) {
		super(sensorId, sensorName, measurementType, unitCode);
		// TODO
	}

	@Override
	public SensorReading generateReading() {
		// TODO
		return null;
	}

}
