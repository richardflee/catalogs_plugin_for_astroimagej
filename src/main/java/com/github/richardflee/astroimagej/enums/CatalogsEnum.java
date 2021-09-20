package com.github.richardflee.astroimagej.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enum lists on-line astronomical catalogs selection options
 * 
 * <p> * Internally encodes catalog magnitude bands as '.' delimited string. Method
 * magBands decodes string and returns string array of filter / magnitude names
 * for selected catalog</p>
 */
public enum CatalogsEnum {
	SIMBAD("B.V.R.I"), VSP("B.V.Rc.Ic"), APASS("B.V.SG.SR.SI"), DSS("");

	private String strVal;
	private static final Map<String, CatalogsEnum> getEnumMap = new HashMap<>();

	CatalogsEnum(String magBand) {
		this.strVal = magBand;
	}

	/**
	 * Decodes '.' delimited filters list into array
	 * 
	 * @return list of catalog filters / magnitude bands
	 */
	public List<String> getMagBands() {
		return Arrays.asList(strVal.split("\\."));
	}
	
	
	/**
	 * Maps catalog name to associated enum value
	 *  
	 * @param catalog name 
	 * @return enum value for this name
	 */	
	public static CatalogsEnum getEnum(String value) {
		return getEnumMap.get(value);
	}

	// initialise catalog look-up map
	static {
		for (final CatalogsEnum en : CatalogsEnum.values()) {
			// string value key to enum lookup
			getEnumMap.put(en.toString(), en);
		}
	}

	public static void main(String args[]) {
		
		for (String magBand : CatalogsEnum.VSP.getMagBands()) {
			System.out.println(String.format("VSP MagBand: %s", magBand));			
		}
		
		CatalogsEnum.VSP.getMagBands().stream().forEach(p -> System.out.println(String.format("VSP %s",  p)));
		
		System.out.println();
		for (String magBand : CatalogsEnum.APASS.getMagBands()) {
			System.out.println(String.format("APASS MagBand: %s", magBand));			
		}
		
		System.out.println();
		System.out.println(String.format(
				"Lookup VSP by catalog name: %s =>  %s", "VSP", CatalogsEnum.getEnum("VSP").toString()));
		System.out.println(String.format(
				"Lookup APASS by catalog name: %s =>  %s", "APASS", CatalogsEnum.getEnum("APASS").toString()));
	}
}