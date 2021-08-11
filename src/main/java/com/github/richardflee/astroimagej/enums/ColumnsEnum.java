package com.github.richardflee.astroimagej.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** 
 * Defines catalog table column index and width
 */
public enum ColumnsEnum {
	AP_COL       ("0.4"), 		// format column index, percent column width
	OBJECTID_COL ("1.22"),
	RA2000_COL   ("2.14"), 
	DEC2000_COL  ("3.14"), 
	MAG_COL      ("4.9"),
	MAG_ERR_COL  ("5.9"),
	MAG_DIFF_COL ("6.9"), 
	DIST_AMIN_COL("7.9"), 
	NOBS_COL     ("8.6"), 
	USE_COL      ("9.4");

	private final String strVal;
	private static final Map<String, ColumnsEnum> map =  new HashMap<>();
	private static int totalWidth = 0;
	
    
	ColumnsEnum(String value) {
		this.strVal = value;
	}

	/**
	 * Table column index
	 * @return table column index 
	 */
	public int getIndex() {
		String[] splitStr = strVal.split("\\.");
		return Integer.valueOf(splitStr[0]);
	}
	
	/**
	 * Table column width
	 * @return percentage column width
	 */
	public int getWidth() {	
		String[] splitStr = strVal.split("\\.");
		return Integer.valueOf(splitStr[1]);
	}
	
	/**
	 * SSum of table column width
	 * @return total table width
	 */
	public static int getTotalWidth() {		
		return totalWidth;
	}
	
	/**
	 * Maps value of column index to associated enum
	 * @param columnIndex table column index
	 * @return enum value for this column
	 */
	public static ColumnsEnum getEnum(int columnIndex) {
		return map.get(String.valueOf(columnIndex));
	}
	
	// initialise look-up map and sum column widths
	static {
		for (final ColumnsEnum en : ColumnsEnum.values()) {
			map.put(en.strVal.substring(0, 1), en);
			totalWidth += en.getWidth();
		}
	}
	
	/**
	 * Total number of enum items
	 */
	public static final int size;
	static {
		size = values().length;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(String.format("AP_COL value = %s", ColumnsEnum.AP_COL.strVal ));
		System.out.println(String.format("AP_COL index, width = %d, %d", ColumnsEnum.AP_COL.getIndex(), ColumnsEnum.AP_COL.getWidth()));
		System.out.println(String.format("Enum for column index %d is %s", 
											ColumnsEnum.AP_COL.getIndex(), getEnum(ColumnsEnum.AP_COL.getIndex()).toString() ));
		System.out.println();
		
		System.out.println(String.format("RA2000_COL value = %s", ColumnsEnum.RA2000_COL.strVal ));
		System.out.println(String.format("RA2000_COL index, width = %d, %d", ColumnsEnum.RA2000_COL.getIndex(), ColumnsEnum.RA2000_COL.getWidth()));
		System.out.println(String.format("Enum for column index %d is %s", 
				ColumnsEnum.RA2000_COL.getIndex(), getEnum(ColumnsEnum.RA2000_COL.getIndex()).toString() ));
		System.out.println();
		
		System.out.println(String.format("No enum items = %02d", ColumnsEnum.size));
		System.out.println(String.format("Total width = %02d", ColumnsEnum.getTotalWidth()));
	}

}
