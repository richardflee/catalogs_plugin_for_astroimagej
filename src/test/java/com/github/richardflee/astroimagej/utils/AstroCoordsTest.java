package com.github.richardflee.astroimagej.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.richardflee.astroimagej.enums.QueryEnum;

class AstroCoordsTest {
	
	public static final double epsilon = 1e-5;

	@DisplayName("Verify ra conversion from numeric (hr) to sexagesimal format in range 0.0 to 23:59:59.99")
	@ParameterizedTest
	@CsvSource({
		"00:00:00.00,    0.0",
		"12:20:44.41, 	12.34567",		// baseline
		"23:59:59.99, 	23.999996",		// max ra
	})
	void testRaHr_To_RaHms_IsCorrect(String raHms, Double raHr) {
		assertEquals(raHms, AstroCoords.raHr_To_raHms(raHr));
	}
	
	@DisplayName("Verify dec conversion from numeric (deg) to sexagesimal format in range ±90:00:00.00")
	@ParameterizedTest
	@CsvSource({
		"+00:00:00.00,    0.0",
		"+12:20:44.41, 	 12.34567",		// baseline dec > 0
		"-12:20:44.41, 	-12.34567",		// baseline dec < 0
		"+89:59:59.99, 	 89.999996",	
		"-89:59:59.99, 	-89.999996",	
		"+90:00:00.00, 	 90.000000",	// dec limit ±90
		"-90:00:00.00, 	-90.000000",		
	})
	void testDecDeg_To_DecDms_IsCorrect(String decDms, Double decDeg) {
		assertEquals(decDms, AstroCoords.decDeg_To_decDms(decDeg));
	}
	
	@DisplayName("Verify ra conversion from sexagesimal to numeric (hr) in range 0.0 to 23:59:59.99")
	@ParameterizedTest
	@CsvSource({
		"00.00000,      00:00:00.00",
		"12.34567,		12:20:44.412",		// baseline
		"23.999996,		23:59:59.986", 		// max ra
	})
	void testRaHms_To_RaHr_IsCorrect(Double raHr, String raHms) {
		assertEquals(raHr, AstroCoords.raHms_To_raHr(raHms), epsilon);
	}
	

	@DisplayName("Verify dec conversion from sexagesimal to numeric (deg) in range ±90:00:00.00")
	@ParameterizedTest
	@CsvSource({
		"0.0,           +00:00:00.00",
		"12.34567,		+12:20:44.41",		// baseline dec > 0
		"-12.34567,		-12:20:44.41",		// baseline dec < 0
		"89.999996,		+89:59:59.99",	
		"-89.999996,	-89:59:59.99",
		"90.000000,		+90:00:00.00",
		"-90.000000,	-90:00:00.00",	 	
	})
	void testDecDms_To_DecDeg_IsCorrect(Double decDeg, String decDms) {
		assertEquals(decDeg, AstroCoords.decDms_To_decDeg(decDms), epsilon);
	}
	
	@DisplayName("Verify formatting of raHms input")
	@ParameterizedTest
	@CsvSource({
		"01:02:03.45,   1:2:3.454",
		"01:02:03.46,   1:2:3.455",
		"01:02:03.45,   1: 2: 3.454"
	})
	void testUserFormat_ToSexagemsimal_RaHms_IsCorrect(String formattedRaHms, String input) {
		assertEquals(formattedRaHms, AstroCoords.sexagesimalFormatter(input, QueryEnum.RA_HMS));
	}
	
	@DisplayName("Verify formatting of decDms input")	
	@ParameterizedTest
	@CsvSource({
		"+01:02:03.45,   +1:2:3.454",
		"-01:02:03.46,   -1:2:3.455",
		"+01:02:03.45,   1: 2: 3.454",
		"-01:02:03.45,   -1: 2: 3.454"
	})
	void testUserFormat_ToSexagemsimal_DecDms_IsCorrect(String formattedRaHms, String input) {
		assertEquals(formattedRaHms, AstroCoords.sexagesimalFormatter(input, QueryEnum.DEC_DMS));
	}

}

