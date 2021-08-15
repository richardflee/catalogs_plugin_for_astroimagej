package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;
import com.github.richardflee.astroimagej.data_objects.FieldObject;
import com.github.richardflee.astroimagej.data_objects.QueryResult;
import com.github.richardflee.astroimagej.fileio.ApassFileReader;
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

//TTDO remove
	// private List<FieldObject> sortedFilteredList = null;

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

	// TTDO
	public void doSaveQueryData() {
		System.out.println("Save Query data");
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
		// retain current targt mag vlaue
		CatalogSettings settings = catalogUi.getCatalogUiSortFilterSettings();
		double targtMag = settings.getTargetMagSpinnerValue();
		settings.setDefaultSettings(targtMag);
		catalogUi.setCatalogUiSortFilterSettings(settings);

		// run query
		// TTD replace with online q
		QueryResult currentResult = fr.runQueryFromFile(query);
		// updates field value
		this.result = currentResult;

		// applies selected sort & filtered options to QueryResult object and updates
		// catalog tables
		updateCatalogUiTable(currentResult);
	}

	/**
	 * Writes radec file with selected table data to radec format text file in local
	 * radec astromagej folder
	 * <p>
	 * Example: ./astroimagej/radec/wasp_12.Rc.020.radec.txt
	 * </p>
	 */
	public void doSaveRaDecFile() {
		// filter selected records
		// TTD apply filters to selected records & save
//		// TTDO replace sorted list with sort process
//		List<FieldObject> selectedList = sortedFilteredList.stream().filter(p -> p.isSelected())
//				.collect(Collectors.toList());
//		RaDecFileWriter fw = new RaDecFileWriter();
//		fw.writeRaDecFile(selectedList, query);
	}

	/**
	 * Reads user-selected radec file, maps data to catalog table and ui control and
	 * creates a new query object.
	 */

	public void doImportRaDecFile() {
		System.out.println("import radec");
		RaDecFileReader fr = new RaDecFileReader();

		// exit of Cancel pressed => no file delected
		if (!fr.getFileSelected()) {
			System.out.println("Cancel pressed");
			return;
		}

		// import query and table data
		CatalogQuery query = fr.getQueryData();
		QueryResult currentResult = fr.getTableData();

		// query and filter settings
		// updateCatalogSettings(null, query);

		// table data
		updateCatalogUiTable(currentResult);

		// assign field value
		this.result = currentResult;
	}

	/**
	 * Updates table with current user filter and sort settings
	 */
	public void doUpdateTable() {
		updateCatalogUiTable(result);
	}

	/**
	 * Clears catalog table and resets totals
	 */
	public void doClearTable() {
		// TTDO updateSettings (clear)
		updateCatalogUiTable(null);
	}

	/*
	 * Sorts reference object records relative to target object. Sort options are
	 * radial distance or difference in magnitude values.
	 * 
	 * @return FieldObject list sorted and filtered as specified by user settings
	 */
	private void updateCatalogUiTable(QueryResult result) {
		// clears table
		catalogTableListener.updateTable(null);

		// result = null => reset sortedFilteredList field and exit method
		if (result == null) {
			return;
		}

		// current ui data
		CatalogSettings settings = catalogUi.getCatalogUiSortFilterSettings();

		// field objects listed by sort order and applied filters
		// List<FieldObject> currentSortedFilteredList = null;

		// updates query result object with user-input target mag value
		double targetMag = settings.getTargetMagSpinnerValue();
		result.getTargetObject().setMag(targetMag);

		// sort relative to target
		// sort type from selected radio button: sorts radial distance or absolute
		// difference
		// boolean sortByDistance = catalogUi.distanceRadioButton.isSelected();
		List<FieldObject> sortedFilteredList = result.getSortedList(settings);

		// apply nObs limit
		int numberObs = (int) catalogUi.nObsSpinner.getValue();
		sortedFilteredList = sortedFilteredList.stream()
									.filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget())))
									.collect(Collectors.toList());

		// apply mag limits filter
		if (settings.isMagLimitsCheckBoxValue() == true) {
			double magUpperLimit = settings.getUpperLimitSpinnerValue();
			double magLowerLimit = settings.getLowerLimitSpinnerValue();
			
			sortedFilteredList = sortedFilteredList.stream()
									.filter(p -> (Math.abs(magUpperLimit) < 0.01 || p.getMag() <= magUpperLimit))
									.filter(p -> (Math.abs(magLowerLimit) < 0.01 || p.getMag() >= magLowerLimit))
									.collect(Collectors.toList());
		}
		
		settings = updateLabelValues(settings, sortedFilteredList.size());
		
		catalogUi.setCatalogUiSortFilterSettings(settings);

		// run table update with sort / filter selections
		catalogTableListener.updateTable(sortedFilteredList);
		this.settings = settings;
	}

	private CatalogSettings updateLabelValues(CatalogSettings settings, int nFilteredRecords) {
		CatalogSettings labelSettings = settings;

		labelSettings.setTotalLabelValue(this.result.getRecordsTotal());
		labelSettings.setFilteredLabelValue(nFilteredRecords - 1);
		
		double limitVal = settings.getUpperLimitSpinnerValue();
		String limitStr = (Math.abs(limitVal) < 0.01) ? "N/A" : String.format("%.1f", limitVal);
		labelSettings.setUpperLabelValue(limitStr);
		
		limitVal = settings.getLowerLimitSpinnerValue();
		limitStr = (Math.abs(limitVal) < 0.01) ? "N/A" : String.format("%.1f", limitVal);
		labelSettings.setLowerLabelValue(limitStr);
		
		return labelSettings;
	}

//	private Double[] getMagLimitValues(CatalogSettings settings) {
//		Double[] limitValues = new Double[2];
//
//		double magUpperLimit = settings.getUpperLimitSpinnerValue();
//		double targetMag = settings.getTargetMagSpinnerValue();
//		double magLowerLimit = settings.getLowerLimitSpinnerValue();
//
//		if (Math.abs(magUpperLimit) < 0.01) {
//			limitValues[0] = "N/A";
//		} else {
//			limitValues[0] = String.format("%.1f", targetMag + magUpperLimit);
//		}
//
//		if (Math.abs(magLowerLimit) < 0.01) {
//			limitValues[1] = "N/A";
//		} else {
//			limitValues[1] = String.format("%.1f", targetMag + magLowerLimit);
//		}
//		return limitValues;
//	}

//	/*
//	 * Import catalogUI sort and filter control values
//	 */
//	private void importCatalogSettings() {
//		// values of nominal mag, upper and lower limits and state of apply filter
//		// checkbox
//		targetMag = Double.valueOf(catalogUi.targetMagSpinner.getValue().toString());
//		magUpperLimit = Double.valueOf(catalogUi.upperLimitSpinner.getValue().toString());
//		magLowerLimit = Double.valueOf(catalogUi.lowerLimitSpinner.getValue().toString());
//		isMagLimitsSelected = Boolean.valueOf(catalogUi.isMagLimitsCheckBox.isSelected());
//
//		// sort option, mutually exclusive
//		isDistanceSelected = Boolean.valueOf((Boolean) catalogUi.distanceRadioButton.isSelected());
//		isDeltaMagSelected = Boolean.valueOf((Boolean) catalogUi.deltaMagRadioButton.isSelected());
//
//		// number of observations spin control, relevant to apass catalog only
//		numberObs = Integer.valueOf(catalogUi.nObsSpinner.getValue().toString());
//	}

//	private void updateCatalogSettings(FieldObject target, CatalogQuery query) {
//		// catalog query settings
//		catalogUi.objectIdField.setText(query.getObjectId());
//		catalogUi.raField.setText(AstroCoords.raHr_To_raHms(query.getRaHr()));
//		catalogUi.decField.setText(AstroCoords.decDeg_To_decDms(query.getDecDeg()));
//		catalogUi.fovField.setText(String.format("%.1f", query.getFovAmin()));
//		catalogUi.magLimitField.setText(String.format("%.1f", query.getMagLimit()));
//		catalogUi.catalogCombo.setSelectedItem(query.getCatalogType().toString());
//		catalogUi.filterCombo.setSelectedItem(query.getMagBand());
//	}

//	private void initialiseCatalogSortFilerSettings(FieldObject target) {
//		// mag limits
//		catalogUi.targetMagSpinner.setValue(target.getMag());
//		catalogUi.isMagLimitsCheckBox.setSelected(false);
//		catalogUi.nObsSpinner.setValue(1);
//
//	}

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
