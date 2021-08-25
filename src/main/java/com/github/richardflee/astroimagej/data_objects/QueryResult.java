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
		target.setAccepted(true);
		target.setApertureId("T01");

		target.setRadSepAmin(0.0);
		target.setDeltaMag(0.0);
		target.setnObs(1);
		fieldObjects.add(target);
	}
	
	/**
	 * Creates a new ArrayList copy of CatalogResult field objects array
	 * 
	 * @return new ArrayList full copy of FieldObjects list 
	 */
//	public List<FieldObject> copyFieldObjects() {
//		List<FieldObject> copyList = new ArrayList<>();
//		for (FieldObject fo : this.getFieldObjects()) {
//			copyList.add(new FieldObject(fo));
//		}
//		return copyList;
//	}

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
	 * Total number of reference field objects, excludes target object
	 * 
	 * @return total reference field objects
	 */
	public int getRecordsTotal() {
		return fieldObjects.size() - 1;
	}
	
	/**
	 * Number of reference field objects within filter limits, excludes target object
	 * 
	 * @return total accepted field objects
	 */	
	public int getAcceptedTotal() {
		long count = getFieldObjects().stream().filter(p -> p.isAccepted()).count() - 1;
		return Math.toIntExact(count);
	}

	/**
	 * Returns first item from list where isTarget is true
	 * 
	 * @return reference to target object
	 */
	public FieldObject getTargetObject() {
		return fieldObjects.stream().filter(p -> p.isTarget()).findFirst().get();
	}
	
	public void setTargetObject(FieldObject fo) {
		FieldObject target= getTargetObject();
		
		// data
		target.setObjectId(fo.getObjectId());
		target.setRaHr(fo.getRaHr());
		target.setDecDeg(fo.getDecDeg());
		target.setMag(fo.getMag());
		
		// presets
		target.setTarget(true);
		target.setApertureId("T01");
		target.setMagErr(0.00);
		target.copyDeltaMag(0.0);		
		target.setRadSepAmin(0.0);
		target.setnObs(1);
		target.setSelected(true);
		target.setAccepted(true);
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
		//result.getFieldObjects().stream().forEach(System.out::println);
		System.out.println(String.format("\nTarget mag expected 12.345: %.3f", result.getTargetMag()));
		
		targetMag = 9.875;
		result.setTargetMag(targetMag);
		System.out.println(String.format("Target mag expected 9.875: %.3f", result.getTargetMag()));
		
		List<FieldObject> acceptedList = result.getFieldObjects();
		long counter = acceptedList.stream().filter(p -> p.isAccepted()).count();
		System.out.println(String.format("Start accepted field objects: %d", counter));
		
		acceptedList.get(1).setAccepted(false);
		counter = acceptedList.stream().filter(p -> p.isAccepted()).count();
		System.out.println(String.format("Accepted field objects after filter one: %d", counter));
		
		result = null;
	}
}
