package com.github.richardflee.astroimagej.visibility_plotter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.visibility_plotter.CoordsConverter.CoordsEnum;

class SolarTest {

	public static final double TOL = 1e-6;
	// comparing times to 1 sec
	public static final double SECOND_TOL = 1.0 / 3600.0;
	public static final double MINUTE_TOL = 60.0 * SECOND_TOL;

	private static ObservationSite bostonSite = null;
	private static ObservationSite gwchSite = null;
	private static ObservationSite polarSite = null;
	private static ObservationSite sidingSite = null;
	
	double t2010 = 0.0;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		double siteLong = -71.05; // 71.05W
		double siteLat = 42.37; // 42.37N
		double siteElevation = 0.0;
		double utcOffsetHr = -5.0;
		bostonSite = new ObservationSite(siteLong, siteLat, siteElevation, utcOffsetHr);
		
		double siteLongDeg = 0.0;
		double siteLatDeg = 52.0; // 42.37N
		utcOffsetHr = 0.0;
		gwchSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, utcOffsetHr);
		
		siteLongDeg = -71.05;
		siteLatDeg = 82.0; // 82N
		utcOffsetHr = -5.0;
		polarSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, utcOffsetHr);
		
		siteLongDeg = AstroCoords.dmsToDeg("149:03:42");
		siteLatDeg = -1.0 * AstroCoords.dmsToDeg("31:16:24");   // 31S
		utcOffsetHr = 11.0;
		sidingSite = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, utcOffsetHr);
	}


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
		Solar solar = new Solar(bostonSite);
		
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		assertEquals("17:45", st.getCivilSunSetValue());
	}
	
	
	@DisplayName("Verify sunrise civil time, Boston site PA49")
	@Test
	void testSunRiseTime() {
		LocalDate siteCivilDate = LocalDate.of(1986, 3, 10).minusDays(1);
		Solar solar = new Solar(bostonSite);
		
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		assertEquals("06:05", st.getCivilSunRiseValue());
	}
	
	
	@DisplayName("Verify twilight evening end time Gwch site PA50")
	@Test
	void testTwilightEndsTime() {
		LocalDate siteCivilDate = LocalDate.of(1979, 9, 7);
		Solar solar = new Solar(gwchSite);
		
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		assertEquals("20:37", st.getCivilTwilightEndsValue());
	}
	
	@DisplayName("Verify twilight start time Gwch site PA50")
	@Test
	void testTwilightStartsTime() {
		LocalDate siteCivilDate = LocalDate.of(1979, 9, 7).minusDays(1);
		Solar solar = new Solar(gwchSite);
		
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		assertEquals("03:17", st.getCivilTwilightStartsValue());
	}

	// SkySafari sun rise and set times are to sun centre, 
	// approx. 5 mins difference to limb-based timings
	// Twilight start & end based on sun centre timings with ~ 2 mins tol
	@DisplayName("Verify SkySafari Sun times for south hemisphere site: Siding springs")
	@Test
	void testSunTimesSidingSprings() {
		LocalDate siteCivilDate = LocalDate.of(2021, 10, 28);
		Solar solar = new Solar(sidingSite);		
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		
		// sunset
		double safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(19,  20, 21));
		String HHmm = st.getCivilSunSetValue();
		LocalTime localTime  = LocalTime.parse(HHmm, Solar.LDT_FOMATTER);		
		assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 6.0 * MINUTE_TOL);
		
		// evening twilight ends
		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(20,  51, 21));
		HHmm = st.getCivilTwilightEndsValue();
		localTime  = LocalTime.parse(HHmm, Solar.LDT_FOMATTER);		
		assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 2.0 * MINUTE_TOL);
		
		// reset test date
		siteCivilDate = LocalDate.of(2021, 10, 28).minusDays(1);
		solar = new Solar(sidingSite);		
		st = solar.getCivilSunTimes(siteCivilDate);
		
		// morning twilight starts
		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(4,  44, 29));
		HHmm = st.getCivilTwilightStartsValue();
		localTime  = LocalTime.parse(HHmm, Solar.LDT_FOMATTER);		
		assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 2.0 * MINUTE_TOL);
		
		// sunrise
		safariHr = TimesConverter.convertLocalTimeToHours(LocalTime.of(6,  15, 12));
		HHmm = st.getCivilSunRiseValue();
		localTime  = LocalTime.parse(HHmm, Solar.LDT_FOMATTER);		
		assertEquals(safariHr, TimesConverter.convertLocalTimeToHours(localTime), 6.0 * MINUTE_TOL);
	}
	
	@DisplayName("Verify Sun never rises winter polar site PA50")
	@Test
	void testSunNeverRises() {
		LocalDate civilDate = LocalDate.of(2019, 12, 23);
		LocalDateTime ldtStart = LocalDateTime.of(civilDate, LocalTime.of(12,  1));
		LocalDateTime ldtEnd = LocalDateTime.of(civilDate, LocalTime.of(11,  59));
		
		Solar solar = new Solar(polarSite);		
		SolarTimes st = solar.getCivilSunTimes(civilDate);
		assertTrue(ldtStart.isEqual(st.getCivilSunSet()));
		assertTrue(ldtStart.isEqual(st.getCivilTwilightEnds()));
		assertTrue(ldtEnd.isEqual(st.getCivilTwilightStarts()));
		assertTrue(ldtEnd.isEqual(st.getCivilSunRise()));	
	}
	
	
	@DisplayName("Verify Sun never sets summer polar site PA50")
	@Test
	void testSunNeverSets() {
		LocalDate civilDate = LocalDate.of(2019, 6, 23);
		LocalDateTime ldtStart = LocalDateTime.of(civilDate, LocalTime.of(0,  0));
		LocalDateTime ldtEnd = LocalDateTime.of(civilDate, LocalTime.of(0,  0));
		
		Solar solar = new Solar(polarSite);		
		SolarTimes st = solar.getCivilSunTimes(civilDate);
		assertTrue(ldtStart.isEqual(st.getCivilSunSet()));
		assertTrue(ldtStart.isEqual(st.getCivilTwilightEnds()));
		assertTrue(ldtEnd.isEqual(st.getCivilTwilightStarts()));
		assertTrue(ldtEnd.isEqual(st.getCivilSunRise()));	
	}
}

