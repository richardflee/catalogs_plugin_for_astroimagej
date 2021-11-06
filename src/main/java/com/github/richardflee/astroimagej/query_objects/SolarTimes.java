package com.github.richardflee.astroimagej.query_objects;

import java.time.LocalTime;

import com.github.richardflee.astroimagej.visibility_plotter.Solar;

public class SolarTimes {
	
	private String civilSunSetValue = null;
	private String civilTwilightEndsValue = null;
	private String civilTwilightStartsValue = null;
	private String civilSunRiseValue = null;
	
	public SolarTimes() {
		
	}
	
	public SolarTimes(LocalTime civilSunSet, LocalTime civilTwilightEnds, 
			LocalTime civilTwilightStarts, LocalTime civilSunRise) {
		
		this.civilSunSetValue = Solar.LDT_FOMATTER.format(civilSunSet.plusSeconds(30));
		this.civilTwilightEndsValue = Solar.LDT_FOMATTER.format(civilTwilightEnds.plusSeconds(30));
		this.civilTwilightStartsValue =  Solar.LDT_FOMATTER.format(civilTwilightStarts.plusSeconds(30));
		this.civilSunRiseValue = Solar.LDT_FOMATTER.format(civilSunRise.plusSeconds(30));
	}

	public String getCivilSunSetValue() {
		return civilSunSetValue;
	}

	public String getCivilTwilightEndsValue() {
		return civilTwilightEndsValue;
	}
	
	public String getCivilTwilightStartsValue() {
		return civilTwilightStartsValue;
	}
	
	public String getCivilSunRiseValue() {
		return civilSunRiseValue;
	}
	
	
	public void setCivilSunSetValue(LocalTime civilSunSet) {
		this.civilSunSetValue = Solar.LDT_FOMATTER.format(civilSunSet.plusSeconds(30));
	}


	public void setCivilTwilightEndsValue(LocalTime civilTwilightEnds) {
		this.civilTwilightEndsValue = Solar.LDT_FOMATTER.format(civilTwilightEnds.plusSeconds(30));
	}


	public void setCivilTwilightStartsValue(LocalTime civilTwilightStarts) {
		this.civilTwilightStartsValue =  Solar.LDT_FOMATTER.format(civilTwilightStarts.plusSeconds(30));
	}


	public void setCivilSunRiseValue(LocalTime civilSunRise) {
		this.civilSunRiseValue = Solar.LDT_FOMATTER.format(civilSunRise.plusSeconds(30));
	}

	@Override
	public String toString() {
		return "SolarTimes [civilSunSetValue=" + civilSunSetValue + ", civilTwilightEndsValue=" + civilTwilightEndsValue
				+ ", civilTwilightStartsValue=" + civilTwilightStartsValue + ", civilSunRiseValue=" + civilSunRiseValue
				+ "]";
	}
	
	


	
}
