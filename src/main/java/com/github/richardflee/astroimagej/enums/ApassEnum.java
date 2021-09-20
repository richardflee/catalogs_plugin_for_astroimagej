package com.github.richardflee.astroimagej.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps mag and mag_err url fragments to APASS filter name
 * 
 * <p>Format: [mag_url].[meg_err_url, SDSS mag bands include a pesky apostrophe (') hexadecimal 27 </p>
 */
public enum ApassEnum {
	B("Bmag.e_Bmag"), V("Vmag.e_Vmag"), SR(sdss("r")), SG(sdss("g")), SI(sdss("i"));

	// Required for HTTP coding apostrophe (') char
	private static final String APOSTROPHE = "%27";
	
	private String strVal;
	private static final Map<String, ApassEnum> getEnumMap = new HashMap<>();

	ApassEnum(String strVal) {
		this.strVal = strVal;
	}

	// returns mag portion of enum value, e.g SR => r'mag
	public String getMagUrl() {
		return this.strVal.split("\\.")[0];
	}
	
	// returns mag err portion of enum value, e.g. SR => e_r'mag
	public String getMagErrUrl() {
		return this.strVal.split("\\.")[1];
	}

	/**
	 * Maps an enum to its text value
	 * 
	 * @param value enum text value
	 * @return associated enum value
	 */
	public static ApassEnum getEnum(String value) {
		return getEnumMap.get(value);
	}

	// Formats SDSS filter to enum value
	// Example: r => r'mag.e_r'mag
	private static String sdss(String filter) {
		filter = filter + APOSTROPHE + "mag";
		return filter + ".e_" + filter;
	}

	// initialise look-up map
	static {
		for (final ApassEnum en : ApassEnum.values()) {
			// string value key to enum lookup
			getEnumMap.put(en.toString(), en);
		}
	}

	public static void main(String[] args) {

		for (ApassEnum en : ApassEnum.values()) {
			System.out.println(String.format("Url %s: %s, %s", en.toString(), en.getMagUrl(), en.getMagErrUrl()));
		}
		System.out.println();

		System.out.println(String.format("Test getEnum match %s: %b", "B", ApassEnum.getEnum("B") == ApassEnum.B));
		System.out.println(String.format("Test getEnum match %s: %b", "SR", ApassEnum.getEnum("SR") == ApassEnum.SR));
		System.out.println(String.format("Test getEnum match %s: %b", "SI", ApassEnum.getEnum("SI") == ApassEnum.SI));
	}

}
