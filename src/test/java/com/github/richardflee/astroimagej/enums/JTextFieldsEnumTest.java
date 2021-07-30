package com.github.richardflee.astroimagej.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JTextFieldsEnumTest {

	private static JTextFieldsEnum obj_id = JTextFieldsEnum.OBJECT_ID;
	private static JTextFieldsEnum ra_hms = JTextFieldsEnum.RA_HMS;
	private static JTextFieldsEnum mag_lim = JTextFieldsEnum.MAG_LIMIT;
	
	@DisplayName("Verify enum string values")
	@Test
	void testGetValue() {
		assertEquals("objectidfield", obj_id.getValue());
		assertEquals("rafield", ra_hms.getValue());
		assertEquals("maglimitfield", mag_lim.getValue());
	}

	@DisplayName("Verify string values inverse lookup")
	@Test
	void testGetEnum() {
		assertEquals(obj_id, JTextFieldsEnum.getEnum("objectidfield"));
		assertEquals(ra_hms, JTextFieldsEnum.getEnum("rafield"));
		assertEquals(mag_lim, JTextFieldsEnum.getEnum("maglimitfield"));
	}

}
