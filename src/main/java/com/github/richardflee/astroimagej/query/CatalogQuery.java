package com.github.richardflee.astroimagej.query;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Encapsulates catalog query parameters
 * <p> Defaults to wasp 12 default parameters for first use and and for unit tests</p>
 */
public class CatalogQuery extends AbstractFieldObject {
	private double fovAmin = 0.0;
	private double magLimit = 0.0;
	private CatalogsEnum catalogType = null;
	private String magBand = null;
	
	/**
	 * No arguments constructor defaults to WASP12 parameters
	 */
	public CatalogQuery() {
		super("wasp 12", 
				AstroCoords.raHms_To_raHr("06:30:32.797"),
				AstroCoords.decDms_To_decDeg("+29:40:20.27"));
		
		this.fovAmin = 60.0;
		this.magLimit = 15.0;
		this.catalogType = CatalogsEnum.VSP;
		this.magBand = "V";
	}
	
	/**
	 * Copy constructor
	 * @param query source object to copy
	 */
	public CatalogQuery(CatalogQuery query) {
		super(query.getObjectId(), query.getRaHr(), query.getDecDeg());
		this.fovAmin = query.getFovAmin();
		this.magLimit = query.getMagLimit();
		this.catalogType = query.getCatalogType();
		this.magBand = query.getMagBand();
	}

	public Double getFovAmin() {
		return fovAmin;
	}

	public void setFovAmin(double fovAmin) {
		this.fovAmin = fovAmin;
	}

	public double getMagLimit() {
		return magLimit;
	}

	public void setMagLimit(double magLimit) {
		this.magLimit = magLimit;
	}

	public CatalogsEnum getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(CatalogsEnum catalogType) {
		this.catalogType = catalogType;
	}

	public String getMagBand() {
		return magBand;
	}

	public void setMagBand(String magBand) {
		this.magBand = magBand;
	}

		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catalogType == null) ? 0 : catalogType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(fovAmin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((magBand == null) ? 0 : magBand.hashCode());
		temp = Double.doubleToLongBits(magLimit);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CatalogQuery other = (CatalogQuery) obj;
		if (catalogType != other.catalogType)
			return false;
		if (Double.doubleToLongBits(fovAmin) != Double.doubleToLongBits(other.fovAmin))
			return false;
		if (magBand == null) {
			if (other.magBand != null)
				return false;
		} else if (!magBand.equals(other.magBand))
			return false;
		if (Double.doubleToLongBits(magLimit) != Double.doubleToLongBits(other.magLimit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CatalogQuery [fovAmin=" + fovAmin + ", magLimit=" + magLimit + ", catalogType=" + catalogType
				+ ", magBand=" + magBand + ", objectId=" + objectId + ", raHr=" + raHr + ", decDeg=" + decDeg + "]";
	}
	
	public static void main(String[] args) {
		CatalogQuery query1 = new CatalogQuery();
		CatalogQuery query2 = new CatalogQuery(query1);
		
		System.out.println(query1.toString());
		System.out.println(query2.toString());
		
		System.out.println(String.format("CatalogQuery equals q1 = q2 : %b", query1.equals(query2)));
	}
}


