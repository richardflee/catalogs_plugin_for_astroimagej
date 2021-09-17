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
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
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

	/**
	 * Runs a query on APASS9 catalog in the Vizier on-line database with url compiled from user-input parameters
	 * 
	 * @param query
	 *     CatalogQuery object encapsulating VSP database query parameters
	 * @return fieldObjects array of FieldObjects matching user-input query parameters
	 */
	@Override
	public List<FieldObject> runQuery(CatalogQuery query) {
		// compiles url
		String url = CatalogUrls.urlBuilder(query);
		
		// extracts array of data lines from Vizier/APASS response
		List<String> lines = importApassData(url);
		
		// converts data lines to array field objects
		List<FieldObject> fieldObjects = getFieldObjects(lines);
		
		// status message
		String statusMessage = String.format("Downloaded %d APASS records", fieldObjects.size());
		setStatusMessage(statusMessage);
		
		return fieldObjects;
	}
	
	/**
	 * Status message getter
	 */
	@Override
	public String getStatusMessage() {
		return this.statusMessage;
	}
	
	/*
	 * Compiles a list of field objects assembled from apass data line array
	 * 
	 * @param lines list of text data lines 
	 * @return list of field objects
	 */
	private List<FieldObject> getFieldObjects(List<String> lines) { 
		List<FieldObject> fieldObjects = new ArrayList<>();
		for (String line : lines) {
			FieldObject fo = compileFieldObject(line);
			fieldObjects.add(fo);
		}
		return fieldObjects;
	}
	
	/**
	 * Compiles a single field object with coordinate-based object id
	 * 
	 * <p>Auto-names APASS objects format: HHMMSSSS±DDMMSSSS</p>
	 * 
	 * @param line tab-delimited text data line
	 * @return compiled field object
	 */
	private FieldObject compileFieldObject(String line) {
		
		// remove any blanks then splits line on tab char
		String[] terms = line.replace(" ", "").split("\t");
		
		// coordinate terms
		double raDeg = Double.valueOf(terms[0]);
		double raHr = raDeg / 15.0;
		double decDeg = Double.valueOf(terms[1]);
		
		// number obs
		int nObs = Integer.valueOf(terms[2]);
		
		// mag terms
		double mag = Double.valueOf(terms[3]);
		double magErr = Double.valueOf(terms[4]);
		
		// create new field object, auto-name object id
		FieldObject fo = new FieldObject(null, raHr, decDeg, mag, magErr);
		
		// set params
		fo.setnObs(nObs);
		fo.setTarget(false);
		fo.setSelected(true);
		
		return fo;
	}
	
	
	/**
	 * Runs APASS catalog-based query on Vizier on-line database
	 * 
	 * @param url Vizier / APASS url specifying query parameters
	 * @return list of data lines extractedd from query response
	 */
	private List<String> importApassData(String url) {
		String line;
		List<String> lines = new ArrayList<>();
		
		// run Vizier query, add valid data lines to string array
		try {
			// initialise connection
			URL vizier = new URL(url);
			URLConnection conn = vizier.openConnection();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// append valid data lines to lines array
			while ((line = in.readLine()) != null)
				if (this.isDataLine(line)) {
					lines.add(line);
				}
			in.close();
		} catch (IOException e1) {
			String statusMessage = "ERROR: Vizier/APASS download error";
			setStatusMessage(statusMessage);
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
	// refer: https://www.baeldung.com/java-check-string-number
	private static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if ((strNum == null) || (strNum.length() == 0)) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}
	
	private void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static void main(String[] args) {

		ApassCatalog apass = new ApassCatalog();

		CatalogQuery query = new CatalogQuery();
		query.setCatalogType(CatalogsEnum.APASS);
		query.setFovAmin(10.0);

		List<FieldObject> fieldObjects = apass.runQuery(query);
		fieldObjects.stream().forEach(p -> System.out.println(p.toString()));
		
		QueryResult result = new QueryResult(query, new CatalogSettings(12.345));
		result.appendFieldObjects(fieldObjects);
		
		System.out.println(String.format("No. download records: %d", fieldObjects.size()));
		System.out.println(String.format("Status message: %s", apass.getStatusMessage()));
		
	}

}
