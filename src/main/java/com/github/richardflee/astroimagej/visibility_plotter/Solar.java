package com.github.richardflee.astroimagej.visibility_plotter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.utils.MathUtils;
import com.github.richardflee.astroimagej.visibility_plotter.CoordsConverter.CoordsEnum;

/**
 * Computes sunrise, set and astronomical twilight times at a specified
 * observation site <p> Ref: Practical Astronomy,.. Duffett-Smith, Sections 49,
 * 50 </p>
 */

public class Solar {

	// formats rise-set times hh:mm
	public static final DateTimeFormatter LDT_FOMATTER = DateTimeFormatter.ofPattern("HH:mm");

	// Sun's angular dia * 0.5
	private static final double SUN_SEMIDIA_DEG = 0.533 / 2.0;

	// poly constants to compute obliquity of the ecliptic PA27
	private static final String ECL_0 = "23:26:21.45";
	private static final double ECL_1 = -46.815;
	private static final double ECL_2 = -0.0006;
	private static final double ECL_3 = 0.00181;

	// quadratic const to compute solar mean ecliptic longitude
	private static final double eg0 = 279.6966778;
	private static final double eg1 = 36000.76892;
	private static final double eg2 = 0.0003025;

	// quadratic const to compute solar longitude at perigee
	private static final double w0 = 281.2208444;
	private static final double w1 = 1.719175;
	private static final double w2 = 0.000452778;

	// quadratic const to compute eccentricity Sun-Earth orbit
	private static final double e0 = 0.01675104;
	private static final double e1 = -0.0000418;
	private static final double e2 = -0.000000126;

	// iteration tolerance in radian (1 uRad)
	private static final double ITER_TOL = 1e-6;

	// sun rise-set zenith angles, including 34' refraction
	private static final double ZENITH_RISESET_DEG = 90.0 + SUN_SEMIDIA_DEG + CoordsConverter.REFRACT_NU_DEG;

	// twilight zenith angle, no refraction correction applied
	private static final double ZENITH_TWILIGHT_DEG = 108.0;

	// field variables
	private ObservationSite site = null;
	private TimesConverter solarTimesConverter = null;

	public Solar(ObservationSite site) {
		this.site = site;
		this.solarTimesConverter = new TimesConverter(site);
	}

	/**
	 * Computes sunset, sunrise and twilight local civil times for specified date
	 * 
	 * @param siteCivilDate observation date
	 * @return SolarTimes object encapsulating solar civil times in HH:mm format
	 */
	public SolarTimes getCivilSunTimes(LocalDate siteCivilDate) {
		// default SolarTimes data object
		SolarTimes solarTimes = new SolarTimes();
		
		// observation evening sunset
		CoordsEnum en = CoordsEnum.SUN_SETTING;
		double zenDeg = Solar.ZENITH_RISESET_DEG;
		LocalTime civilTime = getCivilSunTime(siteCivilDate, en, zenDeg);
		solarTimes.setCivilSunSetValue(civilTime);

		// observation evening twilight ends
		en = CoordsEnum.SUN_SETTING;
		zenDeg = Solar.ZENITH_TWILIGHT_DEG;
		civilTime = getCivilSunTime(siteCivilDate, en, zenDeg);
		solarTimes.setCivilTwilightEndsValue(civilTime);
		
		// next morning twilight starts
		en = CoordsEnum.SUN_RISING;
		zenDeg = Solar.ZENITH_TWILIGHT_DEG;
	    civilTime = getCivilSunTime(siteCivilDate.plusDays(1), en, zenDeg);
		solarTimes.setCivilTwilightStartsValue(civilTime);
		
		// next morning sunrise
		en = CoordsEnum.SUN_RISING;
		zenDeg = Solar.ZENITH_RISESET_DEG;
		civilTime = getCivilSunTime(siteCivilDate.plusDays(1), en, zenDeg);
		solarTimes.setCivilSunRiseValue(civilTime);
		return solarTimes;
	}
	
	/*
	 * Computes one of sunset, sunrise or twilight local civil time for specified date
	 * 
	 * @param siteCivilDate observation date
	 * @param en SUN_RISING or SUN_SETTING flag
	 * @param zenDeg Sun's zenith angle in deg
	 * @return sunset, sunrise or twilight in local civil time
	 */
	private LocalTime getCivilSunTime(LocalDate siteCivilDate, CoordsEnum en, double zenDeg) {
		// convert local civil noon to utc date-time
		LocalDateTime civilNoon = LocalDateTime.of(siteCivilDate, TimesConverter.LOCAL_TIME_NOON);
		LocalDateTime utcNoon = this.solarTimesConverter.convertCivilDateTimeToUtc(civilNoon);
		
		// get sunset/rise/twilight utc date-time
		LocalTime utcTime = utcZenithAngleTime(zenDeg, utcNoon, en);
		LocalDateTime utcDateTime = LocalDateTime.of(siteCivilDate, utcTime);
		
		// convert utc -> local civil time
		LocalDateTime civilDateTime = this.solarTimesConverter.convertUtcToCivilDateTime(utcDateTime);
		return civilDateTime.toLocalTime();
	}

	/*
	 * Double iteration to compute utc time for Sun at specified zenith angle <p>PA
	 * 50 for equation connecting HA to zenith angle</p>
	 * @param zenithDeg Sun's zenith angle (zenith angle = 90° - Sun altitude)
	 * @param utcNoon civil noon converted to utc
	 * @param en SUN_RISING or SUN_SETTING
	 * @return Sun rising or setting time utc
	 */
	private LocalTime utcZenithAngleTime(double zenithDeg, LocalDateTime utcNoon, CoordsEnum en) {
		// initial inputs
		LocalDateTime utcRiseSet = utcNoon;
		LocalDate utcDate = utcNoon.toLocalDate();

		// double iteration to compute utc time for specified solar zenith angle
		for (int i = 0; i < 2; i++) {

			// current extimate Sun's coordinates
			double sunRaHr = Solar.getSunRaDec(utcRiseSet).get(CoordsEnum.RA_HR);
			double sunDecDeg = Solar.getSunRaDec(utcRiseSet).get(CoordsEnum.DEC_DEG);

			// Sun' hour angle
			double haTerm = sunHaTerm(zenithDeg, sunDecDeg, this.site.getSiteLatitudeDeg());
			double haHr = Math.toDegrees(Math.acos(haTerm)) / 15.0;

			// Sun's LST (local sidereal time) in range 0..24 hr: HA = LST ± solar RA
			double lstRiseSetHr = (en == CoordsEnum.SUN_RISING) ? sunRaHr - haHr : sunRaHr + haHr;
			MathUtils.reduceToRange(lstRiseSetHr, 24.0);

			// converts LST to UTC
			LocalDateTime lstRiseSetDateTime = LocalDateTime.of(utcDate,
					TimesConverter.convertHoursToLocalTime(lstRiseSetHr));
			utcRiseSet = this.solarTimesConverter.convertLstToUtc(lstRiseSetDateTime);
		}
		return utcRiseSet.toLocalTime();
	}

	/*
	 * Computes inverse cosine of solar hour angle when Sun ias at specified zenith
	 * angle <p>Return value magnitude greater than 1.0 indicates Sun never rises or
	 * never sets</p> <p>Ref PA 50</p>
	 * @param decDeg current solar declination in deg
	 * @param siteLatDeg geographic latitude observation site
	 * @return inverse cosine of solar hour angle
	 */
	private double sunHaTerm(double zenithDeg, double decDeg, double siteLatDeg) {
		double haTerm1 = MathUtils.cosd(zenithDeg) - MathUtils.sind(decDeg) * MathUtils.sind(siteLatDeg);
		double haTerm2 = MathUtils.cosd(decDeg) * MathUtils.cosd(siteLatDeg);
		return haTerm1 / haTerm2;
	}

	/**
	 * Computes Sun's equatorial coordinates ra and dec for specified utc <p> Ref:
	 * PA section 47 </p>
	 * @param utcDateTime
	 *     utc date-time
	 * @return map containing solar ra and dec in deg
	 */
	protected static Map<CoordsEnum, Double> getSunRaDec(LocalDateTime utcDateTime) {

		// number Julian centuries since 1900 Jan 0.5
		double jd = TimesConverter.convertUtcToJD(utcDateTime);
		double t = (jd - TimesConverter.JD_1900) / TimesConverter.C_DAYS;

		// compute the current mean ecliptic, perigee and orbit eccentricity
		double eg = Solar.eclipticLongDeg(t);
		double wg = Solar.perigeeLongDeg(t);
		double ecc = Solar.orbitEccentricity(t);

		// solar mean anomaly in rad
		double m0Rad = Math.toRadians(MathUtils.reduceToRange(eg - wg, 360));

		// optimised eccentric anomaly in rad
		double eccAnomaly = optimiseEccentricAnomaly(m0Rad, ecc);

		// true anomaly nu in deg
		double tanNu = Math.sqrt((1 + ecc) / (1 - ecc)) * Math.tan(0.5 * eccAnomaly);
		double nu = Math.toDegrees(2.0 * Math.atan(tanNu));
		nu = MathUtils.reduceToRange(nu, 360.0);

		// Sun's geocentric ecliptic longitude
		double solarLongDeg = MathUtils.reduceToRange(nu + wg, 360);

		// maps solar longitude ecliptic coordinates to ra, dec in equatorial coords
		Map<CoordsEnum, Double> map = Solar.convertEclipticToEquatorial(solarLongDeg, 0.0, utcDateTime.toLocalDate());
		return map;
	}

	/**
	 * Iterative solution to Kepler's equation E - esin(E) = M (small e) <p> Ref: PA
	 * section 47, routine R2 </p>
	 * @param m0Rad
	 *     solar mean anomaly in radian
	 * @param ecc
	 *     eccentricity of Sun-Earth orbit
	 * @return optimised value of eccentric anomaly
	 */
	private static double optimiseEccentricAnomaly(double m0Rad, double ecc) {
		// start vlaues
		double eccAnomaly = m0Rad;
		double diff = eccAnomaly - ecc * Math.sin(eccAnomaly) - m0Rad;

		// iterate until tol condition met
		while (Math.abs(diff) > ITER_TOL) {
			// value of eccAnomaly for this cycle
			double de = diff / (1 - ecc * Math.cos(eccAnomaly));
			eccAnomaly = eccAnomaly - de;

			// compute new diff term
			diff = eccAnomaly - ecc * Math.sin(eccAnomaly) - m0Rad;
		}
		return eccAnomaly;
	}

	/*
	 * Computes Sun's mean ecliptic longitude at t <p> Ref: PA section 46 </p>
	 * @param t number Julian centuries since 1900 Jan 0.5
	 * @return time-corrected ecliptic longitude in range 0 to 360 deg
	 */
	protected static double eclipticLongDeg(double t) {
		return MathUtils.reduceToRange(eg0 + eg1 * t + eg2 * t * t, 360);
	}

	/*
	 * Computes Sun's perigee at time t <p> Ref: PA section 46 </p>
	 * @param t number Julian centuries since 1900 Jan 0.5
	 * @return time-corrected perigee in range 0 to 360 deg
	 */
	protected static double perigeeLongDeg(double t) {
		return MathUtils.reduceToRange(w0 + w1 * t + w2 * t * t, 360);
	}

	/*
	 * Computes eccentricity of the Sun-Earth orbit at t <p> Ref: PA section 46 </p>
	 * @param t number Julian centuries since 1900 Jan 0.5
	 * @return time-corrected orbit eccentricity
	 */
	protected static double orbitEccentricity(double t) {
		return (e0 + e1 * t + e2 * t * t);
	}

	/**
	 * Converts coordinates from ecliptic to equatorial system <p> Ref: PA section
	 * 27 </p>
	 * @param ecpLongDeg
	 *     ecliptic longitude in deg
	 * @param ecpLatDeg
	 *     ecliptic latitude in deg
	 * @param utcDate
	 *     observation utc date
	 * @return map containing equatorial coordinates ra in hour and dec in deg
	 */
	public static Map<CoordsConverter.CoordsEnum, Double> convertEclipticToEquatorial(double ecpLongDeg,
			double ecpLatDeg, LocalDate utcDate) {

		// computes ecliptic obliquity for current date
		double ecp = ecliptic(utcDate);

		// shorthand sin and cos terms
		double se = MathUtils.sind(ecp);
		double ce = MathUtils.cosd(ecp);

		// ecliptic -> decDeg
		double decTerm = MathUtils.sind(ecpLatDeg) * ce;
		decTerm += MathUtils.cosd(ecpLatDeg) * MathUtils.sind(ecpLongDeg) * se;
		double decDeg = Math.toDegrees(Math.asin(decTerm));

		// ecliptic -> raHr
		double raY = MathUtils.sind(ecpLongDeg) * ce - MathUtils.tand(ecpLatDeg) * se;
		double raX = MathUtils.cosd(ecpLongDeg);
		double raHr = Math.toDegrees(Math.atan2(raY, raX)) / 15.0;
		raHr = MathUtils.reduceToRange(raHr, 24.0);

		// compile map
		Map<CoordsConverter.CoordsEnum, Double> map = new HashMap<>();
		map.put(CoordsConverter.CoordsEnum.RA_HR, raHr);
		map.put(CoordsConverter.CoordsEnum.DEC_DEG, decDeg);
		return map;
	}

	/**
	 * Computes time-corrected ecliptic obliquity <p> Ref: PA section 27 </p>
	 * @param localDate
	 *     date to compute ecliptic obliquity
	 * @return J2000 ecliptic obliquity corrected for date
	 */
	private static double ecliptic(LocalDate localDate) {
		LocalDateTime utcDateTime = LocalDateTime.of(localDate, TimesConverter.LOCAL_TIME_0);
		double jd0 = TimesConverter.convertUtcToJD0(utcDateTime);
		double t = (jd0 - TimesConverter.JD_2000) / TimesConverter.C_DAYS;
		double de = (Solar.ECL_1 * t + Solar.ECL_2 * Math.pow(t, 2) + Solar.ECL_3 * Math.pow(t, 3)) / 3600;
		double obliquity = AstroCoords.dmsToDeg(Solar.ECL_0) + de;
		return obliquity;
	}

	public static void main(String[] args) {
		// gwch site
		// double siteLongDeg = 0.0;
		// double siteLatDeg = 55.0; // 42.37N
		// double siteElevation = 0.0;
		// ObservationSite gwchSite = new ObservationSite(siteLongDeg, siteLatDeg,
		// siteElevation, 0.0);
		//
		// LocalDate siteCivilDate = LocalDate.of(1979, 9, 22);
		// Solar solarTimes = new Solar(gwchSite, siteCivilDate);
		//
		// System.out.println(String.format("\nGreenwich Date: %s",
		// siteCivilDate.toString()));
		//

		// System.out.println(solarTimes.getSunSetValue());
		// System.out.println(solarTimes.getTwilightEndValue());
		// System.out.println(solarTimes.getTwilightStartValue());
		// System.out.println(solarTimes.getSunRiseValue());
		// System.out.println(String.format("Never sets: %b",
		// solarTimes.isSunNeverSets()));
		// // System.out.println(String.format("Never rises: %b",
		// solarTimes.isSunNeverRises()));
		//
		//// System.out.println();
		//// System.out.println(solarTimes.getUtcSunSet().toString());
		//// System.out.println(solarTimes.getUtcTwilightEnds().toString());
		//// System.out.println(solarTimes.getUtcTwilightStarts().toString());
		//// System.out.println(solarTimes.getUtcSunRise().toString());
		//
		double siteLong = -71.05; // 71.05W
		double siteLat = 42.37; // 42.37N
		double siteAlt = 0.0;
		double utcOffsetHr = -5.0;
		ObservationSite bostonSite = new ObservationSite(siteLong, siteLat, siteAlt, utcOffsetHr);

		LocalDate siteCivilDate = LocalDate.of(1986, 3, 10);
		Solar solar = new Solar(bostonSite);

		System.out.println(String.format("\nBoston Date: %s", siteCivilDate.toString()));

		// sunset and twilight end times end of observation day
		SolarTimes st = solar.getCivilSunTimes(siteCivilDate);
		System.out.println(st.toString());
		System.out.println();

		// sunrise and twilight start times start of observation dat
		st = solar.getCivilSunTimes(siteCivilDate.minusDays(1));
		System.out.println(st.toString());

		// solarTimes.isSunNeverSets()));
		//// System.out.println(String.format("Never rises: %b",
		// solarTimes.isSunNeverRises()));
		////
		//// System.out.println();
		//// System.out.println(solarTimes.getUtcSunSet().toString());
		//// System.out.println(solarTimes.getUtcTwilightEnds().toString());
		//// System.out.println(solarTimes.getUtcTwilightStarts().toString());
		//// System.out.println(solarTimes.getUtcSunRise().toString());
		//
		// siteCivilDate = LocalDate.of(1986, 3, 9);
		// solarTimes = new SolarTimes(bostonSite, siteCivilDate);
		//
		// System.out.println(String.format("\nBoston Date: %s",
		// siteCivilDate.toString()));
		// System.out.println(solarTimes.getSunSetValue());
		// System.out.println(solarTimes.getTwilightEndValue());
		// System.out.println(solarTimes.getTwilightStartValue());
		// System.out.println(solarTimes.getSunRiseValue());
		// System.out.println(String.format("Never sets: %b",
		// solarTimes.isSunNeverSets()));
		// System.out.println(String.format("Never rises: %b",
		// solarTimes.isSunNeverRises()));
		//
		// System.out.println();
		// System.out.println(solarTimes.getUtcSunSet().toString());
		// System.out.println(solarTimes.getUtcTwilightEnds().toString());
		// System.out.println(solarTimes.getUtcTwilightStarts().toString());
		// System.out.println(solarTimes.getUtcSunRise().toString());
	}
}
