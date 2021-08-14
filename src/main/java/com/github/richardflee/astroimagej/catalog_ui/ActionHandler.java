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
	private List<FieldObject> sortedFilteredList = null;

	/**
	 * Parameterised constructor references CatalogUI to access form control values
	 * 
	 * @param catalogUi reference to main user form interface
	 */
	public ActionHandler(CatalogUI catalogUi) {
		this.catalogUi = catalogUi;
		
		// TTDO load setting from props file, otherwise new
		this.settings = new CatalogSettings();
		//this.settings = catalogUi.importCatalogUiSortFilter();

		// TTD replace with cat query build method
		//query = new CatalogQuery();
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
		QueryResult currentResult = fr.runQueryFromFile(query);

		// applies selected sort & filtered options to QueryResult object and updates
		// catalog tables
		updateCatalogUiTable(currentResult);

		// update field value
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
		// filter selected records
		//TTDO replace sorted list with sort process
		List<FieldObject> selectedList = sortedFilteredList.stream().filter(p -> p.isSelected())
				.collect(Collectors.toList());
		RaDecFileWriter fw = new RaDecFileWriter();
		fw.writeRaDecFile(selectedList, query);
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
		//updateCatalogSettings(null, query);

		// table data
		updateCatalogUiTable(currentResult);
		
		// assign field value
		// this.result = currentResult;
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
			this.sortedFilteredList = null;
			return;
		}

		// current ui data
		settings = catalogUi.importCatalogUiSortFilter();
		
		// field objects listed by sort order and applied filters
		List<FieldObject> currentSortedFilteredList = null;

		// updates query result object with user-input target mag value
		double targetMag = settings.getTargetMagSpinnerValue();
		result.getTargetObject().setMag(targetMag);
		
		

		// sort relative to target
		// sort type from selected radio button: sorts radial distance or absolute
		// difference
		// boolean sortByDistance = catalogUi.
		// List<FieldObject> sortedList = ApplyTableSort(sortByDistance);

		// apply nObs limit
//		currentSortedFilteredList = sortedList.stream().filter(p -> ((p.getnObs() >= numberObs) || (p.isTarget())))
//				.collect(Collectors.toList());

		// option to apply mag difference filters
		// if upper and/or lower limits < 0.1 (effectively 0) then limits are not
		// applied (N/A)
		double magUpperLimit = settings.getUpperLimitSpinnerValue();
		double magLowerLimit = settings.getLowerLimitSpinnerValue();
		
		if (settings.isMagLimitsCheckBoxValue() == true) {
			final double upperLimit =
					((Math.abs(magUpperLimit) < 0.01) ? 100.0 : targetMag + magUpperLimit);
			String limit = (Math.abs(magUpperLimit) < 0.01) ? "N/A" : String.format("%.1f", upperLimit);
			catalogUi.upperLabel.setText(limit);

			final double lowerLimit = ((Math.abs(magLowerLimit) < 0.01) ? -100.0 : targetMag + magLowerLimit);
			limit = (Math.abs(magLowerLimit) < 0.01) ? "N/A" : String.format("%.1f", lowerLimit);
			catalogUi.lowerLabel.setText(limit);

			// apply mag upper & lower mag limits
			currentSortedFilteredList = currentSortedFilteredList.stream().filter(p -> (p.getMag() >= lowerLimit))
					.filter(p -> p.getMag() <= upperLimit).collect(Collectors.toList());
		}

		// update number of filtered records, clip to 0 if count is negative
		int filteredRecords = currentSortedFilteredList.size() - 1;
		filteredRecords = (filteredRecords > 0) ? filteredRecords : 0;
		catalogUi.filteredLabel.setText(String.format("%3d", filteredRecords));

		// run table update with sort / filter selections
		catalogTableListener.updateTable(currentSortedFilteredList);

		this.sortedFilteredList = currentSortedFilteredList;
	}

	/*
	 * Sorts FieldObject array by user sort selection Distanc eor |Delta Ma|.
	 * 
	 * @param sortByDistance true if Distance radio button selected, false if |Delta
	 *                       Mag| selected
	 * @return FieldObject array sorted by selection type
	 */
	private List<FieldObject> applyTableSort(QueryResult result, CatalogSettings settings) {
		
		boolean sortByDistance = settings.isDistanceRadioButtonValue();
		double targetMag = settings.getTargetMagSpinnerValue();
		
		List<FieldObject> sortedList = null;
		if (sortByDistance) {
			sortedList = result.sortByDistance(targetMag);
		} else {
			sortedList = result.sortByDeltaMag(targetMag);
		}
		return sortedList;
	}

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
