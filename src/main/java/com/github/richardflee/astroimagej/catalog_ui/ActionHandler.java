package com.github.richardflee.astroimagej.catalog_ui;

import java.io.File;
import java.util.Comparator;
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
	private CatalogTableListener tableListener;
	private CatalogDataListener dataListener;

	// references to catalog database query and result objects
	private PropertiesFileIO propertiesFile = null;

	// result object compiled from database query records or imported from radec
	// file
	private QueryResult result = null;

	// catalog table dataset compiled running updateCatalogTable
	private List<FieldObject> sortedFilteredList = null;
	
	private RaDecFileWriter fileWriter;

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * 
	 * @param tableListener reference to main user form interface
	 */
	public ActionHandler(PropertiesFileIO propertiesFile) {
		this.propertiesFile = propertiesFile;
		this.fileWriter = new RaDecFileWriter();
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * 
	 * @param catalogTableListener reference to CataTableListner interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.tableListener = catalogTableListener;
	}

	public void setCatalogDataListener(CatalogDataListener catalogDataListener) {
		this.dataListener = catalogDataListener;
	}

	// TTDO
	public void doSimbadQuery() {

		String message = "ERROR: SIMBAD Query";
		dataListener.updateStatus(message);
	}

	/**
	 * Imports and writes to properties file current catalog Ui query parameters,
	 * plus a subset settings parameters
	 */
	public void doSaveQuerySettingsData() {
		CatalogQuery query = dataListener.getQueryData();
		CatalogSettings settings = dataListener.getSettingsData();

		// update properties file
		String statusMessage = propertiesFile.setPropertiesFileData(query, settings);
		dataListener.updateStatus(statusMessage);
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
		CatalogSettings settings = dataListener.getSettingsData();
		resetSettings(settings.getTargetMagSpinnerValue());

		// run query
		// TTD replace with online q
		CatalogQuery query = dataListener.getQueryData();
		QueryResult currentResult = fr.runQueryFromFile(query);

		// updates field value
		this.result = currentResult;

		// applies selected sort & filtered options to QueryResult object and updates
		// catalog tables
		updateCatalogTable();
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astromagej folder
	 * <p>
	 * Example: ./astroimagej/radec/wasp_12.Rc.020.radec.txt
	 * </p>
	 */
	public void doSaveRaDecFile() {

		// filter user selected records
		List<FieldObject> selectedList = sortedFilteredList.stream()
				.filter(p -> p.isSelected())
				.collect(Collectors.toList());

		// writes sorted_filtered_selected data to radec file
		CatalogQuery query = this.result.getQuery();
		this.fileWriter.writeRaDecFile(selectedList, query);
		
		// reset catalog ui query and sort-fileter settings
		double targetMag = result.getTargetMag();
		CatalogSettings settings = new CatalogSettings(targetMag);
		
		// record numbers
		int nFilteredRecords = sortedFilteredList.size() - 1;
		settings.updateLabelValues(result.getRecordsTotal(), nFilteredRecords);
		
		// apply updates
		dataListener.setQueryData(query);
		dataListener.setSettingsData(settings);
		

		// status line
		String message = fileWriter.getStatusMessage();
		dataListener.updateStatus(message);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 * 
	 * @return true if file read operation was successful
	 */
	public boolean doImportRaDecFile() {
		RaDecFileReader fr = new RaDecFileReader();

		QueryResult currentResult = fr.readRaDecData();
		if (currentResult == null) {
			String message = fr.getStatusMessage();
			dataListener.updateStatus(message);
			return (this.result != null);
		}

		CatalogQuery query = currentResult.getQuery();

		// update cataloguo query
		dataListener.setQueryData(query);

		// status line
		String message = fr.getStatusMessage();
		dataListener.updateStatus(message);

		// update field value
		this.result = currentResult;

		// table data
		updateCatalogTable();

		return true;
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		updateCatalogTable();
	}

	/**
	 * Clears catalog table and resets catalogui settings
	 */
	public void doClearTable() {

		// clear result field
		this.result = null;

		// clear sortedFiltered list
		this.sortedFilteredList = null;

		CatalogSettings settings = dataListener.getSettingsData();
		resetSettings(settings.getTargetMagSpinnerValue());

		// status line
		String message = "Cleared catalog result table, reset sort and filter settings";
		dataListener.updateStatus(message);

		// clears table with null result
		updateCatalogTable();
	}

	// reset sort & filter settings, retains current target mag value
	private void resetSettings(double targetMag) {
		CatalogSettings settings = new CatalogSettings(targetMag);

		// TTD get query from result
		dataListener.setSettingsData(settings);
	}

	/*
	 * Sorts QueryResult result object records relative to target object. <p>Sort
	 * options are radial distance or difference in magnitude values.</p>
	 * 
	 * @return FieldObject list sorted and filtered as specified by user settings
	 */
	private void updateCatalogTable() {

		// clears table and exits if result = null
		tableListener.updateTable(null);
		if (result == null) {
			return;
		}

		// import current ui data
		CatalogSettings settings = dataListener.getSettingsData();

		// get pesky targetMag value
		double targetMag = settings.getTargetMagSpinnerValue();

		// updates target mag & ref object delta mag with current pesky value ..
		result.updateDeltaMags(targetMag);

		// list to populate catalog table with appleid sort & filter settings
		List<FieldObject> currentSortedFilteredList = result.getFieldObjects();

		// sort by distance option
		if (settings.isDistanceRadioButtonValue() == true) {
			currentSortedFilteredList = currentSortedFilteredList.stream()
					.sorted(Comparator.comparingDouble(FieldObject::getRadSepAmin)).collect(Collectors.toList());
			// sort by delta mag option
		} else if (settings.isDeltaMagRadioButtonValue() == true) {
			currentSortedFilteredList = currentSortedFilteredList.stream()
					.sorted(Comparator.comparingDouble(p -> Math.abs(p.getDeltaMag()))).collect(Collectors.toList());
		}

		// apply nObs limit to reference object records
		int numberObs = settings.getnObsSpinnerValue();
		currentSortedFilteredList = currentSortedFilteredList.stream()
				.filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget()))).collect(Collectors.toList());

		// apply mag limits filter
		if (settings.isMagLimitsCheckBoxValue() == true) {
			double upperLimit = settings.getUpperLimitSpinnerValue();
			double lowerLimit = settings.getLowerLimitSpinnerValue();

			// apply filter range targetmag + lowerLimit to tagetMg + upperLimit
			// if lower or upper limit < 0.01 disable respective filter
			currentSortedFilteredList = currentSortedFilteredList.stream()
					.filter(p -> (Math.abs(upperLimit) < 0.01 || p.getMag() <= upperLimit + targetMag))
					.filter(p -> (Math.abs(lowerLimit) < 0.01 || p.getMag() >= lowerLimit + targetMag))
					.collect(Collectors.toList());
		}

		// update record and mag limit label values and update catalogui display
		int nFilteredRecords = currentSortedFilteredList.size() - 1;
		settings.updateLabelValues(result.getRecordsTotal(), nFilteredRecords);
		dataListener.setSettingsData(settings);

		// run table update with sort / filter selections
		tableListener.updateTable(currentSortedFilteredList);

		this.sortedFilteredList = currentSortedFilteredList;
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
		System.out.println(
				String.format("Default decDms +29:40:20.27: %s", AstroCoords.decDeg_To_decDms(q0.getDecDeg())));

		System.out.println(String.format("\nModified query object freddy: %s", q1.getObjectId()));
		System.out.println(String.format("Modified query decDeg -23.456: %.3f", q1.getDecDeg()));
		System.out.println(String.format("Modified settings targetMag 7.89: %.2f", s1.getTargetMagSpinnerValue()));
	}
}
