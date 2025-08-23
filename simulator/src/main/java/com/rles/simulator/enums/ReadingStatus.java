package com.rles.simulator.enums;

/* Reading Status:
 * ON - Sensor is reporting valid, active reading
 * OFF - The reading is from when the sensor was not active or turned off
 * STALE - The reading is out-dated - no new data has been received within an expected time frame
 * WARN - The reading is valid, but some parameter is in a warning threshold
 * FAULT - The reading is invalid due to malfunction or parameter beyond warning threshold.
 */
public enum ReadingStatus {
	ON((byte)0),
	OFF((byte)1),
	STALE((byte)2),
	WARN((byte)3),
	FAULT((byte)4);

	private final byte code;
	
	ReadingStatus(byte code) {
		this.code = code;
	}
	
	public byte getCode() { return code; }
	
	public static ReadingStatus getStatus(byte code) {
		for (ReadingStatus s : values()) {
			if (s.code == code) return s;
		}
		return ReadingStatus.FAULT; // If we can't find the code, return it as a FAULT status
	}
	
}
