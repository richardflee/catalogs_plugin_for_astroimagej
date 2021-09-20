package com.github.richardflee.astroimagej.enums;

import java.util.Arrays;

/**
 * Maps enums to text markers to delimit data sections in radec text files.
 *
 */
public enum RaDecFilesEnum {
	DATA_TABLE_START("#\n#*** DATA_TABLE_START\n"), DATA_TABLE_END("#*** DATA_TABLE_END\n"),
	QUERY_DATA_LINE("#\n#*** QUERY_DATA_LINE\n"), CHART_URI_LINE("#\n#*** CHART_URI_LINE\n");

	private String strVal = null;

	RaDecFilesEnum(String strVal) {
		this.strVal = strVal;
	}
	
	public String getStrVal() {
		return this.strVal;
	}

	public static void main(String[] args) {
		// assign enum strVal to lineMarkers array
		String[] lineMarkers = new String[RaDecFilesEnum.values().length];
		int counter = 0;
		for (RaDecFilesEnum en : RaDecFilesEnum.values()) {
			lineMarkers[counter++] = en.strVal;
		}
		
		// match enum to a line marker
		for (int idx = 0; idx < lineMarkers.length; idx++) {
			String marker = lineMarkers[idx];
			Arrays.stream(RaDecFilesEnum.values()).filter(p -> marker.contains(p.toString()))
					.forEach(p -> System.out.println(String.format("Matched: %s => %s", p.strVal, p.toString())));
		}
	}
}
