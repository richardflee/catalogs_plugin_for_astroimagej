package com.github.richardflee.astroimagej.visibility_plotter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.jfree.data.time.Minute;

import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.utils.MathUtils;

/**
 * Practical Astronomy time methods to compute site sidereal time <p> Ref: PA-
 * Practical Astronomy with Your Calculator and Spreadsheet, Duffett-Smith </p>
 * <p> Ref: Java compilation:
 * https://github.com/jfcarr-practical-astronomy/practical-astronomy-java jfcarr
 * </p>
 */
public class TimesConverter {

	public static final LocalDateTime EPOCH_1950 = LocalDateTime.of(1950, 1, 1, 0, 0, 0);

	public static final LocalDateTime EPOCH_2000 = LocalDateTime.of(2000, 1, 1, 12, 0, 0);
	public static final double JD_2000 = TimesConverter.convertUtcToJD(EPOCH_2000); // 2451545.00000

	public static final LocalDateTime EPOCH_2010 = LocalDateTime.of(2009, 12, 31, 0, 0, 0);
	public static final double JD_2010 = TimesConverter.convertUtcToJD(EPOCH_2010); // 2455196.50000

	// solar_dya / sidereal day
	public static final double SIDEREAL_FACTOR = 1.002737909;

	// century in days
	public static final double C_DAYS = 36525.0;

	// LocalTime constants for preceding midnight and noon
	public static final LocalTime LOCAL_TIME_0 = LocalTime.of(0, 0, 0);
	public static final LocalTime LOCAL_TIME_NOON = LocalTime.of(12, 0, 0);

	// rise-set fail conditions
	public static final double NEVER_RISES_HR = 0.0;
	public static final double NEVER_SETS_HR = 12.0;

	public static final LocalTime NEVER_RISES_UTC = LOCAL_TIME_0;
	public static final LocalTime NEVER_SETS_UTC = LOCAL_TIME_NOON;

	public static final double NEVER_RISES_DEG = 0.0;
	public static final double NEVER_SETS_DEG = 180.0;

	// arcane constants to compute JD
	static final double C_FACTOR = 365.25; // approx days in year
	static final double D_FACTOR = 30.6001;
	static final double JD_BASE = 1720994.5; // UT -1-10-28T00:00:00 (0:0:0.00 UT on Oct 28, -1)

	// constants to compute GST & quadratic const
	private static final double T00_COEF = 6.697374558;
	private static final double T01_COEF = 2400.051336;
	private static final double T02_COEF = 0.000025862;

	private ObservationSite site = null;
	private double utcOffsetHr = 0.0;

	// epoch date times
	public static final LocalDateTime EPOCH_1900 = LocalDateTime.of(1899, 12, 31, 12, 0, 0);
	public static final double JD_1900 = convertUtcToJD(EPOCH_1900); // 2415020.00000

	public TimesConverter(ObservationSite site) {
		this.site = site;
		this.utcOffsetHr = site.getUtcOffsetHr();
	}

	/**
	 * Converts UTC to Greenwich Sidereal Time (GST) <p> Ref: PA section 12 </p>
	 * @param utcDateTime
	 *     UTC or Greenwich date-time
	 * @return GST LocalTime in 24 hr range
	 */
	public LocalDateTime convertUtcToGst(LocalDateTime utcDateTime) {
		double utcHr = TimesConverter.convertLocalTimeToHours(utcDateTime.toLocalTime());
		double t0 = utcGstFactor(utcDateTime);
		double gstHr = MathUtils.reduceToRange(utcHr * TimesConverter.SIDEREAL_FACTOR + t0, 24);
		LocalTime gstTime = TimesConverter.convertHoursToLocalTime(gstHr);
		return LocalDateTime.of(utcDateTime.toLocalDate(), gstTime);
	}

	/**
	 * Computes Julian Date for specified Greenwich date-time <p> Ref: PA section 4,
	 * restricted to Gregorian calender (post 1582 ..) </p>
	 * @param utcDateTime
	 *     Greenwich local time in Gregorian calendar
	 * @return Julian Date
	 */
	public static double convertUtcToJD(LocalDateTime utcDateTime) {
		// compile time terms
		long elapsed = utcDateTime.getHour() * 3600 + utcDateTime.getMinute() * 60 + utcDateTime.getSecond();
		double dayFrac = (double) (elapsed) / (24.0 * 3600);
		double fDay = (double) utcDateTime.getDayOfMonth() + dayFrac;
		double fMonth = (double) utcDateTime.getMonthValue();
		double fYear = (double) utcDateTime.getYear();

		// treats Jan or Feb as 13th or 14th months of the previous year
		double y = (fMonth < 3) ? fYear - 1 : fYear;
		double m = (fMonth < 3) ? fMonth + 12 : fMonth;

		// compile JD number ..
		double a = Math.floor(y / 100);
		double b = 2 - a + Math.floor(a / 4);
		double c = Math.floor(TimesConverter.C_FACTOR * y);
		double d = Math.floor(TimesConverter.D_FACTOR * (m + 1.0));
		return b + c + d + fDay + TimesConverter.JD_BASE;
	}

	/**
	 * Computes Julian Date at 0h on on specified Greenwich date
	 * @param utcDateTime
	 *     Greenwich local time
	 * @return Julian Date at 0h on that day
	 */
	public static double convertUtcToJD0(LocalDateTime utcDateTime) {
		LocalDateTime dt0 = LocalDateTime.of(utcDateTime.toLocalDate(), TimesConverter.LOCAL_TIME_0);
		return TimesConverter.convertUtcToJD(dt0);
	}

	/**
	 * Converts JFreeChart Minute quantity to LocalDateTime
	 */
	public static LocalDateTime convertMinuteToCivilDateTime(Minute current, LocalDate civilDate) {
		int hour = current.getHourValue();
		int minute = current.getMinute();
		LocalTime currentTime = LocalTime.of(hour, minute);
		return LocalDateTime.of(civilDate, currentTime);
	}

	/**
	 * Converts LocalDateTime to JFreeChart Minute quantity
	 */
	public static Minute convertCivilDateTimeToMinute(LocalDateTime civilDateTime) {
		Instant instant = civilDateTime.toInstant(ZoneOffset.UTC);
		Date date = Date.from(instant);
		return new Minute(date);
	}

	public static Long convertCivilDateTimeToMillis(LocalDateTime civilDateTime) {
		Minute minute = TimesConverter.convertCivilDateTimeToMinute(civilDateTime);
		return minute.getFirstMillisecond();
	}
	
	/**
	 * Converts the local site time including utc offset to GMT / UTC <p> Ref: PA
	 * section 9 </p>
	 * @param siteCivilDateTime
	 *     utc offset local date-time
	 * @param site
	 *     observation location geographic and time parameters
	 * @return utc date-time
	 */
	public LocalDateTime convertCivilDateTimeToUtc(LocalDateTime siteCivilDateTime) {
		int utcOffsetMins = (int) (60.0 * utcOffsetHr);
		LocalDateTime utcDateTime = siteCivilDateTime.minusMinutes(utcOffsetMins);
		return utcDateTime;
	}

	/**
	 * Converts GMT / UTC to the UTC offset local site time <p> Based on PA section
	 * 10 </p>
	 * @param utcDateTime
	 *     UTC / GMT date-time
	 * @param site
	 *     geographic and time parameters
	 * @return site date-time
	 */
	public LocalDateTime convertUtcToCivilDateTime(LocalDateTime utcDateTime) {
		int utcOffsetMins = (int) (60.0 * utcOffsetHr);
		LocalDateTime siteDateTime = utcDateTime.plusMinutes(utcOffsetMins);
		return siteDateTime;
	}

	/**
	 * Converts local time to decimal hours
	 * @param localTime
	 *     input
	 * @return decimal hours
	 */
	public static double convertLocalTimeToHours(LocalTime localTime) {
		double hh = localTime.getHour();
		double mm = localTime.getMinute() / 60.0;
		double ss = (localTime.getSecond() + localTime.getNano() * 1e-9) / 3600.0;
		return hh + mm + ss;
	}

	/**
	 * Converts decimal houts to LocalTime format
	 * @param hr
	 *     input
	 * @return local time
	 */
	public static LocalTime convertHoursToLocalTime(double hr) {
		double reducedTime = MathUtils.reduceToRange(hr, 24.0);
		int hh = (int) (1.0 * reducedTime);
		int mm = (int) ((reducedTime - hh) * 60);
		Double ss = (3600 * (reducedTime - hh) - 60 * mm);
		Double nano = (ss - ss.intValue()) * 1e9;
		return LocalTime.of(hh, mm, ss.intValue(), nano.intValue());
	}

	/**
	 * Converts Greenwich Sidereal Time (GST) to UTC <p> Ref: PA section 13 </p>
	 * @param gstDateTime
	 *     Greenwich date-time
	 * @return UTC LocalTime in range 0h to 23h 56m 04s
	 */
	public LocalDateTime convertGstToUtc(LocalDateTime gstDateTime) {
		double gstHr = TimesConverter.convertLocalTimeToHours(gstDateTime.toLocalTime());
		double t0 = utcGstFactor(gstDateTime);
		double utcHr = MathUtils.reduceToRange(gstHr - t0, 24) / TimesConverter.SIDEREAL_FACTOR;
		LocalTime utcTime = TimesConverter.convertHoursToLocalTime(utcHr);
		return LocalDateTime.of(gstDateTime.toLocalDate(), utcTime);
	}

	/**
	 * Converts Greenwich Sidereal time (GST) to Local Sidereal Time (LST) <p> Ref:
	 * PA section 14 </p>
	 * @param gstLocalTime
	 *     Greenwich Sidereal Time
	 * @param site
	 *     observation location geographic and time parameters
	 * @return Local Sidereal Time at observation site
	 */
	public LocalDateTime convertGstToLst(LocalDateTime gstDateTime) {
		double gstHr = TimesConverter.convertLocalTimeToHours(gstDateTime.toLocalTime());
		double siteLongOffsetHr = getSite().getSiteLongitudeDeg() / 15.0;
		double lstHr = MathUtils.reduceToRange(gstHr + siteLongOffsetHr, 24);
		LocalTime lstTime = TimesConverter.convertHoursToLocalTime(lstHr);
		return LocalDateTime.of(gstDateTime.toLocalDate(), lstTime);
	}

	/**
	 * Converts Local Sidereal Time (LST) to Greenwich Sidereal time (GST) <p> Ref:
	 * PA section 15 </p>
	 * @param lstLocalTime
	 *     Local Sidereal Time
	 * @param site
	 *     observation location geographic and time parameters
	 * @return Greenwich Sidereal Time
	 */
	public LocalDateTime convertLstToGst(LocalDateTime lstDateTime) {
		double lstHr = TimesConverter.convertLocalTimeToHours(lstDateTime.toLocalTime());
		double siteLongOffsetHr = getSite().getSiteLongitudeDeg() / 15.0;
		double gstHr = MathUtils.reduceToRange(lstHr - siteLongOffsetHr, 24);
		LocalTime gstTime = TimesConverter.convertHoursToLocalTime(gstHr);
		return LocalDateTime.of(lstDateTime.toLocalDate(), gstTime);
	}

	public LocalDateTime convertUtcToLst(LocalDateTime utcDateTime) {
		LocalDateTime gstDateTime = convertUtcToGst(utcDateTime);
		LocalDateTime lstDateTime = convertGstToLst(gstDateTime);
		return lstDateTime;
	}

	public LocalDateTime convertLstToUtc(LocalDateTime lstDateTime) {
		LocalDateTime gstDateTime = convertLstToGst(lstDateTime);
		LocalDateTime utcDateTime = convertGstToUtc(gstDateTime);
		return utcDateTime;
	}

	/*
	 * Code common to GST <-> UTC time conversions
	 */
	private double utcGstFactor(LocalDateTime dateTime) {
		// Julian date at start of day (0h)
		double jd0 = TimesConverter.convertUtcToJD0(dateTime);
		double t = (jd0 - TimesConverter.JD_2000) / TimesConverter.C_DAYS;
		double t0 = MathUtils.reduceToRange(T00_COEF + (T01_COEF * t) + (T02_COEF * t * t), 24);
		return t0;
	}

	// getters & setters
	public ObservationSite getSite() {
		return site;
	}

	public void setSite(ObservationSite site) {
		this.site = site;
	}

	public static void main(String[] args) {
		// moore 45N 85W
		double siteLong = AstroCoords.dmsToDeg("-85:31:42.51");
		double siteLat = AstroCoords.dmsToDeg("+38:20:41.25");
		double siteElevation = 100.0;
		ObservationSite mooreSite = new ObservationSite(siteLong, siteLat, siteElevation, 0.0);
		TimesConverter mooreTimesConverter = new TimesConverter(mooreSite);

		// utc -> lst
		LocalDateTime utcDateTime = LocalDateTime.of(2020, 2, 24, 2, 0, 0);

		LocalDateTime lstDateTime = mooreTimesConverter.convertUtcToLst(utcDateTime);

		System.out.println(utcDateTime.toString());
		System.out.println(lstDateTime.toString());

		LocalDateTime x = mooreTimesConverter.convertLstToUtc(lstDateTime);
		System.out.println(x.toString());
		System.out.println();

		// double conversion JFree Minute <-> LocalDateTime
		Minute current = TimesConverter.convertCivilDateTimeToMinute(utcDateTime);
		LocalDateTime minToLDT = TimesConverter.convertMinuteToCivilDateTime(current, utcDateTime.toLocalDate());

		System.out.println(String.format("Start date-time:                  %s", utcDateTime.toString()));
		System.out.println(String.format("Minute date conversion:           %s", current.toString()));
		System.out.println(String.format("End date after double conversion: %s", minToLDT.toString()));
	}
}
