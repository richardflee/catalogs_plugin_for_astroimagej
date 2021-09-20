package com.github.richardflee.astroimagej.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Catalog ui jtext field identifiers to enable validation of user inputs
 */
public enum QueryEnum {
	// values = <index>.<jtextfield_id>
	OBJECT_ID("0.objectidfield"), 
	RA_HMS("1.rafield"),
	DEC_DMS("2.decfield"), 
	FOV_AMIN("3.fovaminfield"), 
	MAG_LIMIT("4.maglimitfield"),
	CATALOG_DROPDOWN("5.nofield"),
	FILTER_DROPDOWN("6.nofield");
	
	private String strVal;
	private static final Map<String, QueryEnum> getEnumMap =  new HashMap<>();
	
	QueryEnum(String value) {
		this.strVal = value;
	}
	
	/**
	 * enum index
	 * @return enum index 
	 */
	public int getIndex() {	
		String [] splitStr = this.strVal.split("\\.");		
		return Integer.valueOf(splitStr[0]);
	}
	
	/**
	 * JTextField identifier
	 * @return enum identifier
	 */
	public String getFieldId() {	
		String [] splitStr = this.strVal.split("\\.");		
		return splitStr[1];
	}
	
	/**
	 * Maps fieldName to associated enum value
	 *  
	 * @param fieldName enum field value, e.g. objectId for enum OBJECT_ID 
	 * @return enum value for this field value
	 */
	public static QueryEnum getEnum(String value) {
		return getEnumMap.get(String.valueOf(value));
	}
	
	
	// initialise field_id look-up map 
		static {
			for (final QueryEnum en : QueryEnum.values()) {
				// split strVal and return 2nd element
				getEnumMap.put(en.strVal.split("\\.")[1], en);
			}
		}
	

	/**
	 * Total number of enum items
	 */
	public static final int size;
	static {
		size = values().length;
	}	

	public static void main(String[] args) {
		
		System.out.println(String.format("OBJECT_ID Field name: %s", QueryEnum.OBJECT_ID.getFieldId()));
		System.out.println(String.format("FOV_AMIN Field name: %s", QueryEnum.FOV_AMIN.getFieldId()));
		
		System.out.println();
		System.out.println(String.format(
				"Lookup OBJECT_ID by field name: %s =>  %s", "objectidfield", QueryEnum.getEnum("objectidfield").toString()));
		
		System.out.println();
		System.out.println(String.format(
				"Lookup index from enum: %s %01d", QueryEnum.OBJECT_ID.toString(), QueryEnum.OBJECT_ID.getIndex()));
		System.out.println(String.format(
				"Lookup index from enum: %s %01d", QueryEnum.FOV_AMIN.toString(), QueryEnum.FOV_AMIN.getIndex()));
		System.out.println(String.format(
				"Lookup index from enum: %s %01d", QueryEnum.CATALOG_DROPDOWN.toString(), QueryEnum.CATALOG_DROPDOWN.getIndex()));
		System.out.println(String.format(
				"Lookup index from enum: %s %01d", QueryEnum.FILTER_DROPDOWN.toString(), QueryEnum.FILTER_DROPDOWN.getIndex()));
	}

}
