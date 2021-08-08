package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;

/**
 * This class handles catalog_ui button click events to command database query,
 * update tale data and radec file read / write operations
 *
 */
public class ActionHandler {
	// reference to CatalogUI, main user form
	private CatalogUI catalogUi;

	// references to catalog database query and result objects
	private CatalogQuery query;
	private QueryResult result;

	// interface reference updateTable
	private CatalogTableListener catalogTableListener;

	// catalogUi field and spinner values
	private double targetMag;
	private double magUpperLimit;
	private double magLowerLimit;

	private int numberObs;

	private boolean isMagLimitsSelected;
	private boolean isRadSepSelected;
	private boolean isDeltaMagSelected;

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * 
	 * @param catalogUi reference to main user form interface
	 */
	public ActionHandler(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;
		importCatalogUiSettings();

		// TDD replace with cat query build method
		query = new CatalogQuery();
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * 
	 * @param catalogTableListener reference to CataTableListner interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.catalogTableListener = catalogTableListener;
	}

	/**
	 * Runs a query on on-line astronomical database with query object parameters.
	 * Outputs result records in the catalog table.
	 */

	// TTD test with catalog query methods
	public void doCatalogQuery() {
		// file read demo ..
		ApassFileReader fr = new ApassFileReader();
		QueryResult result = fr.runQueryFromFile(query);
		this.result = result;

		// Update total reference object records returned by catalog query
		// excluding target object which is always the top table entry
		int totalRecords = result.getTotalRecords();
		totalRecords = (totalRecords > 0) ? totalRecords : 0;
		catalogUi.totalLabel.setText(String.format("%3d", totalRecords));

		// uodates table with sorted & optioanlly filtered query results
		updateCatalogTable(result);
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		updateCatalogTable(result);
	}

	/**
	 * Clears catalog table and resets totals
	 * 
	 */
	public void doClearTable() {
		catalogUi.filteredLabel.setText("0");
		catalogUi.totalLabel.setText("0");
		updateCatalogTable(null);
	}

	/*
	 * Sorts reference object records relative to target object. Sort options are
	 * radial distance or difference in magnitude values.
	 */
	private void updateCatalogTable(QueryResult result) {
		if (result == null) {
			catalogTableListener.updateTable(null);
			return;
		}
		// current ui data
		importCatalogUiSettings();

		// updates query result object with user-input target mag value
		result.getTargetObject().setMag(targetMag);

		// sort relative to target
		// sort type from selected radio button: sorts radial distance or absolute
		// difference
		List<FieldObject> sortedList = null;
		if (isRadSepSelected) {
			sortedList = result.sortByDistance(targetMag);
		} else if (isDeltaMagSelected) {
			sortedList = result.sortByDeltaMag(targetMag);
		}

		// apply nObs limit
		List<FieldObject> sortedFilteredList = null;
		sortedFilteredList = sortedList.stream().filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget())))
				.collect(Collectors.toList());

		// option to apply mag difference filters
		// if upper and/or lower limits < 0.1 (effectively 0) then limits are not
		// applied (N/A)
		if (isMagLimitsSelected) {
			final double upperLimit = ((Math.abs(magUpperLimit) < 0.01) ? 100.0 : targetMag + magUpperLimit);
			String limit = (Math.abs(magUpperLimit) < 0.01) ? "N/A" : String.format("%.1f", upperLimit);
			catalogUi.upperLabel.setText(limit);

			final double lowerLimit = ((Math.abs(magLowerLimit) < 0.01) ? -100.0 : targetMag + magLowerLimit);
			limit = (Math.abs(magLowerLimit) < 0.01) ? "N/A" : String.format("%.1f", lowerLimit);
			catalogUi.lowerLabel.setText(limit);

			// apply mag upper & lower mag limits
			sortedFilteredList = sortedFilteredList.stream().filter(p -> (p.getMag() >= lowerLimit))
					.filter(p -> p.getMag() <= upperLimit).collect(Collectors.toList());
		}

		// update number of filtered records, clip to 0 if count is negative
		int filteredRecords = sortedFilteredList.size() - 1;
		filteredRecords = (filteredRecords > 0) ? filteredRecords : 0;
		catalogUi.filteredLabel.setText(String.format("%3d", filteredRecords));

		// run table update with sort / filter selections
		catalogTableListener.updateTable(sortedFilteredList);
	}

	/*
	 * Import catalogUI sort and filter control values
	 */
	private void importCatalogUiSettings() {
		// values of nominal mag, upper and lower limits and state of apply filter
		// checkbox
		targetMag = Double.valueOf(catalogUi.magSpinner.getValue().toString());
		magUpperLimit = Double.valueOf(catalogUi.upperLimitSpinner.getValue().toString());
		magLowerLimit = Double.valueOf(catalogUi.lowerLimitSpinner.getValue().toString());
		isMagLimitsSelected = Boolean.valueOf(catalogUi.isMagLimitsCheckBox.isSelected());

		// sort option, mutually exclusive
		isRadSepSelected = Boolean.valueOf((Boolean) catalogUi.radSepRadioButton.isSelected());
		isDeltaMagSelected = Boolean.valueOf((Boolean) catalogUi.deltaMagRadioButton.isSelected());

		// number of observations spin control, relevant to apass catalog only
		numberObs = Integer.valueOf(catalogUi.nObsSpinner.getValue().toString());
	}

	public static void main(String args[]) {

		ApassFileReader fr = new ApassFileReader();
		CatalogQuery query = new CatalogQuery();
		QueryResult result = fr.runQueryFromFile(query);
		System.out.println(result.toString());

		List<FieldObject> fieldObjects = result.getFieldObjects();
		FieldObject target = fieldObjects.stream().filter(p -> p.isTarget()).findFirst().get();
		fieldObjects.remove(target);
		target.setApertureId("T01");

		// sort by distance
		List<FieldObject> sortByRadSep = fieldObjects.stream()
				.sorted(Comparator.comparingDouble(FieldObject::getRadSepAmin)).collect(Collectors.toList());

		int idx = 2;
		for (FieldObject fo : sortByRadSep) {
			String apNumber = (fo.isSelected()) ? String.format("C%02d", idx++) : "";
			fo.setApertureId(apNumber);
		}
		sortByRadSep.add(0, target);
		System.out.println("here");

		double tgtMag = 16.046;

		List<FieldObject> sortByDeltaMag = fieldObjects.stream()
				.sorted(Comparator.comparingDouble(p -> Math.abs((p.getMag() - tgtMag)))).collect(Collectors.toList());
		idx = 2;
		for (FieldObject fo : sortByDeltaMag) {
			String apNumber = (fo.isSelected()) ? String.format("C%02d", idx++) : "";
			fo.setApertureId(apNumber);
		}

		sortByDeltaMag.add(0, target);
		for (FieldObject fo : sortByDeltaMag) {
			System.out.println(fo.toString());
		}
	}
}
