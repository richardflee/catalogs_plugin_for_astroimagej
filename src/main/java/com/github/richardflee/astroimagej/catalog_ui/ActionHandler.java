package com.github.richardflee.astroimagej.catalog_ui;

import java.io.File;
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
import com.github.richardflee.astroimagej.utils.AstroCoords;

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
	private PropertiesFileIO propertiesFile = null;
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

		propertiesFile = new PropertiesFileIO();
		
		this.query = propertiesFile.getPropertiesQueryData();
		this.settings = propertiesFile.getPropertiesSettingsData();
		catalogUi.setQueryData(query);
		catalogUi.setSettingsData(settings);
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
		q = catalogUi.getQueryData();
		s = catalogUi.getSettingsData();

		q.setObjectId("fred");
		s.setTotalLabelValue(101);

		catalogUi.setQueryData(q);
	}

	/**
	 * Imports and writes to properties file current catalog Ui query parameters,
	 * plus a subset settings parameters
	 */
	public void doSaveQuerySettingsData() {
		CatalogQuery query = catalogUi.getQueryData();
		CatalogSettings settings = catalogUi.getSettingsData();
		this.propertiesFile.setPropertiesFileData(query, settings);
	}

	/**
	 * Runs a query on on-line astronomical database with query object parameters.
	 * Outputs result records in the catalog table.
	 */
	// TTDO replace apass file read with on-line q
	public void doCatalogQuery() {
		
		// save current query & target mag data
		doSaveQuerySettingsData();
		
		// file read demo ..
		ApassFileReader fr = new ApassFileReader();

		// resets sort filter settings and updates catalogui
		// retain current target mag value
		CatalogSettings settings = catalogUi.getSettingsData();
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
		catalogUi.setSettingsData(settings);
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
		CatalogSettings currentSettings = catalogUi.getSettingsData();
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
		catalogUi.setSettingsData(currentSettings);

		// run table update with sort / filter selections
		catalogTableListener.updateTable(sortedFilteredList);

		// update field value
		this.settings = currentSettings;

		// returns sorted-filtered list to export to radec file
		return sortedFilteredList;
	}

	public static void main(String args[]) {

		// property file tests: new file, default values, modified values
		
		// roundabout way to delete properties file ..
		PropertiesFileIO pf = new PropertiesFileIO();
		File f = new File(pf.getPropertiesFilePath());
		f.delete();
		
		// .. then make a new one with default values
		pf = new PropertiesFileIO();
		CatalogQuery q0 = pf.getPropertiesQueryData();
		CatalogSettings s0 = pf.getPropertiesSettingsData();

		// modify q0 and s0 & update properties file
		q0.setObjectId("freddy");
		q0.setDecDeg(-23.456);
		s0.resetDefaultSettings(7.89);
		pf.setPropertiesFileData(q0, s0);

		CatalogQuery q1 = pf.getPropertiesQueryData();
		CatalogSettings s1 = pf.getPropertiesSettingsData();
		
		System.out.println(String.format("Default object WASP 12: %s", q0.getObjectId()));
		System.out.println(String.format("Default decDms +29:40:20.27: %s", AstroCoords.decDeg_To_decDms(q0.getDecDeg())));

		System.out.println(String.format("\nModified query object freddy: %s", q1.getObjectId()));
		System.out.println(String.format("Modified query decDeg -23.456: %.3f", q1.getDecDeg()));
		System.out.println(String.format("Modified settings targetMag 7.89: %.2f", s1.getTargetMagSpinnerValue()));
	}
}
