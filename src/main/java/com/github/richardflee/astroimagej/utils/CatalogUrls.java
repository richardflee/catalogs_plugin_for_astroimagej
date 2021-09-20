package com.github.richardflee.astroimagej.utils;

import com.github.richardflee.astroimagej.enums.ApassEnum;
import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.enums.SimbadEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;

/**
 * Class methods to compile url for an on-line database query.
 * 
 * <p> Simbad: http://simbad.u-strasbg.fr/guide/sim-url.htx </>p
 * 
 * <p> SkyView : DSS Ref: https://skyview.gsfc.nasa.gov/current/docs/batchpage.html</>p
 * 
 * <p> Vsp: Not documented (2021-05),  see https://www.aavso.org/apis-aavso-resources </>p
 * 
 * <p>VizieR/APASS: http://cdsarc.u-strasbg.fr/doc/asu-summary.htx</p>
 */
public class CatalogUrls {
	
	// HTTP protocol: ascii g=hexadecimal representations
	private static final String SPACE_CHAR = "%20";		// http coding for space char ' '
	private static final String PLUS_SIGN = "%2b";      // http coding for plus sign '+'

	private static final int MAX_RECORDS = 1500;

	/**
	 * Compiles a url for a Simbad database query, signature ([CatalogQuery], [SimbadUrlType]).
	 * 
	 * @param query catalog query data
	 * @param paramType which data item to download
	 * @return compiled Simbad url for specified paramType
	 */
	public static String urlBuilder(CatalogQuery query, SimbadEnum paramType) {
		// SIMBAD header
		String url = "http://simbad.u-strasbg.fr/simbad/sim-id?output.format=votable";

		// embed user object id
		url += String.format("&Ident=%s&output.params=main_id,", query.getObjectId());

		// append url fragment for selected parameter
		url += paramType.getUrlFragment();
		return url;
	}
	
	/**
	 * Compiles a url for a database query, signature ([CatalogQuery])
	 * 
	 * @param query catalog query data
	 * @param paramType which data item to download
	 * @return compiled url for specified catalogType
	 */
	public static String urlBuilder(CatalogQuery query) {
		String url = "";
		CatalogsEnum cataogsEnum = query.getCatalogType();
		if (cataogsEnum == CatalogsEnum.DSS) {
			// SkyView header
			url = "https://skyview.gsfc.nasa.gov/cgi-bin/images?Survey=digitized+sky+survey";

			// chart centre coords = ra (deg) & dec (deg)
			url += String.format("&position=%.5f,%.5f", query.getRaHr() * 15.0, query.getDecDeg());

			// fov in deg
			url += String.format("&Size=%s", query.getFovAmin() / 60.0);

			// 1000x1000 pixels & append FITS file type
			int nPix = 1000;
			url += String.format("&Pixels=%s&Return=FITS", nPix);
			
		} else if (cataogsEnum == CatalogsEnum.VSP) {
			// VSP header
			url = "https://app.aavso.org/vsp/api/chart/?format=json";
			
			// fov nn.n (arcmin)
			url +=  String.format("&fov=%.1f", query.getFovAmin());
			
			// magLimit nn.n (mag)
			url += String.format("&maglimit=%.1f", query.getMagLimit()); 
			
			// ra nnn.nnnnn (0 to 360 deg)
			url += String.format("&ra=%.5f", query.getRaHr() * 15.0);
			
			// dec nn.nnnnn (0 to ± 90 deg)
			url += String.format("&dec=%.5f", query.getDecDeg());	
			
			// orientation
			url += "&north=up&east=left";
			
		} else if (cataogsEnum == CatalogsEnum.APASS) {
			// APASS header
			url = "http://vizier.u-strasbg.fr/viz-bin/asu-tsv?-source=APASS9";
			
			// url coordinates and fov fragments
			// centre ra (deg)
			double raDeg = query.getRaHr() * 15.0;
			String ra = String.format("&-c=%.8f", raDeg);
			
			// centre dec(deg), replace leading '+' sign with %2b
			String dec = String.format("%.8f", query.getDecDeg());
			dec = (dec.charAt(0) == '-') ? dec : PLUS_SIGN + dec;
			
			// square fov in arcmin
			double fovAmin = query.getFovAmin();
			String fov = String.format("&-c.bm=%.1fx%.1f", fovAmin, fovAmin);
			
			// append combined ra, dec and fov
			url += ra  + SPACE_CHAR + dec + fov;
			
			// format url output contents
			// ra and dec in J2000 equinox / epoch, number observations
			String radec = "&-out=_RAJ" + SPACE_CHAR + "_DEJ" + SPACE_CHAR + "nobs";	
			url += radec + SPACE_CHAR;
			
			// looks up ApassNum for selected filter & appends mag, mag_err url fragments
			ApassEnum en = ApassEnum.getEnum(query.getMagBand());
			url += en.getMagUrl() + SPACE_CHAR + en.getMagErrUrl() + SPACE_CHAR;
			
			// limit number of records
			url += String.format("&-out.max=%d", MAX_RECORDS);
		}
		return url;
	}
	
	public static void main(String[] args) {
		
		CatalogQuery query = new CatalogQuery();
		query.setCatalogType(CatalogsEnum.APASS);
		
		query.setMagBand("B");
		ApassEnum en = ApassEnum.getEnum(query.getMagBand());
		System.out.println(String.format("APASS filter: %s", query.getMagBand()));
		System.out.println(String.format("Url %s: %s, %s", en.toString(), en.getMagUrl(), en.getMagErrUrl()));
		System.out.println();
		
		query.setMagBand("SG");
		en = ApassEnum.getEnum(query.getMagBand());
		System.out.println(String.format("APASS filter: %s", query.getMagBand()));
		System.out.println(String.format("Url %s: %s, %s", en.toString(), en.getMagUrl(), en.getMagErrUrl()));
		System.out.println();
		
		System.out.println(urlBuilder(query));
		
	}
	
}

