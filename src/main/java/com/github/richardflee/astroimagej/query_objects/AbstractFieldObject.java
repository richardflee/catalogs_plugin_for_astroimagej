package com.github.richardflee.astroimagej.query_objects;

import com.github.richardflee.astroimagej.utils.AstroCoords;

public class AbstractFieldObject {
	
	protected String objectId = null;
	protected double raHr = 0.0;
	protected double decDeg = 0.0;
	
	public AbstractFieldObject(String objectId, double raHr, double decDeg) {
		this.raHr = raHr;
		this.decDeg = decDeg;
		setObjectId(objectId);
	}
	
	private String compileObjectId() {
		String raHms = AstroCoords.raHr_To_raHms(raHr);
		String decDms = AstroCoords.decDeg_To_decDms(decDeg);
		String id = raHms + decDms;
		return id.replace(":", "").replace(".", "");			
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = (objectId != null) ? objectId : compileObjectId();
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
	
	

}
