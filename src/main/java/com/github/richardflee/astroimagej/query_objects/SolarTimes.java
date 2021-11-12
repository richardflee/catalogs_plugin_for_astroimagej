package com.github.richardflee.astroimagej.query_objects;

import java.time.LocalDateTime;

import com.github.richardflee.astroimagej.visibility_plotter.Solar;

public class SolarTimes {
	private LocalDateTime civilSunSet = null;
	private  LocalDateTime civilTwilightEnds;
	private LocalDateTime civilTwilightStarts;
	private LocalDateTime civilSunRise;
	
	private String civilSunSetValue = null;
	private String civilTwilightEndsValue = null;
	private String civilTwilightStartsValue = null;
	private String civilSunRiseValue = null;
	
	public SolarTimes() {
		
	}
	
	public SolarTimes(LocalDateTime civilSunSet, LocalDateTime civilTwilightEnds, 
			LocalDateTime civilTwilightStarts, LocalDateTime civilSunRise) {
		
		this.civilSunSet = civilSunSet;
		this.civilTwilightEnds = civilTwilightEnds;
		this.civilTwilightStarts = civilTwilightStarts;
		this.civilSunRise = civilSunRise;
		
		this.civilSunSetValue = Solar.LDT_FOMATTER.format(civilSunSet.plusSeconds(30));
		this.civilTwilightEndsValue = Solar.LDT_FOMATTER.format(civilTwilightEnds.plusSeconds(30));
		this.civilTwilightStartsValue =  Solar.LDT_FOMATTER.format(civilTwilightStarts.plusSeconds(30));
		this.civilSunRiseValue = Solar.LDT_FOMATTER.format(civilSunRise.plusSeconds(30));
	}

	
	public void setCivilSunSet(LocalDateTime civilSunSet) {
		this.setCivilSunSetValue(civilSunSet);
		this.civilSunSet = civilSunSet;
	}

	public void setCivilTwilightEnds(LocalDateTime civilTwilightEnds) {
		this.setCivilTwilightEndsValue(civilTwilightEnds);		
		this.civilTwilightEnds = civilTwilightEnds;
	}

	public void setCivilTwilightStarts(LocalDateTime civilTwilightStarts) {
		this.setCivilTwilightStartsValue(civilTwilightStarts);
		this.civilTwilightStarts = civilTwilightStarts;
	}

	public void setCivilSunRise(LocalDateTime civilSunRise) {
		setCivilSunRiseValue(civilSunRise);		
		this.civilSunRise = civilSunRise;
	}

	private void setCivilSunSetValue(LocalDateTime civilSunSet) {
		this.civilSunSetValue = Solar.LDT_FOMATTER.format(civilSunSet.plusSeconds(30));
	}


	private void setCivilTwilightEndsValue(LocalDateTime civilTwilightEnds) {
		this.civilTwilightEndsValue = Solar.LDT_FOMATTER.format(civilTwilightEnds.plusSeconds(30));
	}


	private void setCivilTwilightStartsValue(LocalDateTime civilTwilightStarts) {
		this.civilTwilightStartsValue =  Solar.LDT_FOMATTER.format(civilTwilightStarts.plusSeconds(30));
	}


	private void setCivilSunRiseValue(LocalDateTime civilSunRise) {
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

	public LocalDateTime getCivilSunSet() {
		return civilSunSet;
	}

	public LocalDateTime getCivilTwilightEnds() {
		return civilTwilightEnds;
	}

	public LocalDateTime getCivilTwilightStarts() {
		return civilTwilightStarts;
	}

	public LocalDateTime getCivilSunRise() {
		return civilSunRise;
	}

	@Override
	public String toString() {
		return "SolarTimes [civilSunSetValue=" + civilSunSetValue + ", civilTwilightEndsValue=" + civilTwilightEndsValue
				+ ", civilTwilightStartsValue=" + civilTwilightStartsValue + ", civilSunRiseValue=" + civilSunRiseValue
				+ "]";
	}
	
	


	
}
