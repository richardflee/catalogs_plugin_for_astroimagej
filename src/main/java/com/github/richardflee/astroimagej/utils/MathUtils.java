package com.github.richardflee.astroimagej.utils;

public class MathUtils {

	/**
	 * Adapts Math sine function to degree 
	 */
	public static double sind(double angleDeg) {
		double angleRad = Math.toRadians(angleDeg);
		return Math.sin(angleRad);
	}
	
	/**
	 * Adapts Math cos function to degree 
	 */
	public static double cosd(double angleDeg) {
		double angleRad = Math.toRadians(angleDeg);
		return Math.cos(angleRad);
	}
	
	/**
	 * Adapts Math tan function to degree 
	 */
	public static double tand(double angleDeg) {
		return sind(angleDeg) / cosd(angleDeg);
	}
	
	/**
	 * Reduces signed numbers to lie within specified range 
	 * 
	 * @param numVal number to reduced to specified range; may be positive or negative
	 * @param range number range 0..rngVal; takes absolute range value 
	 * @return reduced number in specified range
	 */
	public static double reduceToRange(double numVal, double range) {
		range = Math.abs(range);
		return numVal - (range * Math.floor(numVal / range));
	}
	
}
