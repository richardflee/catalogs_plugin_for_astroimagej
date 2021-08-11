package com.github.richardflee.astroimagej.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryEnumTest {

	private static QueryEnum obj_id = QueryEnum.OBJECT_ID;
	private static QueryEnum ra_hms = QueryEnum.RA_HMS;
	private static QueryEnum mag_lim = QueryEnum.MAG_LIMIT;
	
	@DisplayName("Verify enum string values")
	@Test
	void testGetValue() {
		assertEquals("objectidfield", obj_id.getFieldId());
		assertEquals("rafield", ra_hms.getFieldId());
		assertEquals("maglimitfield", mag_lim.getFieldId());
	}

	@DisplayName("Verify string values inverse lookup")
	@Test
	void testGetEnum() {
		assertEquals(obj_id, QueryEnum.getEnum("objectidfield"));
		assertEquals(ra_hms, QueryEnum.getEnum("rafield"));
		assertEquals(mag_lim, QueryEnum.getEnum("maglimitfield"));
	}

}
