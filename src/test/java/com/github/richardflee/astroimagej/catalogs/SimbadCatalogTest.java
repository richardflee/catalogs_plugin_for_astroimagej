package com.github.richardflee.astroimagej.catalogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.astroimagej.exceptions.SimbadNotFoundException;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;

class SimbadCatalogTest {
	private SimbadCatalog simbad;
	private CatalogQuery query;
	private SimbadResult simbadResult;
	
	private final double epsilon = 1.0e-5;

	@BeforeEach
	void setUp() throws Exception {
		simbad = new SimbadCatalog();
		query = new CatalogQuery();
	}


	@DisplayName("Default WASP-12 data matches SIMBAD query")
	@Test
	void defaultData_MatchSimbadQuery_IsCorrect() throws SimbadNotFoundException {
		//String inputTargetName = "wasp12";
		
		simbadResult = simbad.runQuery(query);	
		// simbadID
		assertEquals("WASP-12", simbadResult.getSimbadId());
		
		//Ra & Dec
		assertEquals(6.50911, simbadResult.getSimbadRaHr(), epsilon);
		assertEquals(29.67230, simbadResult.getSimbadDecDeg(), epsilon);
		
		// mag B, V data
		assertEquals(12.14, simbadResult.getMagB(), epsilon);
		assertEquals(11.57, simbadResult.getMagV(), epsilon);
		
		// mag Rc, Ic no data 
		assertNull(simbadResult.getMagR());
		assertNull(simbadResult.getMagI());		
	}
	
	@DisplayName("Vega data matches SIMBAD query (handle leading '*' char in Simbad objectId)")
	@Test
	void vegaData_MatchSimbadQuery_IsCorrect() throws SimbadNotFoundException {
		query.setObjectId("vega");
		simbadResult = simbad.runQuery(query);	
		// simbadID
		String rx = "[^a-zA-Z0-9\\s\\w\\-\\.]";
		
		String s1 = simbadResult.getSimbadId().replaceAll(rx,  "").trim();
		String s2 = "alf Lyr".replaceAll(rx,  "").trim();		
		assertEquals(s2, s1);		
		
		//Ra & Dec
		assertEquals(18.61565, simbadResult.getSimbadRaHr(), epsilon);
		assertEquals(38.78369, simbadResult.getSimbadDecDeg(), epsilon);
		
		// mag B, V, Rc, Ic data
		assertEquals(0.03, simbadResult.getMagB(), epsilon);
		assertEquals(0.03, simbadResult.getMagV(), epsilon);
		assertEquals(0.07, simbadResult.getMagR(), epsilon);
		assertEquals(0.10, simbadResult.getMagI(), epsilon);
	}
	
	@DisplayName("Object identifier not matched in SIMBAD database throws SimbadNotFoundException")
	@Test
	void whenSimbadNotFoundExceptionThrown_thenAssertionSucceeds() {
		query.setObjectId( "WISP-12");		
	    assertThrows(SimbadNotFoundException.class, () -> simbad.runQuery(query));
	}
}



