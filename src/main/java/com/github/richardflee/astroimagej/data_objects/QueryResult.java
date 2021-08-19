package com.github.richardflee.astroimagej.data_objects;

import java.util.ArrayList;
import java.util.Comparator;
/**
 * Encapsulates the results of a coordinate-based query on the on-line database
 * in an list of type FieldObject
 */
import java.util.List;
import java.util.stream.Collectors;

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

	private enum SortTypeEnum {
		SORT_BY_DIST, SORT_BY_DELTA_MAG
	}

	// query settings associated with current QueryResult
	private CatalogQuery query = null;

	// list of target and refrence field objects
	private List<FieldObject> fieldObjects;

	/**
	 * Constructor for QueryResult objects created by query of on-line astronomical database or importing
	 * radec dataset.
	 * <p>A QueryResult object comprises the database query object and a list of FieldObjects 
	 * initialised with a single target object. </p>
	 * <p>Target fields are extracted from the query catalog query.</p>
	 * 
	 * @param query parameters for on-line database query
	 */
	public QueryResult(CatalogQuery query) {
		// copy query
		this.query = new CatalogQuery(query);
		
		// creates target field object from query and saves target 1st item in FieldObjects array
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
	 * Applies catalog sort and filter settings to full catalog dataset.
	 * 
	 * @param settings catalog ui nobs, and magnitude filter settings
	 * @return field object list ordered by sort type and filtered selections
	 */
	public List<FieldObject> getSortedList(CatalogSettings settings) {
		List<FieldObject> sortedList = new ArrayList<>();
		// sort list by distance or mag difference
		double targetMag = settings.getTargetMagSpinnerValue();
		if (settings.isDistanceRadioButtonValue() == true) {
			sortedList = sortByDistance(targetMag);

		} else if (settings.isDeltaMagRadioButtonValue() == true) {
			sortedList = sortByDeltaMag(targetMag);
		}
		return sortedList;
	}

	/**
	 * Sorts object list in order of increasing radial distance (arcmin) from target
	 * star coordinates.
	 * 
	 * @return list headed by target object then reference objects sorted by radial
	 *         separation to target
	 */
	public List<FieldObject> sortByDistance(double targetMag) {
		return sortFieldObjects(targetMag, SortTypeEnum.SORT_BY_DIST);
	}

	/**
	 * Sorts object list in order of increasing absolute difference in reference an
	 * target star magnitude.
	 * 
	 * <p>
	 * Nominal target magnitude entered by user in UI.
	 * </p>
	 * 
	 * @return list headed by target object then reference objects sorted by
	 *         magnitude difference
	 */
	public List<FieldObject> sortByDeltaMag(double targetMag) {
		return sortFieldObjects(targetMag, SortTypeEnum.SORT_BY_DELTA_MAG);
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

	/**
	 * Updates field object delta mag fields: delatMag = obj_mag - tgt_mag
	 * 
	 * @param targetMag target object nominal mag
	 */
	public void updateDeltaMags(double targetMag) {
		fieldObjects.forEach(p -> p.setDeltaMag(targetMag));
	}

	/*
	 * Sorts list of fieldobjects in ascending order by selected sort type with
	 * target bject first list item.
	 * 
	 * @param targetMag user input value for target mag for current filter / mag
	 * band
	 * 
	 * @param sortType user selects sort either by radial distance or absolute
	 * difference in mag to target value
	 * 
	 * @return sorted list of field obejcts, with target item 0.
	 */
	private List<FieldObject> sortFieldObjects(double targetMag, SortTypeEnum sortType) {

		List<FieldObject> sortedList = null;
		// id target star
		FieldObject targetObject = this.getTargetObject();
		targetObject.setApertureId("T01");

		// update FieldObject delta mag fields
		updateDeltaMags(targetMag);

		if (sortType == SortTypeEnum.SORT_BY_DIST) {
			// sort list excluding target object
			sortedList = fieldObjects.stream().filter(p -> !(p.isTarget()))
					.sorted(Comparator.comparingDouble(FieldObject::getRadSepAmin)).collect(Collectors.toList());

		} else if (sortType == SortTypeEnum.SORT_BY_DELTA_MAG) {
			// sort list excluding target object
			sortedList = fieldObjects.stream().filter(p -> !(p.isTarget()))
					.sorted(Comparator.comparingDouble(p -> Math.abs(p.getDeltaMag()))).collect(Collectors.toList());

		}
		assignApertureId(sortedList);

		// insert target first list item
		sortedList.add(0, targetObject);
		return sortedList;
	}

	/*
	 * Assigns aperture identifier T01 to target object, C02, C03 .. to selected
	 * reference objects and empty string for de-selected objects.
	 */
	private void assignApertureId(List<FieldObject> sorted) {
		int idx = 2;
		String apNumber = "";
		for (FieldObject fo : sorted) {
			if (fo.isTarget()) {
				apNumber = "T01";
			} else {
				apNumber = (fo.isSelected()) ? String.format("C%02d", idx++) : "";
			}
			fo.setApertureId(apNumber);
		}
	}

	@Override
	public String toString() {
		return "QueryResult [fieldObjects=" + fieldObjects + "]";
	}

	public static void main(String[] args) {

		// compile result object from file
		ApassFileReader fr = new ApassFileReader();

		CatalogQuery query = new CatalogQuery();
		
		//QueryResult result = new QueryResult(query);

		QueryResult result = fr.runQueryFromFile(query);

		double targetMag = 12.345;

		// sort by distance:
		List<FieldObject> sortedFilteredList = result.sortByDistance(targetMag);

		// test: monotonic increase in distance to target object
		boolean b = true;
		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
			double rad1 = sortedFilteredList.get(idx).getRadSepAmin();
			double rad = sortedFilteredList.get(idx - 1).getRadSepAmin();
			b = b && (rad1 > rad);
		}
		System.out.println("Sort by distance:");
		System.out.println(String.format("Sorted in increasing distance: %b", b));
		System.out.println();

		sortedFilteredList = result.sortByDeltaMag(targetMag);

		// test: monotonic increase in |obj_mag - tgtmag|
		b = true;
		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
			double mag1 = Math.abs(sortedFilteredList.get(idx).getMag() - targetMag);
			double mag = Math.abs(sortedFilteredList.get(idx - 1).getMag() - targetMag);
			b = b && (mag1 > mag);
		}
		System.out.println(String.format("Target mag = %.3f", targetMag));
		System.out.println(String.format("Sorted in increasing abs deltaMag: %b", b));

		System.out.println(String.format("Total no records =  %02d", result.getRecordsTotal()));

		// ***** getSortedFilteredList
		// test sort by distance
		CatalogSettings settings = new CatalogSettings();
		settings.setTargetMagSpinnerValue(targetMag);
		sortedFilteredList = result.getSortedList(settings);

		System.out.println("\nTest getSortedFileter:");
		sortedFilteredList.stream().forEach(System.out::println);

		b = true;
		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
			double rad1 = sortedFilteredList.get(idx).getRadSepAmin();
			double rad = sortedFilteredList.get(idx - 1).getRadSepAmin();
			b = b && (rad1 > rad);
		}
		System.out.println("\nSort by distance:");
		System.out.println(String.format("SortedFilteredList in increasing distance: %b", b));

		// test sort by mag diff
		settings.setDistanceRadioButtonValue(false);
		settings.setDeltaMagRadioButtonValue(true);
		System.out.println("\nSort by delta mag:");
		sortedFilteredList = result.getSortedList(settings);
		sortedFilteredList.stream().forEach(System.out::println);

		// test: monotonic increase in |obj_mag - tgtmag|
		b = true;
		for (int idx = 2; idx < sortedFilteredList.size(); idx++) {
			double mag1 = Math.abs(sortedFilteredList.get(idx).getMag() - targetMag);
			double mag = Math.abs(sortedFilteredList.get(idx - 1).getMag() - targetMag);
			b = b && (mag1 > mag);
		}
		System.out.println(String.format("Target mag = %.3f", targetMag));
		System.out.println(String.format("Sorted in increasing abs deltaMag: %b", b));

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
