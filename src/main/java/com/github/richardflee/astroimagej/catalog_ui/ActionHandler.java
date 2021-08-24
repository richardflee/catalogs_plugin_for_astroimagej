package com.github.richardflee.astroimagej.catalog_ui;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.catalogs.SimbadCatalog;
import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;
import com.github.richardflee.astroimagej.data_objects.FieldObject;
import com.github.richardflee.astroimagej.data_objects.QueryResult;
import com.github.richardflee.astroimagej.data_objects.SimbadResult;
import com.github.richardflee.astroimagej.exceptions.SimbadNotFoundException;
import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.fileio.RaDecFileReader;
import com.github.richardflee.astroimagej.fileio.RaDecFileWriter;
import com.github.richardflee.astroimagej.listeners.CatalogDataListener;
import com.github.richardflee.astroimagej.listeners.CatalogTableListener;
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
	private CatalogTableListener catalogTableListener;
	private CatalogDataListener catalogDataListener;

	// references to catalog database query and result objects
	private PropertiesFileIO propertiesFile = null;
	private RaDecFileReader radecFileReader = null;
	private RaDecFileWriter fileWriter = null;

	/*
	 * result field: object compiled from database query records or imported from
	 * radec file tableRowsList field: copy of results field objects array,
	 * reference to catalog table rows
	 * 
	 * doCatalogQuery & doImportRaDec: create new result and tableRowsList
	 * doSaveRadec saves selected tableRowsList records to radec file doUpdate
	 * resets tableRowsList to full dataset before applying current sort / filter
	 * options doClear sets objects to null
	 */
	private QueryResult result = null;
	//private List<FieldObject> tableRowsList = null;

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * 
	 * @param catalogTableListener reference to main user form interface
	 */
	public ActionHandler(PropertiesFileIO propertiesFile) {
		this.propertiesFile = propertiesFile;
		this.fileWriter = new RaDecFileWriter();
		this.radecFileReader = new RaDecFileReader();
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * 
	 * @param catalogTableListener reference to CataTableListner interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.catalogTableListener = catalogTableListener;
	}

	public void setCatalogDataListener(CatalogDataListener catalogDataListener) {
		this.catalogDataListener = catalogDataListener;
	}

	public void doSimbadQuery() {
		SimbadCatalog simbad = new SimbadCatalog();
		SimbadResult simbadResult = null;
		String statusMessage = null;

		CatalogQuery query = catalogDataListener.getQueryData();

		// run query and update simbad section with results, "." => no data
		if (query != null) {
			try {
				simbadResult = simbad.runQuery(query);
				catalogDataListener.setSimbadData(simbadResult);
			} catch (SimbadNotFoundException se) {
				catalogDataListener.setSimbadData(null);
			}
			statusMessage = simbad.getStatusMessage();

			// query = null => at least one jtext input is not valid
		} else {
			statusMessage = "ERROR: Invalid text input";
		}
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Imports and writes to properties file current catalog Ui query parameters,
	 * plus a subset of settings parameters
	 */
	public void doSaveQuerySettingsData() {
		String statusMessage = null;

		CatalogQuery query = catalogDataListener.getQueryData();
		if (query != null) {
			CatalogSettings settings = catalogDataListener.getSettingsData();
			statusMessage = propertiesFile.setPropertiesFileData(query, settings);
		} else {
			statusMessage = "ERROR: Invalid input in Catalog Query settings text field";
		}
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Runs a query on on-line astronomical database with query object parameters.
	 * Outputs result records in the catalog table.
	 */
	// TTDO replace apass file read with on-line q
	public void doCatalogQuery() {
		// save current query & target mag data
		doSaveQuerySettingsData();

		// get current query & settings data
		CatalogQuery query = catalogDataListener.getQueryData();

		// resets settings to default, retains current settings targtMag value
		CatalogSettings settings = catalogDataListener.getSettingsData();
		settings.resetDefaultSettings(null);

		// run query -- TTD replace file read demo ..

		// fields: reference field result to new CatlogRecult object
		// set tableRows = full data set all items selected
		ApassFileReader fr = new ApassFileReader();
		this.result = fr.runQueryFromFile(query);
		//this.tableRowsList = result.copyFieldObjects();

		// updates catalog table with full dataset
		updateCatalogTable(settings);
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astromagej folder
	 * <p>
	 * Example: ./astroimagej/radec/wasp_12.Rc.020.radec.txt
	 * </p>
	 */
	public void doSaveRaDecFile() {
//
//		// filter user selected records
//		List<FieldObject> selectedTableList = filteredTableList.stream().filter(p -> p.isSelected())
//				.collect(Collectors.toList());
//
//		// writes sorted_filtered_selected data to radec file
//		CatalogQuery query = this.result.getQuery();
//		this.fileWriter.writeRaDecFile(selectedTableList, query);
//
//		// reset catalog ui query and sort-filter settings
//		double targetMag = result.getTargetMag();
//		CatalogSettings settings = new CatalogSettings(targetMag);
//
//		// record numbers
//		int nTotalRecords = result.getRecordsTotal();
//		int nFilteredRecords = filteredTableList.size() - 1;
//		
//		settings.setTotalLabelValue(nTotalRecords);
//		settings.setFilteredLabelValue(nFilteredRecords);
//
//		// apply updates
//		catalogDataListener.setQueryData(query);
//		catalogDataListener.setSettingsData(settings);
//
//		// status line
//		String message = fileWriter.getStatusMessage();
//		catalogDataListener.updateStatus(message);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 * 
	 * @return true if catalog table is populated, false if table is clear
	 */
	// TTD replace with void & set button state in catalog settings
	public boolean doImportRaDecFile() {
		// import radec file and map to catalog result object
		QueryResult radecResult = radecFileReader.readRaDecData();

		// user pressed cancel, update status message and exit
		if (radecResult == null) {
			String message = radecFileReader.getStatusMessage();
			catalogDataListener.updateStatus(message);

			// TTD remvoe boolean
			// return true if result is not null, false otherwise
			return (this.result != null);
		}

		// extract radec query from catalog result object
		CatalogQuery radecQuery = radecResult.getQuery();

		// default settings with radec target mag
		double targetMag = radecResult.getTargetMag();
		CatalogSettings settings = new CatalogSettings(targetMag);

		// update catalogui
		catalogDataListener.setQueryData(radecQuery);
		catalogDataListener.setSettingsData(settings);

		// status line
		String message = radecFileReader.getStatusMessage();
		catalogDataListener.updateStatus(message);

		// field values: update result with radec values
		// reset tableRowsList to full radec dataet all rows selected
		this.result = radecResult;
		//this.tableRowsList = result.copyFieldObjects();

		// compile table rows from radec file, default settings, no filters applies
		updateCatalogTable(settings);

		// TTD remove
		return true;
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		// import current catalog ui settings and run update on catalog table data
		CatalogSettings settings = catalogDataListener.getSettingsData();
		updateCatalogTable(settings);

		// status message
		String statusMessage = "Catalog table updated with current sort and filter settings";
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Clears catalog table and resets catalogui settings
	 */
	public void doClearTable() {

		// clear result field
		this.result = null;

		// clear table rows data
		//this.tableRowsList = null;

		// resets settings to default, retains current settings targtMag value
		CatalogSettings settings = catalogDataListener.getSettingsData();
		settings.resetDefaultSettings(null);

		// status line
		String message = "Cleared catalog result table, reset sort and filter settings";
		catalogDataListener.updateStatus(message);

		// clears table with null result
		updateCatalogTable(null);
	}

	// resets settings to default, retains current settings targtMag value

//	// reset sort & filter settings, retains current target mag value
//	private void resetSettings(double targetMag) {
//		CatalogSettings settings = new CatalogSettings(targetMag);
//
//		// TTD get query from result
//		catalogDataListener.setSettingsData(settings);
//	}

	/*
	 * Sorts QueryResult result object records relative to target object. <p>Sort
	 * options are radial distance or difference in magnitude values.</p>
	 * 
	 * @return FieldObject list sorted and filtered as specified by user settings
	 */
	private void updateCatalogTable(CatalogSettings settings) {

		// clears table and exits if result = null
		catalogTableListener.updateTable(null);
		if ((result == null) || (settings == null)) {
			return;
		}

		// this.filteredTableRows = null;

		// get pesky targetMag value & update delta mag records
		double targetMag = settings.getTargetMagSpinnerValue();
		
		result.updateDeltaMags(targetMag);
		List<FieldObject> tableRowsList = result.getFieldObjects();

		// sort by distance option
		if (settings.isDistanceRadioButtonValue() == true) {
			tableRowsList = tableRowsList.stream().sorted(Comparator.comparingDouble(FieldObject::getRadSepAmin))
					.collect(Collectors.toList());
			// sort by delta mag option
		} else if (settings.isDeltaMagRadioButtonValue() == true) {
			tableRowsList = tableRowsList.stream().sorted(Comparator.comparingDouble(p -> Math.abs(p.getDeltaMag())))
					.collect(Collectors.toList());
		}

		// apply nObs limit to reference object records
		int numberObs = settings.getnObsSpinnerValue();

		for (FieldObject fo : tableRowsList) {
			boolean isAccepted = (fo.getnObs() >= numberObs);
			fo.setAccepted(isAccepted);
		}

		// upper and lower mag range settings; 0.01 disables range check
		double upperLimit = settings.getUpperLimitSpinnerValue();
		double lowerLimit = settings.getLowerLimitSpinnerValue();

		// disables respective range check if magnitude is less than 0.01
		boolean disableUpperLimit = Math.abs(upperLimit) < 0.01;
		boolean disableLowerLimit = Math.abs(lowerLimit) < 0.01;

		if (settings.isMagLimitsCheckBoxValue() == true) {
			for (FieldObject fo : tableRowsList) {
				boolean isAccepted = fo.isAccepted();
				isAccepted = isAccepted && (disableUpperLimit || (fo.getMag() <= upperLimit + targetMag));
				isAccepted = isAccepted && (disableLowerLimit || (fo.getMag() >= lowerLimit + targetMag));
				fo.setAccepted(isAccepted);
			}
		}

//		filteredTableRows = filteredTableRows.stream()
//				.filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget()))).collect(Collectors.toList());

//		// apply mag limits filter
//		if (settings.isMagLimitsCheckBoxValue() == true) {
//			double upperLimit = settings.getUpperLimitSpinnerValue();
//			double lowerLimit = settings.getLowerLimitSpinnerValue();
//
//			// apply filter range targetmag + lowerLimit to tagetMg + upperLimit
//			// if lower or upper limit < 0.01 disable respective filter
//			filteredTableRows = filteredTableRows.stream()
//					.filter(p -> (Math.abs(upperLimit) < 0.01 || p.getMag() <= upperLimit + targetMag))
//					.filter(p -> (Math.abs(lowerLimit) < 0.01 || p.getMag() >= lowerLimit + targetMag))
//					.collect(Collectors.toList());
//		}

		// filtered record numbers
		int nTotalRecords = result.getRecordsTotal();
		// TTD int nFilteredRecords = filteredTableRows.size() - 1;

		settings.setTotalLabelValue(nTotalRecords);
		// TTD settings.setFilteredLabelValue(nFilteredRecords);

		// update catalog ui
		catalogDataListener.setSettingsData(settings);

		// run table update with sort / filter selections
		catalogTableListener.updateTable(tableRowsList);

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
