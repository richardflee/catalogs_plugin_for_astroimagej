package com.github.richardflee.astroimagej.visibility_plotter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.query_objects.PrecessionObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

class PrecessionObjectTest {
	
	public static final double TOL = 1e-6;
	
	LocalDateTime utcDateTime;

	private static PrecessionObject wasp12;
	private static PrecessionObject alfirk;
	private static PrecessionObject hip47193;
	private static PrecessionObject polaris;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String objectId = "wasp12";
		String raHms = "06:30:32.80";
		String decDms = "+29:40:20";
		wasp12 = new PrecessionObject(objectId, AstroCoords.raHmsToRaHr(raHms), AstroCoords.decDmsToDecDeg(decDms));
		
		objectId = "Alfirk";
		raHms = "21:28:39.60";
		decDms = "+70:33:38.57";
		alfirk = new PrecessionObject(objectId, AstroCoords.raHmsToRaHr(raHms), AstroCoords.decDmsToDecDeg(decDms));
		
		objectId = "HIP 47193";
		raHms = "09:37:05.29";
		decDms = "+81:19:35.0";
		hip47193 = new PrecessionObject(objectId, AstroCoords.raHmsToRaHr(raHms), AstroCoords.decDmsToDecDeg(decDms));
		
		objectId = "Polaris";
		raHms = "02:31:49.10";
		decDms = "+89:15:50.79";
		polaris = new PrecessionObject(objectId, AstroCoords.raHmsToRaHr(raHms), AstroCoords.decDmsToDecDeg(decDms));
		
		
	}
	

	@BeforeEach
	void setUp() throws Exception {
		utcDateTime = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
	}


	@DisplayName("Verify precession with AIJ-CC for object dec 30deg PA34")
	@Test
	void testPrecessionWasp12() {
		
		PrecessionObject precessObj = wasp12;
		double raHr1 = precessObj.precessRa2000(utcDateTime);
		double decDeg1 = precessObj.precessDec2000(utcDateTime);
		
		double raHr0 =  AstroCoords.raHmsToRaHr("06:31:45.6");
		double decDeg0 = AstroCoords.decDmsToDecDeg("+29:39:28.7");
		
		assertEquals(raHr0, raHr1, 100 * TOL);
		assertEquals(decDeg0, decDeg1, 1000 * TOL);
	}
	
	@DisplayName("Verify precession with AIJ-CC for object dec 70deg PA34")
	@Test
	void testPrecessionAlfirk() {
		LocalDateTime obsDate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
		
		PrecessionObject precessObj = alfirk;
		double raHr1 = precessObj.precessRa2000(obsDate);
		double decDeg1 = precessObj.precessDec2000(obsDate);
		
		double raHr0 =  AstroCoords.raHmsToRaHr("21:28:53.8");
		double decDeg0 = AstroCoords.decDmsToDecDeg("+70:38:39.5");
		
		assertEquals(raHr0, raHr1, 100 * TOL);
		assertEquals(decDeg0, decDeg1, 1000 * TOL);
	}
	
	@DisplayName("Verify precession with AIJ-CC for object dec 80deg cf 82deg limit PA34")
	@Test
	void testPrecessionHIP_47193() {
		LocalDateTime obsDate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
		
		PrecessionObject precessObj = hip47193;
		double raHr1 = precessObj.precessRa2000(obsDate);
		double decDeg1 = precessObj.precessDec2000(obsDate);
		
		double raHr0 =  AstroCoords.raHmsToRaHr("09:39:39.7");
		double decDeg0 = AstroCoords.decDmsToDecDeg("+81:14:24.6");
		
		// increase tol on raHr for dec close to 82deg limit to add precession
		assertEquals(raHr0, raHr1, 0.001);
		assertEquals(decDeg0, decDeg1, 1000 * TOL);
	}
	
	@DisplayName("Verify no precession added for object above 82deg limit PA34")
	@Test
	void testPrecessionPolaris() {
		LocalDateTime obsDate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
		
		PrecessionObject precessObj = polaris;
		double raHr1 = precessObj.precessRa2000(obsDate);
		double decDeg1 = precessObj.precessDec2000(obsDate);
		
		double raHr0 =  AstroCoords.raHmsToRaHr("02:31:49.10");
		double decDeg0 = AstroCoords.decDmsToDecDeg("+89:15:50.79");
		
		// ra and dec unchanged
		assertEquals(raHr0, raHr1, TOL);
		assertEquals(decDeg0, decDeg1, TOL);
	}

}

