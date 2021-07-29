package com.github.richardflee.astroimagej.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enum lists on-line astronomical catalogs selection options
 * <p>
 * Internally encodes catalog magnitude bands as a '.' delimited string. Method
 * magBands decodes string and returns string array of filter / magnitude names
 * for selected catalog
 * </p>
 */
public enum CatalogsEnum {
	SIMBAD("B.V.R.I"), VSP("B.V.Rc.Ic"), APASS("B.V.SG.SR.SI"), DSS("");

	private String magBand;

	CatalogsEnum(String magBand) {
		this.magBand = magBand;
	}

	/**
	 * Decodes '.' delimited filters list into array
	 * 
	 * @return list of catalog filters / magnitude bands
	 */
	public List<String> getMagBands() {
		return Arrays.asList(magBand.split("\\."));
	}

	public static void main(String args[]) {
		CatalogsEnum vsp = CatalogsEnum.VSP;
		vsp.getMagBands().forEach(p -> System.out.println(p.toString()));
	}
}