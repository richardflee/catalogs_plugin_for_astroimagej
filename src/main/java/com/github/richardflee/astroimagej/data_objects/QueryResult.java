package com.github.richardflee.astroimagej.data_objects;

import java.util.ArrayList;
/**
 * Encapsulates the results of a coordinate-based query on the on-line database
 * in an list of type FieldObject
 */
import java.util.List;

import com.github.richardflee.astroimagej.fileio.ApassFileReader;

/**
 * Objects of this class encapsulate results of queries of on-line astronomical
 * databases in a list of FieldObjects
 * <p>
 * Target star data is the first list item, identified "T01". The remainder is a
 * list of reference star objects ordered either by radial distance from the
 * target star (in arcmin) or by the absolute difference in magnitude. If user
 * selected, reference objects are identified as "C02", "C03" .. in sort order
 * </p>
 */
public class QueryResult {

	// query settings associated with current QueryResult
	private CatalogQuery query = null;

	// list of target and refrence field objects
	private List<FieldObject> fieldObjects;

	/**
	 * Constructor for QueryResult objects created by query of on-line astronomical
	 * database or importing radec dataset.
	 * <p>
	 * A QueryResult object comprises the database query object and a list of
	 * FieldObjects initialised with a single target object.
	 * </p>
	 * <p>
	 * Target fields are extracted from the query catalog query.
	 * </p>
	 * 
	 * @param query parameters for on-line database query
	 */
	public QueryResult(CatalogQuery query) {
		// copy query
		this.query = new CatalogQuery(query);

		// creates target field object from query and saves target 1st item in
		// FieldObjects array
		fieldObjects = new ArrayList<>();
		FieldObject target = new FieldObject(query.getObjectId(), query.getRaHr(), query.getDecDeg(), 0.0, 0.0);
		target.setTarget(true);
		target.setSelected(true);
		target.setApertureId("T01");

		target.setRadSepAmin(0.0);
		target.setDeltaMag(0.0);
		target.setnObs(1);
		fieldObjects.add(target);
	}

	// getters / setters

	public CatalogQuery getQuery() {
		return query;
	}

	public void setQuery(CatalogQuery query) {
		this.query = query;
	}

	public List<FieldObject> getFieldObjects() {
		return fieldObjects;
	}

	public void setFieldObject(FieldObject fieldObject) {
		fieldObjects.add(fieldObject);
	}

	/**
	 * Total number of list items
	 * 
	 * @return total items
	 */
	public int getRecordsTotal() {
		return fieldObjects.size() - 1;
	}

	/**
	 * Returns first item from list where isTarget is true
	 * 
	 * @return reference to target object
	 */
	public FieldObject getTargetObject() {
		return fieldObjects.stream().filter(p -> p.isTarget()).findFirst().get();
	}
	
	public double getTargetMag() {
		return getTargetObject().getMag();
	}
	
	public void setTargetMag(double targetMag) {
		getTargetObject().setMag(targetMag);
	}

	/**
	 * Updates field object delta mag fields: delatMag = obj_mag - tgt_mag
	 * 
	 * @param targetMag target object nominal mag
	 */
	public void updateDeltaMags(double targetMag) {
		setTargetMag(targetMag);
		fieldObjects.forEach(p -> p.setDeltaMag(targetMag));
	}


	@Override
	public String toString() {
		return "QueryResult [fieldObjects=" + fieldObjects + "]";
	}

	public static void main(String[] args) {

		double targetMag = 12.345;
		
		// compile result object from file
		ApassFileReader fr = new ApassFileReader();

		//build catalog result object
		CatalogQuery query = new CatalogQuery();
		QueryResult result = fr.runQueryFromFile(query);

		// update delta mag
		result.updateDeltaMags(targetMag);		
		result.getFieldObjects().stream().forEach(System.out::println);
		System.out.println(String.format("\nTarget mag expected 12.345: %.3f", result.getTargetMag()));
		
		targetMag = 9.875;
		result.setTargetMag(targetMag);
		System.out.println(String.format("Target mag expected 9.875: %.3f", result.getTargetMag()));

//		// sort by distance:
//		List<FieldObject> sortedFilteredList = result.sortByDistance(targetMag);
//
//		// test: monotonic increase in distance to target object
//		boolean b = true;
//		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
//			double rad1 = sortedFilteredList.get(idx).getRadSepAmin();
//			double rad = sortedFilteredList.get(idx - 1).getRadSepAmin();
//			b = b && (rad1 > rad);
//		}
//		System.out.println("Sort by distance:");
//		System.out.println(String.format("Sorted in increasing distance: %b", b));
//		System.out.println();
//
//		sortedFilteredList = result.sortByDeltaMag(targetMag);
//
//		// test: monotonic increase in |obj_mag - tgtmag|
//		b = true;
//		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
//			double mag1 = Math.abs(sortedFilteredList.get(idx).getMag() - targetMag);
//			double mag = Math.abs(sortedFilteredList.get(idx - 1).getMag() - targetMag);
//			b = b && (mag1 > mag);
//		}
//		System.out.println(String.format("Target mag = %.3f", targetMag));
//		System.out.println(String.format("Sorted in increasing abs deltaMag: %b", b));
//
//		System.out.println(String.format("Total no records =  %02d", result.getRecordsTotal()));
//
//		// ***** getSortedFilteredList
//		// test sort by distance
//		CatalogSettings settings = new CatalogSettings();
//		settings.setTargetMagSpinnerValue(targetMag);
//		sortedFilteredList = result.getSortedList(settings);
//
//		System.out.println("\nTest getSortedFileter:");
//		sortedFilteredList.stream().forEach(System.out::println);
//
//		b = true;
//		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
//			double rad1 = sortedFilteredList.get(idx).getRadSepAmin();
//			double rad = sortedFilteredList.get(idx - 1).getRadSepAmin();
//			b = b && (rad1 > rad);
//		}
//		System.out.println("\nSort by distance:");
//		System.out.println(String.format("SortedFilteredList in increasing distance: %b", b));
//
//		// test sort by mag diff
//		settings.setDistanceRadioButtonValue(false);
//		settings.setDeltaMagRadioButtonValue(true);
//		System.out.println("\nSort by delta mag:");
//		sortedFilteredList = result.getSortedList(settings);
//		sortedFilteredList.stream().forEach(System.out::println);
//
//		// test: monotonic increase in |obj_mag - tgtmag|
//		b = true;
//		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
//			double mag1 = Math.abs(sortedFilteredList.get(idx).getMag() - targetMag);
//			double mag = Math.abs(sortedFilteredList.get(idx - 1).getMag() - targetMag);
//			b = b && (mag1 > mag);
//		}
//		System.out.println(String.format("Target mag = %.3f", targetMag));
//		System.out.println(String.format("Sorted in increasing abs deltaMag: %b", b));

//		
//		// filter test
//		settings.setDistanceRadioButtonValue(false);
//		settings.setDeltaMagRadioButtonValue(true);
//		
//		settings.setUpperLimitSpinnerValue(4.5);
//		settings.setTargetMagSpinnerValue(15.5);
//		settings.setLowerLimitSpinnerValue(-3.5);
//		settings.setnObsSpinnerValue(2);
//		settings.setMagLimitsCheckBoxValue(true);
//		
//		System.out.println("\nFilter test:");
//		System.out.println(String.format("Total number records: %d", settings.getTotalLabelValue()));
//		System.out.println(String.format("Filtered number records: %d", settings.getFilteredLabelValue()));
//		
//		

	}
}
