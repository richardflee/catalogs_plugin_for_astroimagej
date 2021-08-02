package com.github.richardflee.astroimagej.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;

public class ApassFileReader {

	private final static String src = "c:/temp/apass_data.txt";

	public ApassFileReader() {
	}

	// => catalog.runQuery
	public QueryResult runQueryFromFile(CatalogQuery query) {

		QueryResult result = new QueryResult(query);
		FieldObject target = null;
		List<FieldObject> fieldObjects = new ArrayList<>();

		boolean isTarget = true;
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(new File(src)))) {
			while ((line = br.readLine()) != null) {
				FieldObject fo = new FieldObject();
				String[] terms = line.split("\t");

				for (int idx = 0; idx < terms.length; idx++) {

					String term = terms[idx];
					if (idx == ColumnsEnum.OBJECTID_COL.getIndex()) {
						fo.setObjectId(term);
					} else if (idx == ColumnsEnum.RA2000_COL.getIndex()) {
						Double raHr = Double.parseDouble(term);
						fo.setRaHr(raHr);
					} else if (idx == ColumnsEnum.DEC2000_COL.getIndex()) {
						Double decDeg = Double.parseDouble(term);
						fo.setDecDeg(decDeg);
					} else if (idx == ColumnsEnum.MAG_COL.getIndex()) {
						fo.setMag(Double.parseDouble(term));
					} else if (idx == ColumnsEnum.MAG_ERR_COL.getIndex()) {
						fo.setMagErr(Double.parseDouble(term));
					} else if (idx == ColumnsEnum.NOBS_COL.getIndex()) {
						fo.setnObs(Integer.parseInt(term));
					}
				}
				if (isTarget) {
					target = fo;
					fo.setRadSepAmin(target);
					isTarget = false;
				} else {
					// fo.setObjectId(null);
					fo.setRadSepAmin(target);
					fieldObjects.add(fo);
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fieldObjects.stream().sorted(Comparator.comparing(FieldObject::getRadSepAmin))
				.forEach(fo -> result.setFieldObject(fo));
		return result;
	}
	

	public static void main(String[] args) {

		ApassFileReader fr = new ApassFileReader();

		CatalogQuery query = new CatalogQuery();

		QueryResult result = fr.runQueryFromFile(query);

		result.getFieldObjects().forEach(System.out::println);
		System.out.println();

	}

}
