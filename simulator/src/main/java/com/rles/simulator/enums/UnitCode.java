package com.rles.simulator.enums;

// Represent unit codes as integers for byte frames
public enum UnitCode {
	NONE(0, ""),
	PERCENT(1, "%"),
	CELSIUS(2, "C"),
	FAHRENHEIT(3, "F"),
	KELVIN(4, "K"),
	DEGREE(5, "Degree"),
	METER(6, "m"),
	METER_PER_SECOND(7, "m/s"),
	AMP(8, "A"),
	VOLT(9, "V"),
	PASCAL(10, "Pa"),
	CUBIC_METER_PER_SECOND(11, "m3/s"),
	KILOGRAM_PER_SECOND(12, "kg/s"),
	BOOLEAN(13, "bool");
	
	private final int code;
	private final String symbol;
	
	UnitCode(int code, String symbol) {
		this.code = code;
		this.symbol = symbol;
	}
	
	public int getCode() { return code; }
	public String getSymbol() { return symbol; }
	
	public static UnitCode getUnitCode(int code) {
		for (UnitCode u : values()) {
			if (u.code == code) return u;
		}
		return UnitCode.NONE;	// If we can't find the code, default as a unit code of NONE
	}
	
	
	
}
