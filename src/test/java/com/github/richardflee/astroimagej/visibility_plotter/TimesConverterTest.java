package com.github.richardflee.astroimagej.visibility_plotter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.jfree.data.time.Minute;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;

class TimesConverterTest {

	public static final double TOL = 1e-6;

	// default royal obs greenwich, gwch civil time is (proxy for) utc
	private static ObservationSite gwchSite;
	private static TimesConverter tcGwch;

	// example W site moore obsy KY
	private static ObservationSite mooreSite;
	private static TimesConverter mooreTimesConverter;

	// example E site site Tbilisi, Georgia
	//private static ObservationSite tbilisiSite;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// default 51N 0W greenwich
		gwchSite = new ObservationSite();
		tcGwch = new TimesConverter(gwchSite);

		// moore 45N 85W
		double siteLong = AstroCoords.dmsToDeg("-85:31:42.51");
		double siteLat = AstroCoords.dmsToDeg("+38:20:41.25");
		double siteElevation = 100.0;
		double utcOffsetHr = -5.0;
		mooreSite = new ObservationSite(siteLong, siteLat, siteElevation, utcOffsetHr);
		mooreTimesConverter = new TimesConverter(mooreSite);
		
		

//		// tbilisi 45N 41E
//		siteLong = 41.71;
//		siteLat = 44.793;
//		siteElevation = 100.0;
//		tbilisiSite = new ObservationSite(siteLong, siteLat, siteElevation, 0.0);
	}

	@DisplayName("Verify conversion from gmt/utc to gst PA12")
	@Test
	void testConversionUtcToGst() {
		LocalDateTime utcDateTime = LocalDateTime.of(1980, 4, 22, 14, 36, 51, (int) 0.67e9);
		LocalDateTime gstDateTime = tcGwch.convertUtcToGst(utcDateTime);
		LocalTime gstTime = gstDateTime.toLocalTime();
		assertEquals("04:40:05.22", gstTime.toString().substring(0, 11));
	}

	@DisplayName("Verify conversion from gst to utc PA13")
	@Test
	void testConversionGstToUtc() {
		LocalDateTime gstDateTime = LocalDateTime.of(1980, 4, 22, 4, 40, 5, (int) 0.23e9);
		LocalDateTime utcDateTime = tcGwch.convertGstToUtc(gstDateTime);
		LocalTime utcTime = utcDateTime.toLocalTime();
		assertEquals("14:36:51.67", utcTime.toString().substring(0, 11));
	}

	@DisplayName("Verify conversion from gst to lst PA14")
	@Test
	void testConversionGstToLst() {
		double siteLong = -64.0;
		ObservationSite site = new ObservationSite(siteLong, 0.0, 0.0, 0.0);
		TimesConverter tc64W = new TimesConverter(site);

		LocalDateTime gstDateTime = LocalDateTime.of(2000, 1, 1, 4, 40, 5, (int) 0.23e9);
		LocalTime lstTime = tc64W.convertGstToLst(gstDateTime).toLocalTime();
		assertEquals("00:24:05.23", lstTime.toString().substring(0, 11));
	}

	@DisplayName("Verify conversion from lst to gst PA15")
	@Test
	void testConversionLstToGst() {
		double siteLong = -64.0;
		ObservationSite site = new ObservationSite(siteLong, 0.0, 0.0, 0.0);
		TimesConverter tc64W = new TimesConverter(site);

		LocalDateTime lstDateTime = LocalDateTime.of(2000, 1, 1, 0, 24, 5, (int) 0.23e9);
		LocalTime gstTime = tc64W.convertLstToGst(lstDateTime).toLocalTime();
		assertEquals("04:40:05.23", gstTime.toString().substring(0, 11));
	}

	@DisplayName("Verify conversion from utc to lst")
	@Test
	void testConversionUtcToLst() {
		TimesConverter mooreTimesConverter = new TimesConverter(mooreSite);
		
		LocalDateTime utcDateTime = LocalDateTime.of(2020, 2, 24, 2, 0, 46);
		LocalTime lstTime = mooreTimesConverter.convertUtcToLst(utcDateTime).toLocalTime();
		assertEquals("06:32:22", lstTime.toString().substring(0, 8));
	}

	@DisplayName("Verify conversion from lst to utc")
	@Test
	void testConversionLstToUtc() {
		TimesConverter mooreTimesConverter = new TimesConverter(mooreSite);
		
		LocalDateTime lstDateTime = LocalDateTime.of(2020, 2, 24, 6, 32, 22);
		LocalTime utcTime = mooreTimesConverter.convertLstToUtc(lstDateTime).toLocalTime();
		// add 1 sec to for time-stamp rounding
		utcTime = utcTime.plusSeconds(1);
		assertEquals("02:00:46", utcTime.toString().substring(0, 8));
	}
	
	
	@DisplayName("Verify epoch julian dates")
	@Test
	void testEpochDatesoJD() {

		assertEquals(2415020.00000, TimesConverter.JD_1900, TOL);
		assertEquals(2451545.00000, TimesConverter.JD_2000, TOL);
		assertEquals(2455196.50000, TimesConverter.JD_2010, TOL);
	}
	
	@DisplayName("Verify conversion frpm utc to JD PA04")
	@Test
	void testConversionFromUtcToJD() {
		LocalDateTime utcDateTime = LocalDateTime.of(2009, 06, 19, 18, 0, 0);
		assertEquals(2455002.250000, TimesConverter.convertUtcToJD(utcDateTime), TOL);
	}

	@DisplayName("Verify conversion from gmt/utc to 0 hr JD0")
	@Test
	void testConversionFromUtcToJD0() {
		LocalDateTime utcDateTime = LocalDateTime.of(2017, 8, 21, 0, 0, 0);
		assertEquals(2457986.500000, TimesConverter.convertUtcToJD(utcDateTime), TOL);
	}
	
	@DisplayName("Verify conversion from site civil time to utc mooreSite PA9")
	@Test
	void testConvertCivilDateTimeToUtc() {
		LocalDateTime siteCivilDateTime = LocalDateTime.of(1980, 4, 1, 1, 26, 51, (int) (0.67 * 1e9));
		
		LocalDateTime utcDateTime = mooreTimesConverter.convertCivilDateTimeToUtc(siteCivilDateTime);
		assertEquals("1980-04-01T06:26:51.67", 
				utcDateTime.toString().substring(0, "yyyy-mm-ddTHH:MM:SS.SS".length()));		
	}
	
	@DisplayName("Verify conversion from utc to site civil time to utc mooreSite PA10")
	@Test
	void testConvertUtcToCivilDateTime() {
		LocalDateTime utcDateTime = LocalDateTime.of(1980, 3, 31, 17, 56, 51, (int) (0.67 * 1e9));
		
		utcDateTime = LocalDateTime.of(1980, 4, 1, 7, 26, 51, (int) (0.67 * 1e9));
		LocalDateTime siteCivilDateTime = mooreTimesConverter.convertUtcToCivilDateTime(utcDateTime);
		assertEquals("1980-04-01T02:26:51.67", 
				siteCivilDateTime.toString().substring(0, "yyyy-mm-ddTHH:MM:SS.SS".length()));
	}
	
	@DisplayName("Verify forward and reverse conversion from civil date-time to JFree Minute")
	@Test
	void testConvertCivilTimeToMinute() {
		LocalDateTime utcDateTime = LocalDateTime.of(2020, 2, 24, 2, 1, 0);
		Minute current = TimesConverter.convertCivilDateTimeToMinute(utcDateTime);
		LocalDateTime ldt = TimesConverter.convertMinuteToCivilDateTime(current);
		assertEquals("2020-02-24T02:01", ldt.toString().substring(0, "yyyy-mm-ddTHH:MM".length()));
	}

}
