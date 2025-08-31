package com.rles.simulator.enums;

// Represent unit codes as integers for byte frames
public enum UnitCode {
	NONE((byte)0, ""),
	PERCENT((byte)1, "%"),
	CELSIUS((byte)2, "C"),
	FAHRENHEIT((byte)3, "F"),
	KELVIN((byte)4, "K"),
	DEGREE((byte)5, "Degree"),
	METER((byte)6, "m"),
	METER_PER_SECOND((byte)7, "m/s"),
	AMP((byte)8, "A"),
	VOLT((byte)9, "V"),
	PASCAL((byte)10, "Pa"),
	CUBIC_METER_PER_SECOND((byte)11, "m3/s"),
	KILOGRAM_PER_SECOND((byte)12, "kg/s"),
	BOOLEAN((byte)13, "bool");
	
	private final byte code;
	private final String symbol;
	
	UnitCode(byte code, String symbol) {
		this.code = code;
		this.symbol = symbol;
	}
	
	public byte getCode() { return code; }
	public String getSymbol() { return symbol; }
	
	public static UnitCode getUnitCode(byte code) {
		for (UnitCode u : values()) {
			if (u.code == code) return u;
		}
		return UnitCode.NONE;	// If we can't find the code, default as a unit code of NONE
	}
	
	
	
}
