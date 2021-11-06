package com.github.richardflee.astroimagej.visibility_plotter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.visibility_plotter.CoordsConverter.CoordsEnum;

class SolarTimesTest {

	public static final double TOL = 1e-6;
	// comparing times to 1 sec
	public static final double SECOND_TOL = 1.0 / 3600.0;
	public static final double MINUTE_TOL = 60.0 * SECOND_TOL;

	private static ObservationSite bostonSite = null;
//	private static ObservationSite gwchSite = null;
//	private static ObservationSite polarSite = null;
//	private static ObservationSite sidingSite = null;
	
	// private static SolarTimes solarTimes = null;
	

	double t2010;

//	@BeforeAll
//	static void setUpBeforeClass() throws Exception {
//		double siteLong = -71.05; // 71.05W
//		double siteLat = 42.37; // 42.37N
//		double siteElevation = 0.0;
//		bostonSite = new ObservationSite(siteLong, siteLat, siteElevation, -5.0);
//		
//		double siteLongDeg = 0.0;
//		double siteLatDeg = 52.0; // 42.37N
//		gwchSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, 0.0);
//		
//		siteLongDeg = 0.0;
//		siteLatDeg = 82.0; // 82N
//		polarSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, 0.0);
//		
//		siteLongDeg = AstroCoords.dmsToDeg("149:03:42");
//		siteLatDeg = -1.0 * AstroCoords.dmsToDeg("31:16:24");   // 31S
//		sidingSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, 11.0);
//	}


	@BeforeEach
	void setUp() throws Exception {
		LocalDateTime epoch2010 = LocalDateTime.of(2009, 12, 31, 0, 0, 0);
		double jd = TimesConverter.convertUtcToJD(epoch2010);
		t2010 = (jd - TimesConverter.JD_1900) / TimesConverter.C_DAYS;
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@DisplayName("Verify conversion ecliptic to equatorial coordinates PA27")
	@Test
	void testConvertEclipticToEquatorial() {
		LocalDate localDate = LocalDate.of(2009, 7, 6);
		double ecpLongDeg = AstroCoords.dmsToDeg("139:41:10");
		double ecpLatDeg = AstroCoords.dmsToDeg("4:52:31");
		Map<CoordsEnum, Double> map = Solar.convertEclipticToEquatorial(ecpLongDeg, ecpLatDeg, localDate);
		
		assertEquals(9.581478, map.get(CoordsConverter.CoordsEnum.RA_HR), TOL);
		assertEquals(19.535003, map.get(CoordsConverter.CoordsEnum.DEC_DEG), TOL);
	}
	
	@DisplayName("Verify eccentricity (e) Sun-Earth orbit at epoch 2010.0 PA46")
	@Test
	void testOrbitEccentricity() {
		double ecc = Solar.orbitEccentricity(t2010);
		assertEquals(0.016705, ecc, TOL);
	}

	@DisplayName("Verify mean ecliptic longitude (eg) at epoch 2010.0 PA46")
	@Test
	void testEclipticLongDeg() {
		double eg = Solar.eclipticLongDeg(t2010);
		assertEquals(279.557208, eg, TOL);
	}

	@DisplayName("Verify perigee (wg) at epoch 2010.0 PA46")
	@Test
	void testPerigeeLongDeg() {
		double wg = Solar.perigeeLongDeg(t2010);
		assertEquals(283.112438, wg, TOL);
	}


	@DisplayName("Verify Sun equatorial coordinates 1988-07-27 PA46")
	@Test
	void testGetSolarRaDec() {
		LocalDateTime utcDateTime = LocalDateTime.of(1988, 7, 27, 0, 0, 0);
		Map<CoordsEnum, Double> map = Solar.getSunRaDec(utcDateTime);
		double raHr0 = AstroCoords.raHmsToRaHr("08:26:04");
		double decDeg0 = AstroCoords.decDmsToDecDeg("19:12:43");

		assertEquals(raHr0, map.get(CoordsEnum.RA_HR), SECOND_TOL);
		assertEquals(decDeg0, map.get(CoordsEnum.DEC_DEG), SECOND_TOL);
	}

	@DisplayName("Verify sunset civil time, Boston site PA49")
	@Test
	void testSunSetTime() {
		LocalDate siteCivilDate = LocalDate.of(1986, 3, 10);
		Solar solarTimes = new Solar(bostonSite, siteCivilDate);
		
		double utcOffsetHr = bostonSite.getUtcOffsetHr();
		LocalDateTime siteCivilDateTime = LocalDateTime.of(siteCivilDate,  LocalTime.of(17,  45));
		LocalTime utcTime = 
				TimesConverter.convertCivilDateTimeToUtc(siteCivilDateTime, utcOffsetHr).toLocalTime();
		
		//assertEquals("17:45", solarTimes.getSunSetValue());
		// assertTrue(ChronoUnit.MINUTES.between(utcTime, solarTimes.getUtcSunSet()) == 0);
	}
	
//	@DisplayName("Verify sunrise civil time, Boston site PA49")
//	@Test
//	void testSunRisetime() {
//		LocalDate siteCivilDate = LocalDate.of(1986, 3, 9);
//		SolarTimes solarTimes = new SolarTimes(bostonSite, siteCivilDate);	
//		
//		double utcOffsetHr = bostonSite.getUtcOffsetHr();
//		LocalDateTime siteCivilDateTime = LocalDateTime.of(siteCivilDate,  LocalTime.of(6,  5));
//		LocalTime utcTime = 
//				TimesConverter.convertCivilDateTimeToUtc(siteCivilDateTime, utcOffsetHr).toLocalTime();
//				
//		// assertEquals("06:05", solarTimes.getSunRiseValue());
//		assertTrue(ChronoUnit.MINUTES.between(utcTime, solarTimes.getUtcSunRise()) == 0);
//	}

	
//	@DisplayName("Verify twilight evening end time Gwch site PA50")
//	@Test
//	void testTwilightEndTime() {
//		LocalDate siteCivilDate = LocalDate.of(1979, 9, 7);
//		SolarTimes solarTimes = new SolarTimes(gwchSite, siteCivilDate);
//		
//		double utcOffsetHr = gwchSite.getUtcOffsetHr();
//		LocalDateTime siteCivilDateTime = LocalDateTime.of(siteCivilDate,  LocalTime.of(20,  37));
//		LocalTime utcTime = 
//				TimesConverter.convertCivilDateTimeToUtc(siteCivilDateTime, utcOffsetHr).toLocalTime();
//		
//		assertEquals("20:37", solarTimes.getTwilightEndValue());
//		assertTrue(ChronoUnit.MINUTES.between(utcTime, solarTimes.getUtcTwilightEnds()) == 0);
//	}
	
//	@DisplayName("Verify twilight morning start time Gwch site PA50")
//	@Test
//	void testTwilightStarTime() {
//		LocalDate siteCivilDate = LocalDate.of(1979, 9, 6);
//		SolarTimes solarTimes = new SolarTimes(gwchSite, siteCivilDate);
//		
//		double utcOffsetHr = gwchSite.getUtcOffsetHr();
//		LocalDateTime siteCivilDateTime = LocalDateTime.of(siteCivilDate,  LocalTime.of(3,  17));
//		LocalTime utcTime = 
//				TimesConverter.convertCivilDateTimeToUtc(siteCivilDateTime, utcOffsetHr).toLocalTime();
//		
//		assertEquals("03:17", solarTimes.getTwilightStartValue());
//		assertTrue(ChronoUnit.MINUTES.between(utcTime, solarTimes.getUtcTwilightStarts()) == 0);
//	}
	
//	@DisplayName("Verify Sun never sets summer polar site PA50")
//	@Test
//	void testSunNeverSets() {
//		LocalDate siteCivilDate = LocalDate.of(1979, 6, 22);
//		SolarTimes solarTimes = new SolarTimes(polarSite, siteCivilDate);
//		
//		assertTrue(solarTimes.isSunNeverSets());
//		assertFalse(solarTimes.isSunNeverRises());
//	}
	
//	@DisplayName("Verify Sun never rises winter polar site PA50")
//	@Test
//	void testSunNeverRises() {
//		LocalDate siteCivilDate = LocalDate.of(1979, 12, 22);
//		SolarTimes solarTimes = new SolarTimes(polarSite, siteCivilDate);
//		
//		assertFalse(solarTimes.isSunNeverSets());
//		assertTrue(solarTimes.isSunNeverRises());
//	}
	
//	@DisplayName("Verify twilight not defined gwch site PA50")
//	@Test
//	void testTwilightNotDefined() {
//		LocalDate siteCivilDate = LocalDate.of(1979, 6, 22);
//		SolarTimes solarTimes = new SolarTimes(gwchSite, siteCivilDate);
//		
//		assertTrue(solarTimes.isTwilightNotDefined());
//		assertFalse(solarTimes.isSunNeverSets());
//		assertFalse(solarTimes.isSunNeverRises());
//	}
	

//	@DisplayName("Verify SkySafari Sun times for south hemisphere site: Siding springs")
//	@Test
//	void testSunTimesSidingSprings() {
//		LocalDate siteCivilDate = LocalDate.of(2021, 10, 28);
//		SolarTimes solarTimes = new SolarTimes(sidingSite, siteCivilDate);
//		
//		double safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(19,  20, 21));
//	//	LocalTime localTime  = LocalTime.parse(solarTimes.getSunSetValue(), SolarTimes.LDT_FOMATTER);		
//	//	assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 6.0 * MINUTE_TOL);
//		
//		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(20,  51, 21));
//		//localTime  = LocalTime.parse(solarTimes.getTwilightEndValue(), SolarTimes.LDT_FOMATTER);		
//		//assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 2.0 * MINUTE_TOL);
//		
//		siteCivilDate = LocalDate.of(2021, 10, 27);
//		solarTimes = new SolarTimes(sidingSite, siteCivilDate);
//		
//		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(4,  44, 29));
//	//	localTime  = LocalTime.parse(solarTimes.getTwilightStartValue(), SolarTimes.LDT_FOMATTER);		
//		//assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 2.0 * MINUTE_TOL);
//		
//		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(6,  15, 12));
//		//localTime  = LocalTime.parse(solarTimes.getSunRiseValue(), SolarTimes.LDT_FOMATTER);		
//		//assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 6.0 * MINUTE_TOL);
//	}
}

