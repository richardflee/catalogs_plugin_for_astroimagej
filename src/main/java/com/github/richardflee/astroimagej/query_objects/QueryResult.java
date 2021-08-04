package com.github.richardflee.astroimagej.query_objects;

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

	private List<FieldObject> fieldObjects;

	public QueryResult(CatalogQuery query) {
		fieldObjects = new ArrayList<>();
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

	private List<FieldObject> sortFieldObjects(double targetMag, SortTypeEnum sortType) {
		
		List<FieldObject> sortedList  = null;
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

	public int getTotalRecords( ) {
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

	// getters / setters

	public List<FieldObject> getFieldObjects() {
		return fieldObjects;
	}

	public void setFieldObject(FieldObject fieldObject) {
		fieldObjects.add(fieldObject);
	}

	@Override
	public String toString() {
		return "QueryResult [fieldObjects=" + fieldObjects + "]";
	}

	public static void main(String[] args) {

		// compile result object from file
		ApassFileReader fr = new ApassFileReader();
		CatalogQuery query = new CatalogQuery();
		QueryResult result = fr.runQueryFromFile(query);
		double targetMag = 12.345;

		// sort by distance:
		List<FieldObject> sorted = result.sortByDistance(targetMag);

		// test: monotonic increase in distance to target object
		boolean b = true;
		for (int idx = 2; idx < sorted.size(); idx++) {
			double rad1 = sorted.get(idx).getRadSepAmin();
			double rad = sorted.get(idx - 1).getRadSepAmin();
			b = b && (rad1 > rad);
		}
		System.out.println("Sort by distance:");
		System.out.println(String.format("Sorted in increasing distance: %b", b));
		System.out.println();


		sorted = result.sortByDeltaMag(targetMag);

		// test: monotonic increase in |obj_mag - tgtmag|
		b = true;
		for (int idx = 2; idx < sorted.size(); idx++) {
			double mag1 = Math.abs(sorted.get(idx).getMag() - targetMag);
			double mag = Math.abs(sorted.get(idx - 1).getMag() - targetMag);
			b = b && (mag1 > mag);
		}
		System.out.println(String.format("Target mag = %.3f", targetMag));
		System.out.println(String.format("Sorted in increasing abs deltaMag: %b", b));
		
		System.out.println(String.format("Total no records =  %02d", result.getTotalRecords()));

	}
}
