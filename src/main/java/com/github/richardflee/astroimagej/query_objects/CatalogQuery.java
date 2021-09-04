package com.github.richardflee.astroimagej.query_objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * This class encapsulates catalog query parameters
 * <p>Defaults to wasp 12 default parameters for first use and and for unit tests</p>
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
		super("wasp 12", AstroCoords.raHms_To_raHr("06:30:32.797"), AstroCoords.decDms_To_decDeg("+29:40:20.27"));

		this.fovAmin = 60.0;
		this.magLimit = 15.0;
		this.catalogType = CatalogsEnum.VSP;
		this.magBand = "V";
	}

	/**
	 * Copy constructor
	 * 
	 * @param query source object to copy
	 */
	public CatalogQuery(CatalogQuery query) {
		super(query.getObjectId(), query.getRaHr(), query.getDecDeg());
		this.fovAmin = query.getFovAmin();
		this.magLimit = query.getMagLimit();
		this.catalogType = query.getCatalogType();
		this.magBand = query.getMagBand();
	}
	
	/**
	 * Compiles query items and header and query data into string array
	 * 
	 * @return two element string array comprising query items and current query data
	 */
	public String[] toFormattedString() {
		String[] lines = new String[2];
		
		// items header
		lines[0] = "#ObjectId, RA, Dec, Fov, MagLimit, Catalog, Filter\n";
		
		// compile data by QueryEnum index
		String[] terms = new String[QueryEnum.size];
		terms[QueryEnum.OBJECT_ID.getIndex()] = String.format("#%s", super.getObjectId());
		terms[QueryEnum.RA_HMS.getIndex()] = String.format("%s", AstroCoords.raHr_To_raHms(super.getRaHr()));
		terms[QueryEnum.DEC_DMS.getIndex()] = String.format("%s", AstroCoords.decDeg_To_decDms(super.getDecDeg()));
		terms[QueryEnum.FOV_AMIN.getIndex()] = String.format("%.1f", getFovAmin());
		terms[QueryEnum.MAG_LIMIT.getIndex()] = String.format("%.1f", getMagLimit());
		terms[QueryEnum.CATALOG_DROPDOWN.getIndex()] = String.format("%s", getCatalogType().toString());
		terms[QueryEnum.FILTER_DROPDOWN.getIndex()] = String.format("%s\n", getMagBand());

		// assemble data into comma-separated string
		lines[1] = Arrays.asList(terms).stream().collect(Collectors.joining(","));
		return lines;
	}

	/**
	 * Static method compiles query object from comma-separated text line compring query paramters
	 * 
	 * @param dataLine comma separated sequence of query data items
	 * @return query object compiled with dataLine parameters
	 */
	public static CatalogQuery fromFormattedString(String dataLine) {
		// strips leading '#' char indicating radec comment line
		dataLine = dataLine.replace("#", "");
		
		// loads comma-delimeted dataLine terms into aray, strip leading/trailing blank spaces
		CatalogQuery query = new CatalogQuery();
		String terms[] = dataLine.replace(" ", "").split(",");
		
		// load query object by QueryNum index
		String objectId = terms[QueryEnum.OBJECT_ID.getIndex()];
		query.setObjectId(objectId);

		String raHms = terms[QueryEnum.RA_HMS.getIndex()];
		query.setRaHr(AstroCoords.raHms_To_raHr(raHms));

		String decDms = terms[QueryEnum.DEC_DMS.getIndex()];
		query.setDecDeg((AstroCoords.decDms_To_decDeg(decDms)));

		String fovAmin = terms[QueryEnum.FOV_AMIN.getIndex()];
		query.setFovAmin(Double.valueOf(fovAmin));

		String magLimit = terms[QueryEnum.MAG_LIMIT.getIndex()];
		query.setMagLimit(Double.valueOf(magLimit));

		String catalogName = terms[QueryEnum.CATALOG_DROPDOWN.getIndex()];
		query.setCatalogType(CatalogsEnum.getEnum(catalogName));

		String magBand = terms[QueryEnum.FILTER_DROPDOWN.getIndex()];
		query.setMagBand(magBand);
		return query;
	}
	
	// geters setters
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

		System.out.println(String.format("\nCatalogQuery equals q1 = q2 : %b", query1.equals(query2)));

		System.out.println(String.format("\nDemo toFormattedString:\n %s", query1.toFormattedString()[1]));

		String dataLine = "#wasp 12,06:30:32.80,+29:40:20.27,60.0,15.0,VSP,V";
		CatalogQuery query3 = CatalogQuery.fromFormattedString(dataLine);
		System.out.println(String.format("Demo fromFormattedString:\n %s", query3.toString()));
	}
}
