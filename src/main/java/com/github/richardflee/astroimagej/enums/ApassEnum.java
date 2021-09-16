package com.github.richardflee.astroimagej.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * APASS Vizier mag-based url fragments <p>Format: [mag_url].[meg_err_url, SDSS
 * mag bands include a pesky ' (apostrophe) hexadecimal 27 </p>
 */
public enum ApassEnum {
	B("Bmag.e_Bmag"), V("Vmag.e_Vmag"), SR(sdss("r")), SG(sdss("g")), SI(sdss("i"));

	private static final String APOSTROPHE = "%27";
	
	private String strVal;
	private static final Map<String, ApassEnum> getEnumMap = new HashMap<>();

	ApassEnum(String strVal) {
		this.strVal = strVal;
	}

	public String getMagUrl() {
		return this.strVal.split("\\.")[0];
	}

	public String getMagErrUrl() {
		return this.strVal.split("\\.")[1];
	}

	/**
	 * Maps catalog name to associated enum value
	 * @param catalog
	 *     name
	 * @return enum value for this name
	 */
	public static ApassEnum getEnum(String value) {
		return getEnumMap.get(value);
	}

	private static String sdss(String filter) {
		filter = filter + APOSTROPHE + "mag";
		return filter + ".e_" + filter;
	}

	// initialise catalog look-up map
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

	}

}
