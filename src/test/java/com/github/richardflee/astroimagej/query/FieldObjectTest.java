package com.github.richardflee.astroimagej.query;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.github.richardflee.astroimagej.utils.AstroCoords;

class FieldObjectTest {
	
	private static FieldObject wasp12;
	private static FieldObject sirius;
	private static FieldObject bkg_164;
	
	private final double tol = 1e-6;
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		wasp12 = 
			new FieldObject("wasp12", 
				AstroCoords.raHms_To_raHr("06:30:32.797"), 
				AstroCoords.decDms_To_decDeg("+29:40:20.27"), 
				0.0, 0.0);
		System.out.println(AstroCoords.raHms_To_raHr("06:30:32.797"));
		System.out.println(AstroCoords.decDms_To_decDeg("+29:40:20.27"));
		System.out.println();
		
		sirius = 
			new FieldObject("Sirius", 
				AstroCoords.raHms_To_raHr("06:45:08.917"), 
				AstroCoords.decDms_To_decDeg("-16:42:58.02"), 
				0.0, 0.0);
		System.out.println(AstroCoords.raHms_To_raHr("06:45:08.917"));
		System.out.println(AstroCoords.decDms_To_decDeg("-16:42:58.02"));
		System.out.println();
		
		bkg_164 = 
				new FieldObject("bkg_164", 
						AstroCoords.raHms_To_raHr("06:30:47.77"), 
						AstroCoords.decDms_To_decDeg("29:35:30.4"), 
						0.0, 0.0);
		System.out.println(AstroCoords.raHms_To_raHr("006:30:47.77"));
		System.out.println(AstroCoords.decDms_To_decDeg("29:35:30.4"));
		System.out.println();
	}

	@DisplayName("Verify large angular separation wasp12 & sirius")
	@Test
	void testLargeRadSepAmin() {
		sirius.setRadSepAmin(wasp12);
		System.out.println(sirius.getRadSepAmin());
		assertEquals(2791.3127738, sirius.getRadSepAmin(), tol);
	}
	
	@DisplayName("Verify small angular separation wasp12 & bkg_164")
	@Test
	void testSmallRadSepAmin() {
		bkg_164.setRadSepAmin(wasp12);
		System.out.println(bkg_164.getRadSepAmin());
		assertEquals(5.8246672, bkg_164.getRadSepAmin(), tol);
	}

}
