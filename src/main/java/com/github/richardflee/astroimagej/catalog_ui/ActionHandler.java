package com.github.richardflee.astroimagej.catalog_ui;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import com.github.richardflee.astroimagej.catalogs.AstroCatalog;
import com.github.richardflee.astroimagej.catalogs.CatalogFactory;
import com.github.richardflee.astroimagej.catalogs.SimbadCatalog;
import com.github.richardflee.astroimagej.catalogs.VspCatalog;
import com.github.richardflee.astroimagej.exceptions.SimbadNotFoundException;
import com.github.richardflee.astroimagej.fileio.DssFitsWriter;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.fileio.RaDecFileReader;
import com.github.richardflee.astroimagej.fileio.RaDecFileWriter;
import com.github.richardflee.astroimagej.listeners.CatalogDataListener;
import com.github.richardflee.astroimagej.listeners.CatalogTableListener;
import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.visibility_plotter.Solar;
import com.github.richardflee.astroimagej.visibility_plotter.VisibilityPlotter;

/**
 * This class handles catalog_ui button click events to run on-lines database queries,
 * update table data and radec file read / write operations
 */
public class ActionHandler {
	// reference to CatalogUI, main user form
	private CatalogTableListener catalogTableListener;
	private CatalogDataListener catalogDataListener;

	/*
	 * result field: object compiled from database query parameters and query response data
	 *  or imported from radec file
	 */
	private QueryResult result = null;
	
	// references to catalog database query and result objects
	private PropertiesFileIO propertiesFile = null;
	private RaDecFileReader radecFileReader = null;
	private RaDecFileWriter fileWriter = null;

	// star chart with selected aperture overlay
	private VspChart vspChart = null;
	
		
	// visibility plot
	private ObservationSite site = null;
	private Solar solar = null;
	
	// observation date
	private LocalDate startingNight = null;


	private final String QUERY_SETTINGS_ERROR = "ERROR: Invalid input in Catalog Query settings text field";

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * @param catalogTableListener
	 *     reference to main user form interface
	 */
	public ActionHandler(PropertiesFileIO propertiesFile) {
		this.propertiesFile = propertiesFile;
		
		// file reader and writer objects
		this.fileWriter = new RaDecFileWriter();
		this.radecFileReader = new RaDecFileReader();
		
		this.site = propertiesFile.getObservationSiteData();
		this.solar = new Solar(site);
		
		// creates vsp star chart with aperture overlay
		this.vspChart = new VspChart();
		
		// initialise starting night to today
		this.startingNight = LocalDate.now();
	}

	/**
	 * Configures local table listener field to broadcast updateTable message
	 * @param catalogTableListener
	 *     reference to CataTableListener interface
	 */
	public void setCatalogTableListener(CatalogTableListener catalogTableListener) {
		this.catalogTableListener = catalogTableListener;
	}
	
	/**
	 * Configures local catalog listener field to broadcast query & settings update messages
	 * 
	 * @param catalogDataListener reference to CatalogDataListener interface
	 */
	public void setCatalogDataListener(CatalogDataListener catalogDataListener) {
		this.catalogDataListener = catalogDataListener;
	}

	/**
	 * Runs an objectId-based query on Simbad on-line database. 
	 * 
	 * <p>Updates Simbad fields with search results or with "." if no match was found</p>
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
	 * Runs a query on an on-line astronomical database with atalogQuery object parameters.
	 * Outputs result records in the catalog table.
	 */
	public void doCatalogQuery() {
		// compile CatalogQuery object from catalog ui Query Settings data
		CatalogQuery query = catalogDataListener.getQueryData();
		if (query == null) {
			catalogDataListener.updateStatus(QUERY_SETTINGS_ERROR);
			return;
		}

		// default settings with catalog ui target mag
		// assemble catalog result with default settings & targe mag
		double targetMag = catalogDataListener.getSettingsData().getTargetMagSpinnerValue();
		this.result = new QueryResult(query, new CatalogSettings(targetMag));

		// runs query on selected on-line catalog, retruns list of field objects
		// append this list to CatalogResut object
		AstroCatalog catalog = CatalogFactory.createCatalog(query.getCatalogType());
		List<FieldObject> fieldObjects = catalog.runQuery(query);
		
		if (fieldObjects != null) {
			result.appendFieldObjects(fieldObjects);
			
			// downloads VSP chart for current query parameters
			// Example chart X26835JN: 
			// wasp12 / 06:30:32.80 / 29:40:20.3 / 10' fov / maglimit = 18.5 / N- E = up-left
			VspCatalog vspChart = new VspCatalog();
			String chartUri = vspChart.downloadVspChart(query);
			result.setChartUri(chartUri);
	
			// applies current sort and default filter settings, populates catalog table
			// with full dataset
			updateCatalogTable(this.result);
	
			// set catalog ui default settings, retaining current target spinner value
			catalogDataListener.setSettingsData(result.getSettings());
	
			// draws new chart, closing any chart that is already open
			this.vspChart.showChart(result);
		}

		// status message
		String statusMessage = catalog.getStatusMessage();
		catalogDataListener.updateStatus(statusMessage);
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astroimagej folder 
	 * 
	 * <p> Example: ./astroimagej/radec/wasp_12.Rc.020.radec.txt </p>
	 */
	public void doSaveRaDecFile() {
		// writes radec with accepted (i.e. meet user filter specs) and selected records
		this.fileWriter.writeRaDecFile(result);
		String message = fileWriter.getStatusMessage();

		// reverts catalog ui to query & settings used for on-line query
		catalogDataListener.setQueryData(result.getQuery());
		catalogDataListener.setSettingsData(result.getSettings());
		
		// option to download dss fits file from SkyView server
		String dssMessage = "DSS Fits file option not selected";
		if (catalogDataListener.getSettingsData().isSaveDssCheckBoxValue() == true) {
			dssMessage = DssFitsWriter.downloadDssFits(result.getQuery());
		} 

		// status line
		catalogDataListener.updateStatus(message + "; " + dssMessage);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 */
	public void doImportRaDecFile() {
		// import radec file and map to catalog result object
		QueryResult radecResult = radecFileReader.importRaDecResult();

		// user pressed cancel, update status message and exit
		if (radecResult == null) {
			String message = radecFileReader.getStatusMessage();
			catalogDataListener.updateStatus(message);
			return;
		}

		// references new QueryResult object
		this.result = radecResult;

		// compile table rows from radec file, default settings, no filters applied
		updateCatalogTable(this.result);

		// update catalogui
		catalogDataListener.setQueryData(this.result.getQuery());
		catalogDataListener.setSettingsData(this.result.getSettings());

		// closes vsp chart if necessary and draws new chart
		this.vspChart.showChart(result);

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

		// closes any open chart and draws current chart
		this.vspChart.showChart(result);

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
		this.vspChart.closeChart();

		// status line
		String statusMessage = "Cleared catalog result table, reset sort and filter settings";
		catalogDataListener.updateStatus(statusMessage);
	}
	
	
	public void doPlotVisibility() {
		CatalogQuery query = catalogDataListener.getQueryData();
		if (query == null) {
			catalogDataListener.updateStatus(QUERY_SETTINGS_ERROR);
			return;
		}
		QueryResult res = new QueryResult(query, null);		
		BaseFieldObject targetObject = res.getTargetObject();
		
//		this.visplot.showChart(targetObject, startingNight);
//		
		VisibilityPlotter visPlotter = new VisibilityPlotter(this.site);
		visPlotter.plotVisiblityCharts(targetObject, startingNight);
		

//		VisibilityPlotter visPlotter = new VisibilityPlotter(site);
//		visPlotter.plotVisiblityCharts(fo, startingNight);
		
	}
	
	
	public void doNewDate(LocalDate currentDate) {
		if (currentDate != null) {
			this.startingNight = currentDate;
			SolarTimes solarTimes = solar.getCivilSunTimes(currentDate);
			catalogDataListener.setSolarTimes(solarTimes);	
		}
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
				String.format("Default decDms +29:40:20.27: %s", AstroCoords.decDegToDecDms(q0.getDecDeg())));

		System.out.println(String.format("\nModified query object freddy: %s", q1.getObjectId()));
		System.out.println(String.format("Modified query decDeg -23.456: %.3f", q1.getDecDeg()));
		System.out.println(String.format("Modified settings targetMag 7.89: %.2f", s1.getTargetMagSpinnerValue()));
	}
}
