package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.Color;

import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.utils.DataVerifier;

/**
 * Verifies user inputs to catalog dialog text fields.
 */
public class JTextVerifier {
	
	private CatalogUI catalogUi;
	
	public JTextVerifier(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;
	}

	
	protected void verifyObjectId(String input) {
		boolean isValid = DataVerifier.isValidObjectId(input);
		if (isValid) {
			catalogUi.objectIdField.setForeground(Color.BLACK);
			catalogUi.raField.requestFocus();
		} else {
			catalogUi.objectIdField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyRaHms(String input) {
		boolean isValid = DataVerifier.isValidCoords(input, QueryEnum.RA_HMS);
		if (isValid) {
			catalogUi.raField.setForeground(Color.BLACK);
			catalogUi.decField.requestFocus();
		} else {
			catalogUi.raField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyDecDms(String input) {
		boolean isValid = DataVerifier.isValidCoords(input, QueryEnum.DEC_DMS);
		if (isValid) {
			catalogUi.decField.setForeground(Color.BLACK);
			catalogUi.fovField.requestFocus();
		} else {
			catalogUi.decField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyFov(String input) {
		boolean isValid = DataVerifier.isValidFov(input);
		if (isValid) {
			catalogUi.fovField.setForeground(Color.BLACK);
			catalogUi.magLimitField.requestFocus();
		} else {
			catalogUi.fovField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyMagLimit(String input) {
		boolean isValid = DataVerifier.isValidMagLimit(input);
		if (isValid) {
			catalogUi.magLimitField.setForeground(Color.BLACK);
			catalogUi.objectIdField.requestFocus();
		} else {
			catalogUi.magLimitField.setForeground(Color.RED);			
		}
	}
}

