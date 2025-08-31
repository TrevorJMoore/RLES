package com.rles.simulator.telemetry;

import java.util.List;
import java.util.zip.CRC32;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.rles.simulator.sensors.SensorReading;

// Turn SensorReading objects into bytes
public class Encoder {
	
	private static final String VERSION = "0.1";

	private static final byte VERSION_BYTE = 0x01;
	private static final short MAGIC = (short)0xAA55;
	private static final byte FLAGS = 0x00;
	
	private static final int HEADER_SIZE = 8; // 2b-magic, 1b-version, 1b-flags, 4b-length
	private static final int DATA_SIZE = 25; //4b-id, 8b-timestamp, 4b-sequence, 8b-value, 1b-status
	private static final int CRC_SIZE = 4;
	private static final int FRAME_SIZE = HEADER_SIZE + DATA_SIZE + CRC_SIZE;
	
	public Encoder() {
		
	}
	
	// Encode a single SensorReading
	// Sensor Reading Schema:
	// sensorId - Integer
	// timestamp - Long
	// sequence - Integer
	// value - Double
	// status - Byte (ReadingStatus code)
	public byte[] encode(SensorReading reading) {
		ByteBuffer buf = ByteBuffer.allocate(FRAME_SIZE).order(ByteOrder.LITTLE_ENDIAN);
		
		// Frame Creation
		// Header
		buf.putShort(MAGIC);
		buf.put(VERSION_BYTE);
		buf.put(FLAGS);
		buf.putInt(DATA_SIZE);
		
		// Data
		buf.putInt(reading.getSensorId());
		buf.putLong(reading.getTimestamp());
		buf.putInt(reading.getSequence());
		buf.putDouble(reading.getValue());
		buf.put(reading.getStatusCode());
		
		// Cyclic Redundancy Check
		byte[] arr = buf.array();
		// We don't include magic
		int crcStart = 2;
		// (HEADER + DATA) - magic
		int crcLen = HEADER_SIZE + DATA_SIZE - crcStart;
		
		CRC32 crc = new CRC32();
		crc.update(arr, crcStart, crcLen);
		long crcVal = crc.getValue();
		
		// Append the CRC
		buf.putInt((int) crcVal);
		
		return arr;
		
	}
	
	// Encode multiple SensorReadings
	public byte[] encodeBatch(List<SensorReading> batch) {
		if (batch == null || batch.isEmpty()) {
			return new byte[0];
		}
		
		// Compute the total size
		int total = FRAME_SIZE * batch.size();
		
		// Add each reading to the buffer
		ByteBuffer buf = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN);
		for (SensorReading r : batch) {
			buf.put(encode(r));
		}
		return buf.array();
	}
	
	// Get current version of the frame schema
	public String getVersion() {
		return Encoder.VERSION;
	}
	
}
