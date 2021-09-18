package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.Color;

import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.utils.InputsVerifier;

/**
 * Verifies user inputs to catalog ui text fields.
 */
public class CatalogInputsVerifier {
	
	private CatalogUI catalogUi;
	
	public CatalogInputsVerifier(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;
	}

	/*
	 * verifies user input objectId format
	 * 
	 * @param input objectId in alphanumeric format
	 */
	protected void verifyObjectId(String input) {
		boolean isValid = InputsVerifier.isValidObjectId(input);
		if (isValid) {
			catalogUi.objectIdField.setForeground(Color.BLACK);
			catalogUi.raField.requestFocus();
		} else {
			catalogUi.objectIdField.setForeground(Color.RED);			
		}
	}
	
	/*
	 * Verifies user input RA format and in range
	 * 
	 * @param input RA in sexagesimal format
	 */
	protected void verifyRaHms(String input) {
		boolean isValid = InputsVerifier.isValidCoords(input, QueryEnum.RA_HMS);
		if (isValid) {
			catalogUi.raField.setForeground(Color.BLACK);
			catalogUi.decField.requestFocus();
		} else {
			catalogUi.raField.setForeground(Color.RED);			
		}
	}
	
	/*
	 * Verifies user input Dec format and in range
	 * 
	 * @param input Dec in sexagesimal format
	 */
	protected void verifyDecDms(String input) {
		boolean isValid = InputsVerifier.isValidCoords(input, QueryEnum.DEC_DMS);
		if (isValid) {
			catalogUi.decField.setForeground(Color.BLACK);
			catalogUi.fovField.requestFocus();
		} else {
			catalogUi.decField.setForeground(Color.RED);			
		}
	}
	
	/*
	 * Verifies user input FOV format and in range
	 * 
	 * @param input FOV in numeric format
	 */	
	protected void verifyFov(String input) {
		boolean isValid = InputsVerifier.isValidFov(input);
		if (isValid) {
			catalogUi.fovField.setForeground(Color.BLACK);
			catalogUi.magLimitField.requestFocus();
		} else {
			catalogUi.fovField.setForeground(Color.RED);			
		}
	}
	
	/*
	 * Verifies user input magLimit format and in range
	 * 
	 * @param input magLimit in numeric format
	 */	
	protected void verifyMagLimit(String input) {
		boolean isValid = InputsVerifier.isValidMagLimit(input);
		if (isValid) {
			catalogUi.magLimitField.setForeground(Color.BLACK);
			catalogUi.objectIdField.requestFocus();
		} else {
			catalogUi.magLimitField.setForeground(Color.RED);			
		}
	}
}

