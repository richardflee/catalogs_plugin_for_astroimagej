package com.github.richardflee.astroimagej.catalog_ui;

import java.io.File;
import java.util.List;

import com.github.richardflee.astroimagej.catalogs.SimbadCatalog;
import com.github.richardflee.astroimagej.exceptions.SimbadNotFoundException;
import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.fileio.RaDecFileReader;
import com.github.richardflee.astroimagej.fileio.RaDecFileWriter;
import com.github.richardflee.astroimagej.listeners.CatalogDataListener;
import com.github.richardflee.astroimagej.listeners.CatalogTableListener;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * This class handles catalog_ui button click events to command database query,
 * update tale data and radec file read / write operations
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

	// star chart with selected aperture overlay
	private VspChart chart = null;

	/*
	 * result field: object compiled from database query records or imported from
	 * radec file. Each field object tracks its filtered and selected state
	 * doCatalogQuery & doImportRaDec: create new result doSaveRadec saves selected
	 * selected results field objects to radec format file options doClear resets
	 * result null
	 */
	private QueryResult result = null;

	private final String QUERY_SETTINGS_ERROR = "ERROR: Invalid input in Catalog Query settings text field";

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * @param catalogTableListener
	 *     reference to main user form interface
	 */
	public ActionHandler(PropertiesFileIO propertiesFile) {
		this.propertiesFile = propertiesFile;
		this.fileWriter = new RaDecFileWriter();
		this.radecFileReader = new RaDecFileReader();
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * @param catalogTableListener
	 *     reference to CataTableListner interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.catalogTableListener = catalogTableListener;
	}

	public void setCatalogDataListener(CatalogDataListener catalogDataListener) {
		this.catalogDataListener = catalogDataListener;
	}

	/**
	 * Runs an objectId-based query on Simbad on-line database. <p>Updates Simbad
	 * fields with search results or with "." if no match was found</p>
	 */
	public void doSimbadQuery() {
		SimbadCatalog simbad = new SimbadCatalog();
		SimbadResult simbadResult = null;

		CatalogQuery query = catalogDataListener.getQueryData();
		if (query == null) {
			catalogDataListener.updateStatus(QUERY_SETTINGS_ERROR);
			return;
		}

		// run simbad query, raises EimbadNotFound exception if no match to user input
		// objectId
		try {
			simbadResult = simbad.runQuery(query);
		} catch (SimbadNotFoundException se) {
			simbadResult = null;
		}

		// update catalog ui with simbad results
		// "." in simbad fields indicates no match found
		catalogDataListener.setSimbadData(simbadResult);

		String statusMessage = simbad.getStatusMessage();
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Imports and writes to properties file current catalog Ui query parameters,
	 * plus a subset of settings parameters
	 */
	public void doSaveQuerySettingsData() {
		CatalogQuery query = catalogDataListener.getQueryData();
		if (query == null) {
			catalogDataListener.updateStatus(QUERY_SETTINGS_ERROR);
			return;
		}

		// import catalog ui target mag and save
		CatalogSettings settings = catalogDataListener.getSettingsData();

		String statusMessage = propertiesFile.setPropertiesFileData(query, settings);
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Runs a query on on-line astronomical database with query object parameters.
	 * Outputs result records in the catalog table.
	 */
	// TTDO replace apass file read with on-line q
	public void doCatalogQuery() {
		// compile CatalogQuery object from catalog ui Query Settings data
		CatalogQuery query = catalogDataListener.getQueryData();
		if (query == null) {
			catalogDataListener.updateStatus(QUERY_SETTINGS_ERROR);
			return;
		}

		// default settings with catalog ui target mag
		double targetMag = catalogDataListener.getSettingsData().getTargetMagSpinnerValue();

		// assemble catalog result with default settings & targe mag
		this.result = new QueryResult(query, new CatalogSettings(targetMag));

		// run query
		// TTD replace with online query
		ApassFileReader fr = new ApassFileReader();
		List<FieldObject> referenceObjects = fr.runQueryFromFile(query);
		result.appendFieldObjects(referenceObjects);

		// chart X26835JN: wasp12 / 06:30:32.80 / 29:40:20.3 / 10' fov / maglimit = 18.5
		// / N- E = up-left
		result.setChartUri("https://app.aavso.org/vsp/chart/X26835JN.png?type=chart");

		// applies current sort and default filter settings, populates catalog table
		// with full dataset
		updateCatalogTable(this.result);

		// update catalog ui with default settings, retains current target spinner value
		catalogDataListener.setSettingsData(result.getSettings());

		// closes vsp chart if necessary and draws new chart
		if (chart != null) {
			chart.closeChart();
		}
		chart = new VspChart(this.result);

		// status message
		String statusMessage = "Imported full dataset, sorted by radial distance to target position";
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astroimagej folder <p> Example:
	 * ./astroimagej/radec/wasp_12.Rc.020.radec.txt </p>
	 */
	public void doSaveRaDecFile() {
		// filter user selected records from accepted field objects
		// List<FieldObject> selectedTableList = this.result.getSelectedRecords();

		// writes radec
		this.fileWriter.writeRaDecFile(result);

		// reverts catalog ui to query & settings used to get table data
		catalogDataListener.setQueryData(result.getQuery());
		catalogDataListener.setSettingsData(result.getSettings());

		// status line
		String message = fileWriter.getStatusMessage();
		catalogDataListener.updateStatus(message);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 */
	// TTD replace with void & set button state in catalog settings
	public void doImportRaDecFile() {
		// import radec file and map to catalog result object
		QueryResult radecResult = radecFileReader.importRaDecResult();

		// user pressed cancel, update status message and exit
		if (radecResult == null) {
			String message = radecFileReader.getStatusMessage();
			catalogDataListener.updateStatus(message);
			return;
		}

		// reference new CatalogResult object
		this.result = radecResult;

		// compile table rows from radec file, default settings, no filters applied
		updateCatalogTable(this.result);

		// update catalogui
		catalogDataListener.setQueryData(this.result.getQuery());
		catalogDataListener.setSettingsData(this.result.getSettings());

		// closes vsp chart if necessary and draws new chart
		if (chart != null) {
			chart.closeChart();
		}
		chart = new VspChart(this.result);

		// status line
		String statusMessage = radecFileReader.getStatusMessage();
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		// import current sort & filter settings
		CatalogSettings currentSettings = catalogDataListener.getSettingsData();
		this.result.setSettings(currentSettings);

		// update catalog table with current settings
		updateCatalogTable(this.result);

		// update catalog ui totals, filter sort settings are unchanged
		catalogDataListener.setSettingsData(this.result.getSettings());

		if (chart != null) {
			chart.closeChart();
		}
		// chart = new VspChart(this.result);
		chart.drawChart(result.getFieldObjects());

		// status message
		String statusMessage = "Catalog table updated with current sort and filter settings";
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Clears catalog table and resets catalogui settings
	 */
	public void doClearTable() {
		// current target mag
		double targetMag = result.getTargetObject().getMag();

		// clear result field
		this.result = null;

		// clears table with null result
		updateCatalogTable(null);

		// resets settings to default, retains current spincontrol targtMag value
		catalogDataListener.setSettingsData(new CatalogSettings(targetMag));

		// close & dispose current vsp chart
		chart.closeChart();

		// status line
		String statusMessage = "Cleared catalog result table, reset sort and filter settings";
		catalogDataListener.updateStatus(statusMessage);
	}

	/*
	 * Sorts QueryResult result object records relative to target object. <p>Sort
	 * options are radial distance or difference in magnitude values.</p>
	 * @return FieldObject list sorted and filtered as specified by user settings
	 */
	private void updateCatalogTable(QueryResult result) {
		// null result: sets default settings & clears catalog table
		if (result == null) {
			catalogDataListener.setSettingsData(new CatalogSettings());
			catalogTableListener.updateTable(null);
			return;
		}

		// apply sort & filter settings to dataset
		result.applySelectedSort();
		result.applySelectedFilters();

		// run table update with sort / filter selections
		catalogTableListener.updateTable(result.getFieldObjects());
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
		// s0.resetDefaultSettings(7.89);
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
