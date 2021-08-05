package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;

public class ActionHandler {

	private CatalogFormUI catalogUi;
	private CatalogQuery query;
	private QueryResult result;

	private double targetMag;
	private double magUpperLimit;
	private double magLowerLimit;

	private int numberObs;
	private int totalRecords;
	private int filteredRecords;

	private boolean isMagLimitsSelected;
	private boolean isRadSepSelected;
	private boolean isDeltaMagSelected;

	public ActionHandler(CatalogFormUI catalogUi) {
		this.catalogUi = catalogUi;
		updateCatalogUiSettings();
	}

	private void updateCatalogUiSettings() {

		System.out.println(String.format("do mag limits check: %b", catalogUi.isMagLimitsCheckBox.isSelected()));

		String strVal = catalogUi.magSpinner.getValue().toString();

		targetMag = Double.valueOf(catalogUi.magSpinner.getValue().toString());
		magUpperLimit = Double.valueOf(catalogUi.upperLimitSpinner.getValue().toString());

		magLowerLimit = Double.valueOf(catalogUi.lowerLimitSpinner.getValue().toString());
		isMagLimitsSelected = Boolean.valueOf(catalogUi.isMagLimitsCheckBox.isSelected());

		// totalRecords = Integer.valueOf(catalogUi.totalLabel.getText().toString());
		// filteredRecords =
		// Integer.valueOf(catalogUi.filteredLabel.getText().toString());

		isRadSepSelected = Boolean.valueOf((Boolean) catalogUi.radSepRadioButton.isSelected());
		isDeltaMagSelected = Boolean.valueOf((Boolean) catalogUi.deltaMagRadioButton.isSelected());

		numberObs = Integer.valueOf(catalogUi.nObsSpinner.getValue().toString());

	}

	public void doCatalogQuery(SimpleListener aListener) {

		System.out.println("catalog query");

		ApassFileReader fr = new ApassFileReader();
		query = new CatalogQuery();
		result = fr.runQueryFromFile(query);

		int totalRecords = result.getTotalRecords();
		totalRecords = (totalRecords > 0) ? totalRecords : 0;
		catalogUi.totalLabel.setText(String.format("%3d", totalRecords));

		updateCatalogTable(aListener);
	}

	public void doUpdateTable(SimpleListener aListener) {
		updateCatalogTable(aListener);
	}

	private void updateCatalogTable(SimpleListener aListener) {
		updateCatalogUiSettings();
		result.getTargetObject().setMag(targetMag);
		System.out.println(String.format("Updated target mag = %.3f", result.getTargetObject().getMag()));

		// sort
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

		if (isMagLimitsSelected) {

			// apply target mag limits
			if (Math.abs(magUpperLimit) < 0.01) {
				catalogUi.upperLabel.setText(String.format("%s", "N/A"));
			} else {

			}

			final double upperLimit = ((Math.abs(magUpperLimit) < 0.01) ? 100.0 : targetMag + magUpperLimit);
			String upperText = (Math.abs(magUpperLimit) < 0.01) ? "N/A" : String.format("%.1f", upperLimit);
			catalogUi.upperLabel.setText(upperText);

			final double lowerLimit = ((Math.abs(magLowerLimit) < 0.01) ? -100.0 : targetMag + magLowerLimit);
			String lowerText = (Math.abs(magLowerLimit) < 0.01) ? "N/A" : String.format("%.1f", lowerLimit);
			catalogUi.lowerLabel.setText(lowerText);
			
			

			System.out.println(String.format("mag range= %.3f,  %.3f", upperLimit, lowerLimit));

			sortedFilteredList = sortedFilteredList.stream().filter(p -> (p.getMag() >= lowerLimit))
					.filter(p -> p.getMag() <= upperLimit).collect(Collectors.toList());
		}

		int filteredRecords = sortedFilteredList.size() - 1;
		filteredRecords = (filteredRecords > 0) ? filteredRecords : 0;
		catalogUi.filteredLabel.setText(String.format("%3d", filteredRecords));
		aListener.updateTable(sortedFilteredList);

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

		System.out.println("there");

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

//		System.out.println(String.format("mag spinner: %.1f", catalogUi.magSpinner.getValue()));
//		
//		System.out.println(String.format("upper limit spinner: %.1f", catalogUi.upperLimitSpinner.getValue()));
//		
//		System.out.println(String.format("lower limit spinner: %.1f", catalogUi.lowerLimitSpinner.getValue()));
//		
//		System.out.println(String.format("total records label: %s", catalogUi.totalRecordsLabel.getText()));
//		
//		System.out.println(String.format("filtered records label: %s", catalogUi.filteredRecordsLabel.getText()));
//		
//		System.out.println(String.format("rad sep radio button: %b", catalogUi.radSepRadioButton.isSelected()));
//		
//		System.out.println(String.format("delta mag radio buttonr: %b", catalogUi.deltaMagRadioButton.isSelected()));
//		
//		System.out.println(String.format("nobs spinner: %02d", catalogUi.nObsSpinner.getValue()));