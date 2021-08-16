package com.github.richardflee.astroimagej.catalog_ui;

import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;
import com.github.richardflee.astroimagej.data_objects.FieldObject;
import com.github.richardflee.astroimagej.data_objects.QueryResult;
import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.fileio.RaDecFileReader;
import com.github.richardflee.astroimagej.fileio.RaDecFileWriter;

/**
 * This class handles catalog_ui button click events to command database query,
 * update tale data and radec file read / write operations
 *
 */

// TTDO load query + target mag from props file or default query
// return string array & extract query & target
// default settings
public class ActionHandler {
	// reference to CatalogUI, main user form
	private CatalogUI catalogUi;

	// references to catalog database query and result objects
	private CatalogQuery query = null;
	private QueryResult result = null;
	private CatalogSettings settings = null;

	// interface reference updateTable
	private CatalogTableListener catalogTableListener;

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * 
	 * @param catalogUi reference to main user form interface
	 */
	public ActionHandler(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;

		// default settings
		this.settings = new CatalogSettings();

		// TTD replace with read props or default query
		query = new CatalogQuery();

		// TTD read target mag frm props file or deault settings value
		double targetMag = 12.34;
		settings.setTargetMagSpinnerValue(targetMag);
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * 
	 * @param catalogTableListener reference to CataTableListner interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.catalogTableListener = catalogTableListener;
	}

	// TTDO
	public void doSimbadQuery() {
		System.out.println("Simbad Query");
		CatalogQuery q;
		CatalogSettings s;
		q = catalogUi.getCatalogUiQuerySettings();
		s = catalogUi.getCatalogUiSortFilterSettings();

		q.setObjectId("fred");
		s.setTotalLabelValue(101);

		catalogUi.setCatalogUiQuerySettings(q);
		catalogUi.setCatalogUiSortFilterSettings(s);

	}

	/**
	 * Imports and writes to properties file current catalog Ui query parameters, plus subset 
	 * settings parameters
	 */
	public void doSaveQuerySettingsData() {
		PropertiesFileIO pf = new PropertiesFileIO();
		CatalogQuery query = catalogUi.getCatalogUiQuerySettings();
		CatalogSettings settings = catalogUi.getCatalogUiSortFilterSettings();
		pf.setPropertiesFileData(query, settings);
	}

	/**
	 * Runs a query on on-line astronomical database with query object parameters.
	 * Outputs result records in the catalog table.
	 */
	// TTDO replace apass file read with on-line q
	public void doCatalogQuery() {
		// file read demo ..
		ApassFileReader fr = new ApassFileReader();

		// resets sort filter settings and updates catalogui
		// retain current target mag value
		CatalogSettings settings = catalogUi.getCatalogUiSortFilterSettings();
		resetSettings(settings.getTargetMagSpinnerValue());

		// run query
		// TTD replace with online q
		QueryResult currentResult = fr.runQueryFromFile(query);

		// applies selected sort & filtered options to QueryResult object and updates
		// catalog tables
		updateCatalogUiTable(currentResult);

		// updates field value
		this.result = currentResult;
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astromagej folder
	 * <p>
	 * Example: ./astroimagej/radec/wasp_12.Rc.020.radec.txt
	 * </p>
	 */
	public void doSaveRaDecFile() {
		// current tcatalog table dataset
		List<FieldObject> sortedFilteredList = updateCatalogUiTable(result);

		// filter user selected records
		List<FieldObject> selectedList = sortedFilteredList.stream().filter(p -> p.isSelected())
				.collect(Collectors.toList());

		// writes sorted_filtered_selected data to radec file
		RaDecFileWriter fw = new RaDecFileWriter();
		fw.writeRaDecFile(selectedList, query);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 */
	public void doImportRaDecFile() {
		// exit of Cancel pressed => no file selected
		RaDecFileReader fr = new RaDecFileReader();
		if (!fr.getFileSelected()) {
			System.out.println("Cancel pressed");
			return;
		}

		// import query and table data
		// CatalogQuery query = fr.getQueryData();
		QueryResult currentResult = fr.getTableData();

		// table data
		updateCatalogUiTable(currentResult);

		// update field value
		this.result = currentResult;
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		updateCatalogUiTable(result);
	}

	/**
	 * Clears catalog table and resets catalogui settings
	 */
	public void doClearTable() {
		updateCatalogUiTable(null);
		resetSettings(settings.getTargetMagSpinnerValue());
	}

	// reset sort & filter settings, retains current target mag value
	private void resetSettings(double targetMag) {
		settings.resetDefaultSettings(targetMag);
		catalogUi.setCatalogUiSortFilterSettings(settings);
	}

	/*
	 * Sorts reference object records relative to target object. Sort options are
	 * radial distance or difference in magnitude values.
	 * 
	 * @return FieldObject list sorted and filtered as specified by user settings
	 */
	private List<FieldObject> updateCatalogUiTable(QueryResult result) {

		// clears table and exits if result = null
		catalogTableListener.updateTable(null);
		if (result == null) {
			return null;
		}

		// import current ui data & sort by distance or mag diff
		CatalogSettings currentSettings = catalogUi.getCatalogUiSortFilterSettings();
		List<FieldObject> sortedFilteredList = result.getSortedList(currentSettings);

		// apply nObs limit to reference object records
		int numberObs = (int) catalogUi.nObsSpinner.getValue();
		sortedFilteredList = sortedFilteredList.stream().filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget())))
				.collect(Collectors.toList());

		// apply mag limits filter
		if (currentSettings.isMagLimitsCheckBoxValue() == true) {
			double upperLimit = currentSettings.getUpperLimitSpinnerValue();
			double targetMag = currentSettings.getTargetMagSpinnerValue();
			double lowerLimit = currentSettings.getLowerLimitSpinnerValue();

			// apply filter range targetmag + lowerLimit to tagetMg + upperLimit
			// if lower or upper limit < 0.01 disable respective filter
			sortedFilteredList = sortedFilteredList.stream()
					.filter(p -> (Math.abs(upperLimit) < 0.01 || p.getMag() <= upperLimit + targetMag))
					.filter(p -> (Math.abs(lowerLimit) < 0.01 || p.getMag() >= lowerLimit + targetMag))
					.collect(Collectors.toList());
		}

		// update record and mag limit label values and update catalogui display
		currentSettings.updateLabelValues(result.getRecordsTotal(), sortedFilteredList.size() - 1);
		catalogUi.setCatalogUiSortFilterSettings(currentSettings);

		// run table update with sort / filter selections
		catalogTableListener.updateTable(sortedFilteredList);

		// update field value
		this.settings = currentSettings;

		// returns sorted-filtered list to export to radec file
		return sortedFilteredList;
	}

	public static void main(String args[]) {

		ApassFileReader fr = new ApassFileReader();
		CatalogQuery query = new CatalogQuery();
		QueryResult result = fr.runQueryFromFile(query);
		CatalogSettings settings = new CatalogSettings();

		settings.setTargetMagSpinnerValue(15.0);
		settings.setUpperLimitSpinnerValue(0.5);
		settings.setLowerLimitSpinnerValue(-1.5);

		System.out.println(result.toString());

//		List<FieldObject> fieldObjects = result.getFieldObjects();
//		FieldObject target = fieldObjects.stream().filter(p -> p.isTarget()).findFirst().get();
//		fieldObjects.remove(target);
//		target.setApertureId("T01");

//		double magUpperLimit = settings.getUpperLimitSpinnerValue();
//		double magLowerLimit = settings.getLowerLimitSpinnerValue();

		// List<FieldObject> sortedFilteredList = result.getSortedList(settings);

//		sortedFilteredList.stream()
//				.filter(p -> (Math.abs(magUpperLimit) < 0.01 || p.getMag() <= magUpperLimit))
//				.collect(Collectors.toList());

//		// sort by distance
//		List<FieldObject> sortByRadSep = fieldObjects.stream()
//				.sorted(Comparator.comparingDouble(FieldObject::getRadSepAmin)).collect(Collectors.toList());
//
//		int idx = 2;
//		for (FieldObject fo : sortByRadSep) {
//			String apNumber = (fo.isSelected()) ? String.format("C%02d", idx++) : "";
//			fo.setApertureId(apNumber);
//		}
//		sortByRadSep.add(0, target);
//		System.out.println("here");
//
//		double tgtMag = 16.046;
//
//		List<FieldObject> sortByDeltaMag = fieldObjects.stream()
//				.sorted(Comparator.comparingDouble(p -> Math.abs((p.getMag() - tgtMag)))).collect(Collectors.toList());
//		idx = 2;
//		for (FieldObject fo : sortByDeltaMag) {
//			String apNumber = (fo.isSelected()) ? String.format("C%02d", idx++) : "";
//			fo.setApertureId(apNumber);
//		}
//
//		sortByDeltaMag.add(0, target);
//		for (FieldObject fo : sortByDeltaMag) {
//			System.out.println(fo.toString());
//		}
//		

	}
}
