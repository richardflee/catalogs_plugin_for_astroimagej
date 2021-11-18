package com.github.richardflee.astroimagej.query_objects;

import java.time.LocalDateTime;

import org.jfree.data.time.Minute;

import com.github.richardflee.astroimagej.utils.MathUtils;
import com.github.richardflee.astroimagej.visibility_plotter.TimesConverter;

/**
 * Encapsulates a single object visibility values plus star track polar plot
 * coordinates
 */
public class AltAzmObject {
	// class variable
	private static String name = null;
	
	// field
	private LocalDateTime civilDateTime = null;
	private double altDeg = 0.0;
	private double azmDeg = 0.0;
	private double polarX = 0.0;
	private double polarY = 0.0;

	public AltAzmObject(LocalDateTime civilDateTime, double altDeg, double azmDeg) {
		this.civilDateTime = civilDateTime;
		this.altDeg = altDeg;
		this.azmDeg = azmDeg;

		// computes polar chart X, Y coordinates
		// azimuth = 0 at N, increasing eastwards; radial distance = object zenith angle
		double zenDeg = 90.0 - altDeg;
		this.polarX = -1.0 * zenDeg * MathUtils.sind(azmDeg);
		this.polarY = zenDeg * MathUtils.cosd(azmDeg);
	}
	
	public Minute getCurrentMinute() {
		return TimesConverter.convertCivilDateTimeToMinute(this.civilDateTime);
	}

	public LocalDateTime getCivilDateTime() {
		return civilDateTime;
	}

	public double getAltDeg() {
		return altDeg;
	}

	public double getAzmDeg() {
		return azmDeg;
	}

	public double getPolarX() {
		return polarX;
	}

	public double getPolarY() {
		return polarY;
	}
	

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		AltAzmObject.name = name;
	}

	public static void main(String[] args) {
		double altDeg = 12.71;
		double azmDeg = 62.057;
		AltAzmObject aa1 = new AltAzmObject(null, altDeg, azmDeg);
		System.out.println(String.format("PolarX -68.279084: %3.6f", aa1.getPolarX()));
		System.out.println(String.format("PolarY  36.217458: %3.6f", aa1.getPolarY()));

		altDeg = 60.967;
		azmDeg = 97.124;
		AltAzmObject aa2 = new AltAzmObject(null, altDeg, azmDeg);
		System.out.println(String.format("\nPolarX -28.808867: %3.6f", aa2.getPolarX()));
		System.out.println(String.format("PolarY -3.600589: %3.6f", aa2.getPolarY()));

		altDeg = 76.999;
		azmDeg = 231.75;
		AltAzmObject aa3 = new AltAzmObject(null, altDeg, azmDeg);
		System.out.println(String.format("\nPolarX 10.209905: %3.6f", aa3.getPolarX()));
		System.out.println(String.format("PolarY   -8.048840: %3.6f", aa3.getPolarY()));

		altDeg = 28.416;
		azmDeg = 286.843;
		AltAzmObject aa4 = new AltAzmObject(null, altDeg, azmDeg);
		System.out.println(String.format("\nPolarX 58.942189: %3.6f", aa4.getPolarX()));
		System.out.println(String.format("PolarY   17.843975: %3.6f", aa4.getPolarY()));

	}
}
