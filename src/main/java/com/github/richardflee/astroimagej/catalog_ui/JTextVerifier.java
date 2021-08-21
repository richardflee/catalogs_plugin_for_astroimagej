package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.Color;

import com.github.richardflee.astroimagej.enums.QueryEnum;

/**
 * Verifies user inputs to catalog dialog text fields.
 */
public class JTextVerifier {
	
	private CatalogUI catalogUi;
	
	public JTextVerifier(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;
	}

	// append regex trailing decimal places up to .nnnn
	private static String rxDecimal = "(?:[.]\\d{0,4})?";
	

	/**
	 * Returns true if user input in ChartUI text field is valid, otherwise false
	 * <p>
	 * Refer  to isValid functions for acceptable data range and formats
	 * </p>
	 * 
	 * @param input user input, trimmed of any external white space characters
	 * @param en lowercase string identifying input text field
	 * 
	 * @return true if data complies with data range and format, false otherwise
	 */
//	protected boolean verifyInput(String input, QueryEnum en) {
//		
//		// apply validation of active field data
//		boolean isValid = true;
//		if (en == QueryEnum.OBJECT_ID) {
//			isValid = isValidObjectId(input);
//			
//		} else if (en == QueryEnum.RA_HMS) {
//			isValid = isValidCoords(input, QueryEnum.RA_HMS);
//
//		} else if (en == QueryEnum.DEC_DMS) {
//			isValid = isValidCoords(input, QueryEnum.DEC_DMS);
//
//		} else if (en == QueryEnum.FOV_AMIN) {
//			isValid = isValidFov(input);
//
//		} else if (en == QueryEnum.MAG_LIMIT) {
//			isValid = isValidMagLimit(input);
//		}
//		return isValid;
//	}
	
	
	protected void verifyObjectId(String input) {
		boolean isValid = isValidObjectId(input);
		if (isValid) {
			catalogUi.objectIdField.setForeground(Color.BLACK);
			catalogUi.raField.requestFocus();
		} else {
			catalogUi.objectIdField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyRaHms(String input) {
		boolean isValid = isValidCoords(input, QueryEnum.RA_HMS);
		if (isValid) {
			catalogUi.raField.setForeground(Color.BLACK);
			catalogUi.decField.requestFocus();
		} else {
			catalogUi.raField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyDecDms(String input) {
		boolean isValid = isValidCoords(input, QueryEnum.DEC_DMS);
		if (isValid) {
			catalogUi.decField.setForeground(Color.BLACK);
			catalogUi.fovField.requestFocus();
		} else {
			catalogUi.decField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyFov(String input) {
		boolean isValid = isValidFov(input);
		if (isValid) {
			catalogUi.fovField.setForeground(Color.BLACK);
			catalogUi.magLimitField.requestFocus();
		} else {
			catalogUi.fovField.setForeground(Color.RED);			
		}
	}
	
	protected void verifyMagLimit(String input) {
		boolean isValid = isValidMagLimit(input);
		if (isValid) {
			catalogUi.magLimitField.setForeground(Color.BLACK);
			catalogUi.objectIdField.requestFocus();
		} else {
			catalogUi.magLimitField.setForeground(Color.RED);			
		}
	}
	
	
	
	/**
	 * Test if format of format target name is alphanumeric and at least one character
	 * 
	 * @param input target name
	 * @return true for alphanumeric chars or white-space, '.', '-' and '_' chars,
	 *         false otherwise or empty field
	 */
	protected boolean isValidObjectId(String input) {
		String rx = "^[a-zA-Z0-9\\s\\w\\-\\.]+$";
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/**
	 * Check user input conforms to coordinate formats:
	 * <p>
	 * Ra format: 00:00:00[.00] to 23:59:59[.00] where [.00] indicates optional
	 * decimal places
	 * RA in units hours
	 * </p>
	 * <p>
	 * Dec format ±90:00:00[.00] where [.00] indicates optional decimal places 
	 * Dec in units degree
	 * </p>
	 * 
	 * @param input user input ra or dec values in sexagesimal format
	 * @param radec RA or DEC flag
	 * 
	 * @return true if input conforms to specified format, false otherwise
	 */
	protected boolean isValidCoords(String input, QueryEnum radec) {
		// delete any whitespace chars
		input = input.replaceAll("\\s+", "");

		// hrs regex 0 - 9 or 00 - 23 + ':' delim
		String rxHr = "([0-2]|[0-1][0-9]|2[0-3]):";

		// deg regex 0 - 9 or 00 - 90
		String rxDeg = "([0-9]|[0-8][0-9]|90):";

		// min regex 0 - 9 or 00 - 59 + ':' delim
		String rxMm = "([0-9]|[0-5][0-9]):";

		// ss regex = mm regex less delim char
		String rxSs = rxMm.substring(0, rxMm.length() - 1);
		
		// ra input >= 0, dec input +/-
		String rx = rxDecimal;
		if (radec == QueryEnum.RA_HMS) {
			rx = "^[+]?" + rxHr + rxMm + rxSs + rx;		// ra >= 0
		} else if (radec == QueryEnum.DEC_DMS) {
			rx = "^[+-]?" + rxDeg + rxMm + rxSs + rx;	// dec +/-
		}
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/*
	 * Test if Field-of-View (fov) value is in range
	 * 
	 * @param input width of FOV (arcmiin)
	 * @return true if input in range 1.0 to 1199.99 amin, false otherwise
	 */
	protected boolean isValidFov(String input) {
		// compile regex range 1.0 to 1200] (arcmin)
		// optional leading '+' sign and up to 4 decimal places
		String rx = "([1-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1][0-1][0-9][0-9])";
		rx = "^[+]?" + rx + rxDecimal;
		return (input.trim().length() > 0) && input.matches(rx);
	}

	/*
	 * Test if max mag value is in range
	 * 
	 * @param input maximum object mag to download from on-line catalog
	 * @return true if input in range 1.0 to 99, false otherwise
	 */
	protected boolean isValidMagLimit(String input) {
		// compile regex range 1.0 to 99.99] (mag)
		// optional leading '+' sign and up to 4 decimal places
		String rx = "([1-9]|[1-9][0-9])";
		rx = "^[+]?" + rx + rxDecimal;
		return (input.trim().length() > 0) && input.matches(rx);
	}
}

