package com.github.richardflee.astroimagej.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Catalog ui jtext field identifiers to enable validation of user inputs
 */
public enum JTextFieldsEnum {
	OBJECT_ID("objectidfield"), 
	RA_HMS("rafield"),
	DEC_DMS("decfield"), 
	FOV_AMIN("fovaminfield"), 
	MAG_LIMIT("maglimitfield");
	
	private String strVal;
	private static final Map<String, JTextFieldsEnum> map =  new HashMap<>();
	
	JTextFieldsEnum(String value) {
		this.strVal = value;
	}
	
	public String getValue() {
		return this.strVal;
	}
	
	
	/**
	 * Maps fieldName to associated enum value
	 *  
	 * @param fieldName enum field value, e.g. objectId for enum OBJECT_ID 
	 * @return enum value for this field value
	 */
	public static JTextFieldsEnum getEnum(String value) {
		return map.get(String.valueOf(value));
	}
	static {
		for (JTextFieldsEnum en : JTextFieldsEnum.values()) {
			map.put(en.strVal, en);
		}
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(String.format("OBJECT_ID Field name: %s", JTextFieldsEnum.OBJECT_ID.getValue()));
		System.out.println(String.format("FOV_AMIN Field name: %s", JTextFieldsEnum.FOV_AMIN.getValue()));
		
		System.out.println(String.format("Lookup OBJECT_ID by field name: %s", JTextFieldsEnum.getEnum("objectidfield").toString()));
	
	}
}
