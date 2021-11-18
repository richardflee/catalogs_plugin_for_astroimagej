package com.github.richardflee.astroimagej.visibility_plotter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.utils.MathUtils;

public class CoordsConverter {

	protected enum CoordsEnum {
		ALT_DEG, AZM_DEG, 
		RA_HR, DEC_DEG, 
		RISE_TIME, SET_TIME,
		SUN_RISING, SUN_SETTING;
	}

	// horizon refraction and object size angles PA49
	public static final double REFRACT_NU_DEG = 34.0 / 60.0; // point source refraction
	

	private BaseFieldObject fo = null;
	private TimesConverter timesConverter = null;
	private ObservationSite site = null;
	private double nuDeg = 0.0;
	private boolean neverRises = false;
	private boolean neverSets = false;

	public CoordsConverter(BaseFieldObject fo, ObservationSite site) {
		this.fo = fo;
		this.site = site;
		this.timesConverter = new TimesConverter(site);
		this.nuDeg = REFRACT_NU_DEG;
		
		this.neverRises = (getHaTerm(site.getSiteLatitudeDeg(), fo.getDecDeg()) > 1.0);
		this.neverSets = (getHaTerm(site.getSiteLatitudeDeg(), fo.getDecDeg()) < -1.0);
	}

	
	public Map<CoordsEnum, Double> getAltAzm(LocalDateTime utcDateTime) { 
		double haHr = getHourAngle(utcDateTime);
		return getAltAzm(haHr);
	}

	/**
	 * Computes object altitude and azimuth in horizon coordinates at a given
	 * location and hour angle
	 * <p>
	 * Ref: PA section 25
	 * </p>
	 * 
	 * @param haHr object hour angle -/+ East / West of meridian
	 * @return map indexed by Alt/Azm CoordsEnum enums
	 */
	public Map<CoordsEnum, Double> getAltAzm(double haHr) {
		// site latitude in deg
		double latDeg = this.site.getSiteLatitudeDeg();

		// object J2000 coordinates
		double decDeg = this.fo.getDecDeg();

		// convert HA from hour to deg
		double haDeg = haHr * 15.0;

		// computes object altitude in deg
		double altTerm = MathUtils.sind(decDeg) * MathUtils.sind(latDeg)
				+ MathUtils.cosd(decDeg) * MathUtils.cosd(latDeg) * MathUtils.cosd(haDeg);
		double altDeg = Math.toDegrees(Math.asin(altTerm));

		// computes object azimuth in deg
		double y = -MathUtils.cosd(decDeg) * MathUtils.cosd(latDeg) * MathUtils.sind(haDeg);
		double x = MathUtils.sind(decDeg) - MathUtils.sind(latDeg) * MathUtils.sind(altDeg);
		double azmDeg = MathUtils.reduceToRange(Math.toDegrees(Math.atan2(y, x)), 360);

		// compile and return enum-indexed map with
		Map<CoordsEnum, Double> map = new HashMap<>();
		map.put(CoordsEnum.ALT_DEG, altDeg);
		map.put(CoordsEnum.AZM_DEG, azmDeg);
		return map;
	}
	
	/**
	 * Computes object hour angle
	 * <p>
	 * Ref: PA section 24
	 * </p>
	 * 
	 * @param raHr        object RA in hours
	 * @param utcDateTime UT at time of observation
	 * @param site        observation site
	 * @return -/+ hour angle for object East / West of meridian
	 */
	public double getHourAngle(LocalDateTime utcDateTime) {
		double raHr = fo.getRaHr();

		// converts utc => gst => lst => lstHr
		LocalDateTime gstDateTime = this.timesConverter.convertUtcToGst(utcDateTime);
		LocalDateTime lstDateTime = this.timesConverter.convertGstToLst(gstDateTime);
		LocalTime lstTime = lstDateTime.toLocalTime();
		double lstHr = TimesConverter.convertLocalTimeToHours(lstTime);

		// -/+ hour angle for object East / West of meridian
		double haHr = lstHr - raHr;
		return haHr;
	}

	/**
	 * Computes object rise and set utc a given location
	 * <p>
	 * Ref: PA section 25
	 * </p>
	 * 
	 * @param utcDate observation date
	 * @return map with utc rise and set times; times set to LOCALTIME_ZERO if
	 *         object never rises or is circumpolar
	 */
	public Map<CoordsEnum, Double> getRiseSetHr(LocalDate utcDate) {
		Map<CoordsEnum, Double> map = new HashMap<>();
//		if (this.neverRises) {
//			map.put(CoordsEnum.RISE_TIME, TimesConverter.NEVER_RISES_HR);
//			map.put(CoordsEnum.SET_TIME, TimesConverter.NEVER_RISES_HR);
//			return map;
//		}
//		if (this.neverSets) {
//			map.put(CoordsEnum.RISE_TIME, TimesConverter.NEVER_SETS_HR);
//			map.put(CoordsEnum.SET_TIME, TimesConverter.NEVER_SETS_HR);
//			return map;
//		}

		//double latDeg = this.site.getSiteLatitudeDeg();
		double raHr = this.fo.getRaHr();
		double decDeg = this.fo.getDecDeg();

		// compute HA term
		double haTerm = getHaTerm(site.getSiteLatitudeDeg(), decDeg);

		// hour angle
		double haHr = Math.toDegrees(Math.acos(haTerm)) / 15.0;

		// HA -> LST Rise/set times in hours -> local time
		double lstRiseHr = MathUtils.reduceToRange(raHr - haHr, 24.0);
		double lstSetHr = MathUtils.reduceToRange(raHr + haHr, 24.0);
		
		LocalTime lstRiseTime = TimesConverter.convertHoursToLocalTime(lstRiseHr);
		LocalTime lstSetTime = TimesConverter.convertHoursToLocalTime(lstSetHr);
		
		LocalDateTime lstRiseDateTime = LocalDateTime.of(utcDate, lstRiseTime);
		LocalDateTime lstSetDateTime = LocalDateTime.of(utcDate, lstSetTime);

		// LST -> GST -> UTC
		TimesConverter tc = new TimesConverter(this.site);
		LocalDateTime gstRiseDateTime = tc.convertLstToGst(lstRiseDateTime);
		LocalDateTime gstSetDateTime = tc.convertLstToGst(lstSetDateTime);
		
		//LocalDateTime gstRiseDateTime = LocalDateTime.of(utcDate, gstRiseTime);
		//LocalDateTime gstSetDateTime = LocalDateTime.of(utcDate, gstSetTime);
		
		LocalDateTime utcRiseDateTime = tc.convertGstToUtc(gstRiseDateTime);
		LocalDateTime utcSetDateTime = tc.convertGstToUtc(gstSetDateTime);
		
		double utcRiseHr = TimesConverter.convertLocalTimeToHours(utcRiseDateTime.toLocalTime());
		double utcSetHr = TimesConverter.convertLocalTimeToHours(utcSetDateTime.toLocalTime());

		// compile and return enum-indexed map with utc rise/set times
		map.put(CoordsEnum.RISE_TIME, utcRiseHr);
		map.put(CoordsEnum.SET_TIME, utcSetHr);
		return map;
	}

	/**
	 * Computes object rise azimuth at a given location; azimuth_set = 360 -
	 * azimuth_rise
	 * <p>
	 * Ref: PA section 33
	 * </p>
	 * 
	 * @param utcDate observation date
	 * @return azimuth rise angle in degree; 0.0 if object never rises or is
	 *         circumpolar
	 */
	public double getRiseAzm(LocalDate utcDate) {
		if (this.neverRises) {
			return TimesConverter.NEVER_RISES_DEG;
		}
		if (this.neverSets) {
			return TimesConverter.NEVER_SETS_DEG;
		}
		double latDeg = this.site.getSiteLatitudeDeg();
		double decDeg = this.fo.getDecDeg();

		// compute azimuth_term
		double azmTerm1 = MathUtils.sind(decDeg) + MathUtils.sind(this.nuDeg) * MathUtils.sind(latDeg);
		double azmTerm2 = MathUtils.cosd(this.nuDeg) * MathUtils.cosd(latDeg);
		double azmTerm = azmTerm1 / azmTerm2;

		// returns 0 if |azmTerm| > 1, i.e. if object never rises or is circumpolar
		double azmRiseDeg = Math.abs(azmTerm) > 1.0 ? 0.0 : Math.toDegrees(Math.acos(azmTerm));
		return MathUtils.reduceToRange(azmRiseDeg, 360);
	}

	private double getHaTerm(double siteLatDeg, double decDeg) {
		double term1 = -(MathUtils.sind(this.nuDeg) + 
				MathUtils.sind(siteLatDeg) * MathUtils.sind(decDeg));
		double term2 = MathUtils.cosd(siteLatDeg) * MathUtils.cosd(decDeg);
		return term1 / term2;
	}


	public TimesConverter getTimesConverter() {
		return timesConverter;
	}


	public static void main(String[] args) {

		// test rising setting pa33 ***************************************
		// site
		double rsLongDeg = 64.0; // 64E
		double rsLatDeg = 30.0; // 30N
		double rsAlt = 0.0;
		ObservationSite rsSite = new ObservationSite(rsLongDeg, rsLatDeg, rsAlt, 0.0);

		// object
		String objectId = "RiseSetter";
		double raHr = AstroCoords.raHmsToRaHr("23:39:20");
		double decDeg = AstroCoords.decDmsToDecDeg("+21:42:00");
		BaseFieldObject rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		//TimesConverter rsTimes = new TimesConverter(rsSite);
		CoordsConverter rsCoords = new CoordsConverter(rsObject, rsSite);

		// obs date
		LocalDate rsUtcDate = LocalDate.of(2010, 8, 24);

		// rise az alt
		double riseAzmDeg = rsCoords.getRiseAzm(rsUtcDate);
		double setAzmDeg = 360 - riseAzmDeg;

		// rise-set hrs
		Map<CoordsEnum, Double> rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
		double riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
		double setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);

		System.out.println("\nRISE-SET");
		System.out.println(String.format("Rise-Set site data:\n %s", rsSite.toString()));
		System.out.println(String.format("\nObject data: \n%s", rsObject.toString()));
		System.out.println((String.format("Rise/set azm  %3.6f, %3.6f:", riseAzmDeg, setAzmDeg)));
		System.out.println((String.format("Rise/set hr  %3.6f, %3.6f:", riseUtcHr, setUtcHr)));

		// test never rise & never sets ############
		// circumpolar object
		objectId = "NeverSetter";
		raHr = AstroCoords.raHmsToRaHr("23:39:20");
		decDeg = AstroCoords.decDmsToDecDeg("+85:42:00");
		rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		// rsTimes = new TimesConverter(rsSite);
		rsCoords = new CoordsConverter(rsObject, rsSite);

		// date & coords converter
		rsUtcDate = LocalDate.of(2010, 8, 24);

		// rise az alt
		riseAzmDeg = rsCoords.getRiseAzm(rsUtcDate);
		setAzmDeg = 360 - riseAzmDeg;

		// rise-set hrs
		rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
		riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
		setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);

		System.out.println("\nCircumpolar");
		System.out.println(String.format("Rise-Set site data:\n %s", rsSite.toString()));
		System.out.println(String.format("\nObject data: \n%s", rsObject.toString()));
		System.out.println((String.format("Rise/set azm  %3.6f, %3.6f:", riseAzmDeg, setAzmDeg)));
		System.out.println((String.format("Rise/set hr  %3.6f, %3.6f:", riseUtcHr, setUtcHr)));

		// never rises object
		objectId = "NeverRiser";
		raHr = AstroCoords.raHmsToRaHr("23:39:20");
		decDeg = AstroCoords.decDmsToDecDeg("-85:42:00");
		rsObject = new BaseFieldObject(objectId, raHr, decDeg);

		// converters
		// rsTimes = new TimesConverter(rsSite);
		rsCoords = new CoordsConverter(rsObject, rsSite);

		// date & coords converter
		rsUtcDate = LocalDate.of(2010, 8, 24);

		// rise az alt
		riseAzmDeg = rsCoords.getRiseAzm(rsUtcDate);
		setAzmDeg = 360 - riseAzmDeg;

		// rise-set hrs
		rsUtcTime = rsCoords.getRiseSetHr(rsUtcDate);
		riseUtcHr = rsUtcTime.get(CoordsEnum.RISE_TIME);
		setUtcHr = rsUtcTime.get(CoordsEnum.SET_TIME);

		System.out.println("\nNeverRises");
		System.out.println(String.format("Rise-Set site data:\n %s", rsSite.toString()));
		System.out.println(String.format("\nObject data: \n%s", rsObject.toString()));
		System.out.println((String.format("Rise/set azm  %3.6f, %3.6f:", riseAzmDeg, setAzmDeg)));
		System.out.println((String.format("Rise/set hr  %3.6f, %3.6f:", riseUtcHr, setUtcHr)));

	}
}
