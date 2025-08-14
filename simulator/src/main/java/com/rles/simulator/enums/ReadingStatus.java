package com.rles.simulator.enums;

/* Reading Status:
 * ON - Sensor is reporting valid, active reading
 * OFF - The reading is from when the sensor was not active or turned off
 * STALE - The reading is out-dated - no new data has been received within an expected time frame
 * WARN - The reading is valid, but some parameter is in a warning threshold
 * FAULT - The reading is invalid due to malfunction or parameter beyond warning threshold.
 */
public enum ReadingStatus {
	ON,
	OFF,
	STALE,
	WARN,
	FAULT
}
