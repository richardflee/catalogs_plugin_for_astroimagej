package com.github.richardflee.astroimagej.query;

import com.github.richardflee.astroimagej.utils.AstroCoords;


/**
 * Base class for target and comparison objects
 */
public class AbstractFieldObject {
	
	protected String objectId = null;
	protected double raHr = 0.0;
	protected double decDeg = 0.0;
	
	/**
	 * Base class for query objects. Autogenerate a name for APASS catalog objects
	 * 
	 * @param objectId catalog object name (null APASS catalog)
	 * @param raHr object RA2000 coordinate in units Hr, range 0.0 - 24.0
	 * @param decDeg object DEC2000 coordinate in units deg, range ±90°
	 */
	public AbstractFieldObject(String objectId, double raHr, double decDeg) {
		this.raHr = raHr;
		this.decDeg = decDeg;
		setObjectId(objectId);
	}
	
	/*
	 * Compiles coordinate-based name for (unmaed) APASS objects
	 * @return object name format: HHMMSSSS±DDMMSSSS
	 */
	private String compileObjectId() {
		String raHms = AstroCoords.raHr_To_raHms(raHr);
		String decDms = AstroCoords.decDeg_To_decDms(decDeg);
		String id = raHms + decDms;
		return id.replace(":", "").replace(".", "");			
	}
	
	/**
	 * Sets coordinate-based name for APASS objects
	 * @param objectId object name format: HHMMSSSS+/-DDMMSSSS
	 */
	public void setObjectId(String objectId) {
		this.objectId = (objectId != null) ? objectId : compileObjectId();
	}
	
	public String getObjectId() {
		return objectId;
	}


	public double getRaHr() {
		return raHr;
	}

	public void setRaHr(double raHr) {
		this.raHr = raHr;
	}

	public double getDecDeg() {
		return decDeg;
	}

	public void setDecDeg(double decDeg) {
		this.decDeg = decDeg;
	}

	@Override
	public String toString() {
		return "AbstractFieldObject [objectId=" + objectId + ", raHr=" + raHr + ", decDeg=" + decDeg + "]";
	}
	
	public static void main(String[] args) {
		
		String raHms = "06:30:32.80";
		String decDms = "+29:40:20.27";
		String expectedId = "06303280+29402027";
		
		double raHr = AstroCoords.raHms_To_raHr(raHms);
		double decDeg = AstroCoords.decDms_To_decDeg(decDms);
		AbstractFieldObject afo = new AbstractFieldObject(null, raHr, decDeg);
		
		String compiledId = afo.getObjectId();
		boolean match = compiledId.contentEquals(expectedId);
		
		System.out.println(String.format("expected, compiled = %s, %s, %b", expectedId, compiledId, match));
		
	}
}
