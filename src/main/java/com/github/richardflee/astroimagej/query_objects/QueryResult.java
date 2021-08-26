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
 * databases in a list of FieldObjects <p> Target star data is the first list
 * item, identified "T01". The remainder is a list of reference star objects
 * ordered either by radial distance from the target star (in arcmin) or by the
 * absolute difference in magnitude. If user selected, reference objects are
 * identified as "C02", "C03" .. in sort order </p>
 */
public class QueryResult {

	// query settings associated with current QueryResult
	private CatalogQuery query = null;

	// catalog ui dialog sort and filter settings
	private CatalogSettings settings;

	// list of target and refrence field objects
	private List<FieldObject> fieldObjects;

	/**
	 * Constructor for QueryResult objects created by query of on-line astronomical
	 * database or importing radec dataset.
	 * 
	 * <p> A QueryResult object comprises the database query object and a list of
	 * FieldObjects initialised with a single target object. </p>
	 * 
	 * <p> Target fields are extracted from the query catalog query.</p>
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

	// settings constructor & assign catalog ui target mag to target field object
	public void setSettings(CatalogSettings settings) {
		this.settings = new CatalogSettings(settings);
		getTargetObject().setMag(settings.getTargetMagSpinnerValue());
	}

	// import list field objects and update delta mag & radeSep fields
	public void addFieldObjects(List<FieldObject> fieldObjects) {

		// append reference field objects to target object
		this.fieldObjects.addAll(fieldObjects);

		FieldObject target = getTargetObject();
		double targetMag = target.getMag();

		for (FieldObject fo : this.fieldObjects) {
			fo.setDeltaMag(targetMag);
			fo.setRadSepAmin(target);
		}
	}

	public void applySelectedSort() {
		// sort by distance option
		if (settings.isDistanceRadioButtonValue() == true) {
			fieldObjects = fieldObjects.stream()
					.sorted(Comparator.comparingDouble(p -> p.getRadSepAmin()))
					.collect(Collectors.toList());
			// sort by delta mag option
		} else if (settings.isDeltaMagRadioButtonValue() == true) {
			fieldObjects = fieldObjects.stream().sorted(Comparator
					.comparingDouble(p -> Math.abs(p.getDeltaMag())))
					.collect(Collectors.toList());
		}
	}

	// getters / setters

	public CatalogQuery getQuery() {
		return query;
	}

	public void setQuery(CatalogQuery query) {
		this.query = query;
	}
	
	public CatalogSettings getSettings() {
		return this.settings;
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
	 * Number of reference field objects within filter limits, excludes target
	 * object
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

	/**
	 * Sets properties for target object fo imported from radec file
	 * 
	 * @param fo target field object
	 */
	public void setTargetObject(FieldObject fo) {
		FieldObject target = getTargetObject();

		// data
		target.setObjectId(fo.getObjectId());
		target.setRaHr(fo.getRaHr());
		target.setDecDeg(fo.getDecDeg());

		// radec value targetMag
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

	// TTD remove
	public double getTargetMag() {
		return getTargetObject().getMag();
	}

	// TTD remove
	public void setTargetMag(double targetMag) {
		getTargetObject().setMag(targetMag);
	}

	/**
	 * Updates field object delta mag fields: delatMag = obj_mag - tgt_mag
	 * 
	 * @param targetMag target object nominal mag
	 */
	// ttd make private
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

		// build default catalog result object, init new result object
		CatalogQuery query = new CatalogQuery();
		QueryResult result = new QueryResult(query);

		// build default CatalogSettngs object, asign to result_settings
		CatalogSettings settings = new CatalogSettings(targetMag);
		result.setSettings(settings);

		// compile ref object list from apass file
		ApassFileReader fr = new ApassFileReader();
		List<FieldObject> referenceObjects = fr.runQueryFromFile(query);
		result.addFieldObjects(referenceObjects);

		// sort option radial distance
		result.getSettings().setDistanceRadioButtonValue(true);
		result.getSettings().setDeltaMagRadioButtonValue(false);
		result.applySelectedSort();

		boolean sorted = true;
		for (int idx = 1; idx < result.getFieldObjects().size(); idx++) {
			sorted = sorted && (result.getFieldObjects().get(idx).getRadSepAmin() > result.getFieldObjects().get(idx - 1).getRadSepAmin());
		}
		System.out.println(String.format("Sorted by radial distance: %b", sorted));

		// sort option delta mag
		result.getSettings().setDistanceRadioButtonValue(false);
		result.getSettings().setDeltaMagRadioButtonValue(true);
		result.applySelectedSort();

		sorted = true;
		for (int idx = 1; idx < result.getFieldObjects().size(); idx++) {
			double item1 = Math.abs(result.getFieldObjects().get(idx).getDeltaMag());
			double item0 = Math.abs(result.getFieldObjects().get(idx - 1).getDeltaMag());
			
			sorted = sorted && (item1 >= item0);
			
			// sorted = sorted && (Math.abs(list.get(idx).getDeltaMag()) >= Math.abs(list.get(idx - 1).getDeltaMag()));
		}
		System.out.println(String.format("Sorted by delta mag: %b", sorted));

		// result.getFieldObjects().stream().forEach(System.out::println);;

		// System.out.println(result.toString());

		// target mag test
		FieldObject tgt = result.getTargetObject();
		System.out.println("\nTest target mag");
		System.out.println("input    Settings   Target");
		System.out.println(
				String.format("%7.3f %7.3f  %9.3f", targetMag, settings.getTargetMagSpinnerValue(), tgt.getMag()));

		FieldObject fo2 = result.getFieldObjects().get(2);
		FieldObject fo7 = result.getFieldObjects().get(7);
		double rad0 = 0.0;
		double rad2 = 1.0424;
		double rad7 = 2.1266;

		// radial distance test
		System.out.println("\nTest radial separation");
		System.out.println("raHr       decDeg      radSep  Trig");
		System.out.println(
				String.format("%.7f  %.6f  %.4f   %.4f", tgt.getRaHr(), tgt.getDecDeg(), tgt.getRadSepAmin(), rad0));
		System.out.println(
				String.format("%.7f  %.6f  %.4f   %.4f", fo2.getRaHr(), fo2.getDecDeg(), fo2.getRadSepAmin(), rad2));
		System.out.println(
				String.format("%.7f  %.6f  %.4f   %.4f", fo7.getRaHr(), fo7.getDecDeg(), fo7.getRadSepAmin(), rad7));

		// delta mag test
		System.out.println("\nTest delta mag");
		System.out.println("Mag        Delta     Sums");
		System.out.println(
				String.format("%7.3f  %7.3f   %7.3f", tgt.getMag(), tgt.getDeltaMag(), tgt.getMag() - targetMag));
		System.out.println(
				String.format("%7.3f  %7.3f   %7.3f", fo2.getMag(), fo2.getDeltaMag(), fo2.getMag() - targetMag));
		System.out.println(
				String.format("%7.3f  %7.3f   %7.3f", fo7.getMag(), fo7.getDeltaMag(), fo7.getMag() - targetMag));

//		// update delta mag
//		result.updateDeltaMags(targetMag);		
//		//result.getFieldObjects().stream().forEach(System.out::println);
//		System.out.println(String.format("\nTarget mag expected 12.345: %.3f", result.getTargetMag()));
//		
//		targetMag = 9.875;
//		result.setTargetMag(targetMag);
//		System.out.println(String.format("Target mag expected 9.875: %.3f", result.getTargetMag()));
//		
//		List<FieldObject> acceptedList = result.getFieldObjects();
//		long counter = acceptedList.stream().filter(p -> p.isAccepted()).count();
//		System.out.println(String.format("Start accepted field objects: %d", counter));
//		
//		acceptedList.get(1).setAccepted(false);
//		counter = acceptedList.stream().filter(p -> p.isAccepted()).count();
//		System.out.println(String.format("Accepted field objects after filter one: %d", counter));
//		
//		result = null;
	}
}
