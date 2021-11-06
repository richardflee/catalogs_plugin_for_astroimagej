package com.github.richardflee.astroimagej.catalog_ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.listeners.CatalogTableListener;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Data model for CatalogTable astronomical object table
 * <p>Extends super class AbstractTableModel and implements updateTable method 
 * specified in CatalogTableListener interface.</p>
 */
public class CatalogTableModel extends AbstractTableModel implements CatalogTableListener {
	private static final long serialVersionUID = 1L;

	// table data array
	private List<FieldObject> tableRows;

	// header column names
	final static String headers[] = { "Ap", "ObjectId", "Ra2000", "Dec2000", "Mag", "Mag Err", "Mag diff", "Dist", "Nobs",
			"USE" };
	
	// header tooltips, html formatting
	public static final String[] toolTips = { "<html>Identifies measurement aperture for selected rows</html>",
			"<html>Object identifer<br>APASS format: HHMMSSS±DDMMSS</html>", "<html>Object coordinates</html>",
			"<html>Object coordinates</html>", "<html>Catalog magnitude for selected mag band</html>",
			"<html>Error in catalog magnitude</html>",
			"<html>Delta mag relative to target for selected mag band<br>"
					+ "Optional sort order if user enters a target mag value</html>",
			"<html>Radial distance to target object / arcmin<br>"
					+ "Default sort order with increasing distance</html>",
			"<html>Number of observation sessions<br>" + "(APASS catalog)</html>",
			"<html>USE => copy selected rows to radec file<br>Default = selected</html>" };

	// column numbers 
	private final int USE_COL = ColumnsEnum.USE_COL.getIndex();
	public final int N_COLS = ColumnsEnum.values().length;

	public CatalogTableModel() {
		tableRows = new ArrayList<>();
	}

	
	/**
	 * Overrides CatalogTableListener -> updateTable() method.
	 * <p>Clears table if new data is null,  otherwise updates table with new currentTableRows data</p>
	 * 
	 * @param new data set comprising first row target data then series of reference row data
	 */
	@Override
	public void updateTable(List<FieldObject> currentTableRows) {
		// repeat delete top row (index 0) until table is empty
		int LastRow = tableRows.size();
		while (tableRows.size() > 0) {
			tableRows.remove(0);
		}
		fireTableRowsDeleted(0, LastRow);
		
		// update table with accept = true field objects
		if (currentTableRows != null) {
			int idx = 0;
			for (FieldObject tableRow : currentTableRows) {
				if (tableRow.isAccepted()) {
					addItem(idx, tableRow);
					idx++;
				}
			}
			updateApertureId();
		}
	}

	/**
	 * add row to table at row number idx & update
	 * 
	 * @param idx row number to inset new row 
	 * @param tableRow new row data
	 */
	private void addItem(int idx, FieldObject tableRow) {
		tableRows.add(idx, tableRow);
		fireTableRowsInserted(idx, idx);
	}


	@Override
	public int getRowCount() {
		return tableRows.size();
	}

	@Override
	public int getColumnCount() {
		return N_COLS;
	}

	/**
	 * Returns the value for the cell at rowIndex, columnIndex
	 * <p>Lookup based on ColumnsEnum enum value for that columnIndex</p>
	 * 
	 * @param rowIndex lookup row index
	 * @param columnIndex lookup column index
	 * @return data string or boolean lookup value
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FieldObject objectRow = tableRows.get(rowIndex);
		
		// reverse lookup ColumnsEnum en from columnIndex
		ColumnsEnum en = ColumnsEnum.getEnum(columnIndex);
		
		// match ColumnsEnum en to process data
		Object data = null;
		if (en == ColumnsEnum.AP_COL) {
			data = (String) objectRow.getApertureId();

		} else if (en == ColumnsEnum.OBJECTID_COL) {
			data = (String) objectRow.getObjectId();

		} else if (en == ColumnsEnum.RA2000_COL) {
			data = (String) AstroCoords.raHrToRaHms(objectRow.getRaHr());

		} else if (en == ColumnsEnum.DEC2000_COL) {
			data = (String) AstroCoords.decDegToDecDms(objectRow.getDecDeg());

		} else if (en == ColumnsEnum.MAG_COL) {
			data = (String) String.format("%.3f", objectRow.getMag());

		} else if (en == ColumnsEnum.MAG_ERR_COL) {
			data = (String) String.format("%.3f", objectRow.getMagErr());

		} else if (en == ColumnsEnum.MAG_DIFF_COL) {
			data = (String) String.format("%.3f", objectRow.getDeltaMag());

		} else if (en == ColumnsEnum.DIST_AMIN_COL) {
			data = (String) String.format("%.2f", objectRow.getRadSepAmin());

		} else if (en == ColumnsEnum.NOBS_COL) {
			data = (String) String.format("%2d", objectRow.getnObs());

		} else if (en == ColumnsEnum.USE_COL) {
			data = (Boolean) objectRow.isSelected();
		}
		return data;
	}
	

	/**
	 * Toggles boolean value when user clicks on Use cell checkbox
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == USE_COL) {
			// extract active row, toggle selected flag and update aperture id column
			FieldObject objectRow = tableRows.get(rowIndex);
			boolean isSelected = (Boolean) getValueAt(rowIndex, USE_COL);
			isSelected = !isSelected;
			objectRow.setSelected(isSelected);
			updateApertureId();
		}
	}
	
	/*
	 * Updates aperture id values when user selects or deselects use_col checkbox.
	 * No update to target aperture T01 
	 */
	private void updateApertureId() {
		int counter = 2;
		for (int row = 1; row < tableRows.size(); row++) {
			FieldObject objectRow = tableRows.get(row);
			boolean isSelected = (Boolean) getValueAt(row, USE_COL);

			String apNum = isSelected ? String.format("C%02d", counter++) : "";
			objectRow.setApertureId(apNum);
			objectRow.setSelected(isSelected);
			fireTableRowsUpdated(row, row);
		}
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	/*
	 *  Configure column edit status: only boolean use_col checkbox is editable 
	 *  Target top row is not editable
	 */ 
	@Override
	public boolean isCellEditable(int row, int column) {
		boolean isEditable = (row != 0) && (column == USE_COL);
		return isEditable;
	}

	/*
	 *  Configure column class: string for all columns except use_col = boolean
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		return (column == ColumnsEnum.USE_COL.getIndex()) ? Boolean.class : String.class;
	}
}

