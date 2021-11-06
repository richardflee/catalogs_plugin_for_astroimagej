package com.github.richardflee.astroimagej.query_objects;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.github.richardflee.astroimagej.utils.MathUtils;
import com.github.richardflee.astroimagej.visibility_plotter.TimesConverter;

public class PrecessionObject extends BaseFieldObject {

	// constants to compute precession coordinate corrections
	// Ref: Practical Astronomy Spreadsheet section 34
	private static final double RA0_2000_HR = 3.07420 / 3600.0;
	private static final double RA1_2000_HR = 1.33589 / 3600.0;
	private static final double DE_2000_DEG = 20.0383 / 3600.0;
	
	// maximum dec to add precession term
	private static final double MAX_DEC = 82.0;

	public PrecessionObject(String objectId, double raHr, double decDeg) {
		super(objectId, raHr, decDeg);
	}

	/**
	 * Adds precession correction to J2000 RA for dec < 80°
	 * <p>
	 * Ref: Practical Astronomy Spreadsheet section 34
	 * </p>
	 * @param utcDateTime time of observation
	 * @return object RA corrected for precession
	 */
	public double precessRa2000(LocalDateTime utcDateTime) {
		double precessRaHr = 0.0;
		if (decDeg < MAX_DEC) {
			double nYr = ChronoUnit.DAYS.between(TimesConverter.EPOCH_2000, utcDateTime) / 365.25;
			double raDeg = raHr * 15.0;

			double annualRaPrecess = RA0_2000_HR + RA1_2000_HR * MathUtils.sind(raDeg) * MathUtils.tand(decDeg);
			precessRaHr = annualRaPrecess * nYr;
		}
		return raHr + precessRaHr;
	}

	/**
	 * Adds precession correction to J2000 Dec for dec < 80°
	 * <p>
	 * Ref: Practical Astronomy Spreadsheet section 34
	 * </p>
	 * @param utcDateTime time of observation
	 * @return object DEC corrected for precession
	 */
	public double precessDec2000(LocalDateTime utcDateTime) {
		double precessDecDeg = 0.0;
		if (decDeg < MAX_DEC) {
			double nYr = ChronoUnit.DAYS.between(TimesConverter.EPOCH_2000, utcDateTime) / 365.25;
			double raDeg = raHr * 15.0;

			double annualDecPrecess = DE_2000_DEG * MathUtils.cosd(raDeg);
			precessDecDeg = annualDecPrecess * nYr;
		}
		return decDeg + precessDecDeg;
	}
}
