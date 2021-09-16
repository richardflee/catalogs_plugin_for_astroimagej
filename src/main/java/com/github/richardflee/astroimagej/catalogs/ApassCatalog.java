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
 * photometry data. <p>The user-sepecified search region is centred on RA and
 * DEC coordinates covering a square fov. The query response returns field star
 * records with photometry data for the specified magnitude band (B, V, SG or
 * SI).</p> Example url: TTD
 */

public class ApassCatalog implements AstroCatalog {

	// private static final String SPACE_CHAR = "%20";
	// private static final int MAX_RECORDS = 1500;
	private static final int N_FIELDS = 5;

	public ApassCatalog() {
	}

	@Override
	public List<FieldObject> runQuery(CatalogQuery query) {
		List<FieldObject> fieldObjects = new ArrayList<>();

		System.out.println("here");
		return fieldObjects;
	}

	@Override
	public String getStatusMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean isDataLine(String line) {
		String[] terms = line.split("\t");
		boolean isData = (line.length() > 0) 
				&& (line.charAt(0) != '#') 
				&& (isNumeric(terms[0]))
				&& (terms.length == N_FIELDS);
		return isData;
	}
	
	public static boolean isNumeric(String strNum) {
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

		String url = CatalogUrls.urlBuilder(query);

		URL vizier = null;
		URLConnection yc = null;

		try {
			vizier = new URL(url);
			yc = vizier.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
				if (isDataLine(line)) {
					System.out.println(line);
				}
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(url);

	}

}
