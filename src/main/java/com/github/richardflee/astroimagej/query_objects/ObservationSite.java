package com.github.richardflee.astroimagej.query_objects;

import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * This class handles observation site geographical and time offset data
 */

public class ObservationSite {

	private double siteLongitudeDeg;
	private double siteLatitudeDeg;
	private double siteAlt;
	private double utcOffsetHr;

	/**
	 * Default site: Royal Observatory, Greenwich, UK
	 */
	public ObservationSite() {
		String longDms = "51:28:24.07";
		String latDms = "00:00:00.00";
		this.siteLongitudeDeg = AstroCoords.dmsToDeg(longDms);
		this.siteLatitudeDeg = AstroCoords.dmsToDeg(latDms);
		this.utcOffsetHr = 0.0;
	}

	/**
	 * Sets observatory geographic location <p> UTC defaults to GMT </p>
	 * @param longitude
	 *     geographic longitude in degrees; positive if E of Greenwich, negative
	 *     otherwise
	 * @param latitude
	 *     geographic latitude in degrees; positive if north, negative otherwise
	 * @param elevation
	 *     height in m
	 * @param utcOffsetHr
	 *     TODO
	 */
	public ObservationSite(double siteLongitudeDeg, double siteLatitudeDeg, double siteAlt, double utcOffsetHr) {
		this.siteLongitudeDeg = siteLongitudeDeg;
		this.siteLatitudeDeg = siteLatitudeDeg;
		this.siteAlt = siteAlt;
		this.utcOffsetHr = utcOffsetHr;
	}

	/**
	 * Converts coordinate in degrees to sexagesimal value with ':' delimiter
	 * @param locationDeg
	 *     longitude or latitude in degrees
	 * @return coordinate in sexagesimal format ±ddd:mm:ss.ss
	 */
	public static String locationDegToDms(double locationDeg) {
		// extract sign
		String sign = (locationDeg >= 0) ? "+" : "-";

		// extract ddd, mm, ss terms
		double data = Math.abs(locationDeg);
		int ddd = (int) (1.0 * data);
		int mm = (int) ((data - ddd) * 60);
		double ss = ((data - ddd) * 60 - mm) * 60;

		// return signed sexagesimal coordinate
		return sign + String.format("%02d", ddd) + ":" + String.format("%02d", mm) + ":"
				+ String.format("%5.2f", ss).replace(' ', '0');
	}

	

	public double getSiteLongitudeDeg() {
		return siteLongitudeDeg;
	}

	public void setSiteLongitudeDeg(double siteLongitudeDeg) {
		this.siteLongitudeDeg = siteLongitudeDeg;
	}

	public double getSiteLatitudeDeg() {
		return siteLatitudeDeg;
	}

	public void setSiteLatitudeDeg(double siteLatitudeDeg) {
		this.siteLatitudeDeg = siteLatitudeDeg;
	}

	public double getSiteAlt() {
		return siteAlt;
	}

	public void setSiteAlt(double siteAlt) {
		this.siteAlt = siteAlt;
	}

	public double getUtcOffsetHr() {
		return utcOffsetHr;
	}

	public void setUtcOffsetHr(double utcOffsetHr) {
		this.utcOffsetHr = utcOffsetHr;
	}

	
	
	@Override
	public String toString() {
		return "ObservationSite [siteLongitudeDeg=" + siteLongitudeDeg + ", siteLatitudeDeg=" + siteLatitudeDeg
				+ ", siteAlt=" + siteAlt + ", utcOffsetHr=" + utcOffsetHr + "]";
	}

	public static void main(String[] args) {
		ObservationSite moore = new ObservationSite();
		System.out.println(
				String.format("Default longitude -85:31:42.51: %s", locationDegToDms(moore.getSiteLongitudeDeg())));
		System.out.println(
				String.format("Default latitude +38:20:41.25: %s", locationDegToDms(moore.getSiteLatitudeDeg())));

		// Siding Spring Aus
		// Siding Spring Obs. Coonabarabran/Siding Spring,
		// N.S.W. -31.2733333333333 149.061666666667 1149
		// UTC offset +11:00 (AEDT)
		ObservationSite siding = new ObservationSite(149.06166666, -31.273333333, 1149, 0.0);

		System.out.println(
				String.format("Default longitude +149:03:42: %s", locationDegToDms(siding.getSiteLongitudeDeg())));
		System.out.println(
				String.format("Default latitude -31:16:24: %s", locationDegToDms(siding.getSiteLatitudeDeg())));

		System.out.println();
		System.out.println(siding.toString());
	}

}
