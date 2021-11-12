package com.github.richardflee.astroimagej.visibility_plotter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.visibility_plotter.CoordsConverter.CoordsEnum;

class CoordsConverterTest {

	public static final double TOL = 1e-6;
	// comparing times to 1 sec
	public static final double SEC_TOL = 1.0 / 3600.0;

	//private static final double nuDeg = 34.0 / 60.0;

	// default royal obs greenwich
	//private static ObservationSite gwchSite;

	// example W site moore obsy KY
	private static ObservationSite mooreSite;
	//private static TimesConverter mooreTimesConverter;

	// example S site siding spring
	private static ObservationSite sidingSite;

	// rise-set times and azimuth rise angle
	private static ObservationSite rsSite;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// default 51N 0W greenwich
		//gwchSite = new ObservationSite();

		// moore 45N 85W
		double siteLong = AstroCoords.dmsToDeg("-85:31:42.51");
		double siteLat = AstroCoords.dmsToDeg("+38:20:41.25");
		double siteElevation = 100.0;
		mooreSite = new ObservationSite(siteLong, siteLat, siteElevation, 0.0);
	//	mooreTimesConverter = new TimesConverter(mooreSite);

		// siding spring
		siteLong = AstroCoords.dmsToDeg("149:03:42");
		siteLat = AstroCoords.dmsToDeg("-31:16:24");
		siteElevation = 1100;
		sidingSite = new ObservationSite(siteLong, siteLat, siteElevation, 0.0);

		// rsSite
		double rsLongDeg = 64.0; // 64E
		double rsLatDeg = 30.0; // 30N
		double rsAlt = 0.0;
		rsSite = new ObservationSite(rsLongDeg, rsLatDeg, rsAlt, 0.0);
	}

	@DisplayName("Verify conversion between RA and HA PA24")
	@Test
	void testGetHourAngle() {
		// site
		ObservationSite site = new ObservationSite(-64.0, 40, 0, 0.0);

		// object
		double raHr = AstroCoords.raHmsToRaHr("18:32:21");
		double decDeg = 0.0;
		String objectId = "ha_test";
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// converter objects
		CoordsConverter cc = new CoordsConverter(fo, site);

		// utc
		LocalDateTime utcDateTime = LocalDateTime.of(1980, 4, 22, 18, 36, 51, (int) 0.67e9);

		assertEquals(-14.126763, cc.getHourAngle(utcDateTime), TOL);
	}

	@DisplayName("Verify conversion between RA and HA at moore site PA24")
	@Test
	void testGetHourAngle_Moore() {
		// site
		ObservationSite site = mooreSite;

		// object
		double raHr = AstroCoords.raHmsToRaHr("06:30:17.80");
		double decDeg = 0.0;
		String objectId = "ha_test";
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// converter objects
		CoordsConverter cc = new CoordsConverter(fo, site);

		// utc
		LocalDateTime utcDateTime = LocalDateTime.of(1995, 10, 22, 7, 36, 51);

		assertEquals(-2.574722, cc.getHourAngle(utcDateTime), SEC_TOL);
	}

	@DisplayName("Verify object AltAz coordinates from equatorial coords and object HA PA25")
	@Test
	void testGetAltAz() {

		// FieldObect
		String raHms = "05:51:44";
		String decDms = "+23:13:10";
		double raHr = AstroCoords.raHmsToRaHr(raHms);
		double decDeg = AstroCoords.decDmsToDecDeg(decDms);
		String objectId = "test_AltAz";
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// site
		double siteLatDeg = 52.0;
		ObservationSite site = new ObservationSite(-64.0, siteLatDeg, 0, 0.0);

		// converters
		CoordsConverter cc = new CoordsConverter(fo, site);

		// hour angle
		LocalTime haLocalTime = LocalTime.of(5, 51, 44);
		double haHr = TimesConverter.convertLocalTimeToHours(haLocalTime);

		Map<CoordsConverter.CoordsEnum, Double> map = cc.getAltAzm(haHr);
		assertEquals(19.334345, map.get(CoordsConverter.CoordsEnum.ALT_DEG), TOL);
		assertEquals(283.271027, map.get(CoordsConverter.CoordsEnum.AZM_DEG), TOL);
	}

	@DisplayName("Verify object AltAz coordinates Moore Obsy AIJ CC")
	@Test
	void testComputeAltAz_Moore() {
		String raHms = "06:31:45.56";
		String decDms = "+29:39:28.66";
		double raHr = AstroCoords.raHmsToRaHr(raHms);
		double decDeg = AstroCoords.decDmsToDecDeg(decDms);
		String objectId = "wasp12";
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// hour angle
		LocalTime haLocaltime = LocalTime.of(4, 27, 44);
		double haHr = -1.0 * TimesConverter.convertLocalTimeToHours(haLocaltime);

		// coordsConverter & AltAz map
		// TimesConverter tc = new TimesConverter(mooreSite);
		CoordsConverter coords = new CoordsConverter(fo, mooreSite);
		Map<CoordsConverter.CoordsEnum, Double> map = coords.getAltAzm(haHr);

		assertEquals(35.032333, map.get(CoordsConverter.CoordsEnum.ALT_DEG), 0.005);
		assertEquals(77.526194, map.get(CoordsConverter.CoordsEnum.AZM_DEG), 0.005);
	}

	@DisplayName("Verify object AltAz coordinates Siding Spring Obsy AIJ CC")
	@Test
	void testComputeAltAz_SidingSpring() {
		String raHms = "00:26:30.79";
		String decDms = "-77:08:56.91";
		double raHr = AstroCoords.raHmsToRaHr(raHms);
		double decDeg = AstroCoords.decDmsToDecDeg(decDms);
		String objectId = "HIP2021";
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// hour angle
		LocalTime haLocaltime = LocalTime.of(6, 44, 8);
		double haHr = -1.0 * TimesConverter.convertLocalTimeToHours(haLocaltime);

		// coordsConverter & AltAz map
		CoordsConverter coords = new CoordsConverter(fo, sidingSite);
		Map<CoordsConverter.CoordsEnum, Double> map = coords.getAltAzm(haHr);

		assertEquals(28.017361, map.get(CoordsConverter.CoordsEnum.ALT_DEG), 0.005);
		assertEquals(165.683167, map.get(CoordsConverter.CoordsEnum.AZM_DEG), 0.005);
	}

	@DisplayName("Verify object azimuth rise angle PA 33")
	@Test
	void testComputeAzmRise() {
		// object
		String objectId = "RiseSetter";
		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
		double decDeg = AstroCoords.decDmsToDecDeg("+21:42:00");
		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);

		// date
		LocalDate rsLocalDate = LocalDate.of(2010, 8, 24);
		assertEquals(64.362370, rsCoords.getRiseAzm(rsLocalDate), TOL);
	}

	@DisplayName("Verify object azimuth rise angle is 180.0 if object never sets (circumpolar)")
	@Test
	void testNeverSetsAzmRise() {
		// circumpolar object
		String objectId = "NeverSetter";
		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
		double decDeg = AstroCoords.decDmsToDecDeg("+85:42:00");
		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);

		// date & coords converter
		LocalDate rsLocalDate = LocalDate.of(2010, 8, 24);

		assertEquals(180.0, rsCoords.getRiseAzm(rsLocalDate), TOL);
	}
	
	@DisplayName("Verify object azimuth rise angle is 0.0 if object never rises")
	@Test
	void testNeverRisesAzmRise() {
		// circumpolar object
		String objectId = "NeverRiser";
		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
		double decDeg = AstroCoords.decDmsToDecDeg("-85:42:00");
		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);

		// date
		LocalDate rsUtcDate = LocalDate.of(2010, 8, 24);

		assertEquals(0.0, rsCoords.getRiseAzm(rsUtcDate), TOL);
	}

	@DisplayName("Verify object rise and set utc times PA 33")
	@Test
	void testComputeRiseSetUtc() {
		String objectId = "RiseSetter";
		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
		double decDeg = AstroCoords.decDmsToDecDeg("+21:42:00");
		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);
		
		//converters
		// TimesConverter rsTimes = new TimesConverter(rsSite);
		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);
		
		// date & coords converter 
		LocalDate rsUtcDate = LocalDate.of(2010, 8, 24);
		
		Map<CoordsEnum, Double> rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
		double riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
		double setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);
		
		assertEquals(14.271673, riseUtcHr, TOL);
		assertEquals(4.166987, setUtcHr,  TOL);	
	}
	
//	@DisplayName("Verify object rise-set times are 12 hr if object never sets (circumpolar)")
//	@Test
//	void testNeverSetsRiseSetTimes() {
//		// circumpolar object
//		String objectId = "NeverSetter";
//		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
//		double decDeg = AstroCoords.decDmsToDecDeg("+85:42:00");
//		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);
//
//		// converters
//		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);
//
//		// date
//		LocalDate rsUtcDate = LocalDate.of(2010, 8, 24);
//		
//		Map<CoordsEnum, Double> rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
//		double riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
//		double setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);
//
//		assertEquals(12.0, riseUtcHr, TOL);
//		assertEquals(12.0, setUtcHr, TOL);
//		assertTrue(rsCoords.isNeverSets());
//		assertFalse(rsCoords.isNeverRises());
//		
//	}
	
//	@DisplayName("Verify object rise-set times are 0 hr if object never rises")
//	@Test
//	void testNeverRisesRiseSetTimes() {
//		// object never rises
//		String objectId = "NeverRiser";
//		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
//		double decDeg = AstroCoords.decDmsToDecDeg("-85:42:00");
//		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);
//
//		// converters
//		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);
//
//		// date
//		LocalDate rsUtcDate = LocalDate.of(2010, 8, 24);
//		
//		Map<CoordsEnum, Double> rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
//		double riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
//		double setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);
//
//		assertEquals(0.0, riseUtcHr, TOL);
//		assertEquals(0.0, setUtcHr, TOL);
//		assertFalse(rsCoords.isNeverSets());
//		assertTrue(rsCoords.isNeverRises());
//	}
}
