package com.github.richardflee.astroimagej.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.AbstractFieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

class AbstractFieldObjectTest {
	
	private static AbstractFieldObject afo;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String raHms = "06:30:32.80";
		String decDms = "+29:40:20.27";
		
		double raHr = AstroCoords.raHms_To_raHr(raHms);
		double decDeg = AstroCoords.decDms_To_decDeg(decDms);
		afo = new AbstractFieldObject(null, raHr, decDeg);
		
	}

	@DisplayName("Verify auto-generated objectId for wasp12")
	@Test
	void testGetObjectId() {
		String id = "06303280+29402027";
		assertEquals(id, afo.getObjectId());
	}

}
