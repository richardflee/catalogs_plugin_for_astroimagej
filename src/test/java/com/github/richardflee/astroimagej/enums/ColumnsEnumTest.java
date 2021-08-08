package com.github.richardflee.astroimagej.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ColumnsEnumTest {

	private static ColumnsEnum ap_col = ColumnsEnum.AP_COL;
	private static ColumnsEnum ra2000_col = ColumnsEnum.RA2000_COL;
	private static ColumnsEnum use_col = ColumnsEnum.USE_COL;

	@DisplayName("Verify column index lookup")
	@Test
	void testGetIndex() {
		assertEquals(0, ap_col.getIndex());
		assertEquals(2, ra2000_col.getIndex());
		assertEquals(9, use_col.getIndex());
	}

	@DisplayName("Verify column width lookup")
	@Test
	void testGetWidth() {
		assertEquals(4, ap_col.getWidth());
		assertEquals(14, ra2000_col.getWidth());
		assertEquals(4, use_col.getWidth());
	}

	@DisplayName("Verify total column width = 100")
	@Test
	void testGetTotalWidth() {
		assertEquals(100, ColumnsEnum.getTotalWidth());
	}

	@DisplayName("Verify column index inverse lookup")
	@Test
	void testGetEnum() {
		assertEquals(ap_col, ColumnsEnum.getEnum(0));
		assertEquals(ra2000_col, ColumnsEnum.getEnum(2));
		assertEquals(use_col, ColumnsEnum.getEnum(9));
	}

}
