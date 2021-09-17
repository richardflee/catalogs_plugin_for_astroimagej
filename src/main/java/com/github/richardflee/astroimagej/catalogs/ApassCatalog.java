package com.github.richardflee.astroimagej.catalogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.CatalogUrls;

/**
 * Queries the AAVSO APASS catalog in the VizieR on-line database for field star
 * photometry data. <p>The user-specified search region is centred on RA and DEC
 * coordinates covering a square fov. The query response returns field star
 * records with photometry data for the specified magnitude band (B, V, SR, SG
 * or SI).</p> <p>Example url:
 * http://vizier.u-strasbg.fr/viz-bin/asu-tsv?-source=APASS9
 * &-c=97.63665417%20%2b29.67229722&-c.bm=60.0x60.0
 * &-out=_RAJ%20_DEJ%20nobs%20g%27mag%20e_g%27mag%20&-out.max=1500 </p>
 */

public class ApassCatalog implements AstroCatalog {

	private String statusMessage = null;

	private static final int N_FIELDS = 5;

	public ApassCatalog() {
	}

	@Override
	public List<FieldObject> runQuery(CatalogQuery query) {
		List<FieldObject> fieldObjects = new ArrayList<>();

		String url = CatalogUrls.urlBuilder(query);
		List<String> lines = importApassData(url);
		
		lines.stream().forEach(p -> System.out.println(p));
		
		return fieldObjects;
	}

	@Override
	public String getStatusMessage() {
		return this.statusMessage;
	}

	private List<String> importApassData(String url) {

		String line;
		List<String> lines = new ArrayList<>();

		try {
			URL vizier = new URL(url);
			URLConnection conn = vizier.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = in.readLine()) != null)
				if (this.isDataLine(line)) {
					// System.out.println(line);
					lines.add(line);
				}
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	return lines;

	}

	/*
	 * Returns true if text line complies with data pattern tests
	 * @param tab-delimited line of text
	 * @return true if line complies with tests, false otherwise
	 */
	private boolean isDataLine(String line) {
		String[] terms = line.split("\t");

		// starts by exclude empty string
		boolean isData = (line.length() > 0)

				// excludes comment lines with leading '#'
				&& (line.charAt(0) != '#')

				// first data term is numeric, excludes text header
				&& (isNumeric(terms[0]))

				// excludes line with missing mag data (ra, dec, nobs, mag, mag_err)
				&& (terms.length == N_FIELDS);
		return isData;
	}

	// regular expression numeric test
	// ref: https://www.baeldung.com/java-check-string-number
	private static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if ((strNum == null) || (strNum.length() == 0)) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	public static void main(String[] args) {

		ApassCatalog apass = new ApassCatalog();

		CatalogQuery query = new CatalogQuery();
		query.setCatalogType(CatalogsEnum.APASS);

		apass.runQuery(query);

		//
		// String url = CatalogUrls.urlBuilder(query);
		//
		// URL vizier = null;
		// URLConnection yc = null;
		//
		// try {
		// vizier = new URL(url);
		// yc = vizier.openConnection();
		// BufferedReader in = new BufferedReader(new
		// InputStreamReader(yc.getInputStream()));
		// String line;
		// while ((line = in.readLine()) != null)
		// if (apass.isDataLine(line)) {
		// System.out.println(line);
		// }
		// in.close();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// System.out.println(url);

	}

}
