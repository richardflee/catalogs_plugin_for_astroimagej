package com.github.richardflee.astroimagej.catalog_ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

class CatalogTableModel extends AbstractTableModel implements CatalogTableListener {
	private static final long serialVersionUID = 1L;

	// private Vector<Vector<Object>> rows;
	private List<FieldObject> tableRows;

	final String headers[] = { "Ap", "ObjectId", "Ra2000", "Dec2000", "Mag", "Mag Err", "Mag diff", "Dist", "Nobs",
			"USE" };

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

	// private final int AP_COL = ColumnsEnum.AP_COL.getIndex();
	private final int USE_COL = ColumnsEnum.USE_COL.getIndex();
	public final int N_COLS = ColumnsEnum.values().length;

	public CatalogTableModel() {
		tableRows = new ArrayList<>();
	}

	@Override
	public void updateTable(List<FieldObject> currentTableRows) {

		int LastRow = tableRows.size();
		while (tableRows.size() > 0) {
			tableRows.remove(0);
		}
		fireTableRowsDeleted(0, LastRow);
		// clear table remove rows

		if (currentTableRows != null) {
			int idx = 0;
			for (FieldObject tableRow : currentTableRows) {
				// objectRows.add(objectRow);
				addItem(idx, tableRow);
				fireTableRowsInserted(idx, idx);
				idx++;
			}
			updateApertureId();
		}
	}

	

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	public void addItem(int idx, FieldObject objectRow) {
		tableRows.add(idx, objectRow);
		fireTableRowsInserted(idx, idx);
	}

	public void removeItem(int idx) {
		tableRows.remove(idx);
		fireTableRowsDeleted(idx, idx);
	}

	@Override
	public int getRowCount() {
		return tableRows.size();
	}

	@Override
	public int getColumnCount() {
		return N_COLS;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		FieldObject objectRow = tableRows.get(rowIndex);
		Object data = null;

		if (columnIndex == ColumnsEnum.AP_COL.getIndex()) {
			data = (String) objectRow.getApertureId();

		} else if (columnIndex == ColumnsEnum.OBJECTID_COL.getIndex()) {
			data = (String) objectRow.getObjectId();

		} else if (columnIndex == ColumnsEnum.RA2000_COL.getIndex()) {
			data = (String) AstroCoords.raHr_To_raHms(objectRow.getRaHr());

		} else if (columnIndex == ColumnsEnum.DEC2000_COL.getIndex()) {
			data = (String) AstroCoords.decDeg_To_decDms(objectRow.getDecDeg());

		} else if (columnIndex == ColumnsEnum.MAG_COL.getIndex()) {
			data = (String) String.format("%.3f", objectRow.getMag());

		} else if (columnIndex == ColumnsEnum.MAG_ERR_COL.getIndex()) {
			data = (String) String.format("%.3f", objectRow.getMagErr());

		} else if (columnIndex == ColumnsEnum.MAG_DIFF_COL.getIndex()) {
			data = (String) String.format("%.3f", objectRow.getDeltaMag());

		} else if (columnIndex == ColumnsEnum.DIST_AMIN_COL.getIndex()) {
			data = (String) String.format("%.2f", objectRow.getRadSepAmin());

		} else if (columnIndex == ColumnsEnum.NOBS_COL.getIndex()) {
			data = (String) String.format("%2d", objectRow.getnObs());

		} else if (columnIndex == ColumnsEnum.USE_COL.getIndex()) {
			data = (Boolean) objectRow.isSelected();
		}
		return data;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == USE_COL) {

			FieldObject objectRow = tableRows.get(rowIndex);

			boolean isSelected = (Boolean) getValueAt(rowIndex, USE_COL);
			isSelected = !isSelected;
			objectRow.setSelected(isSelected);
			updateApertureId();
		}
	}
	
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

	@Override
	public boolean isCellEditable(int row, int column) {
		boolean isEditable = (row != 0) && (column == USE_COL);
		return isEditable;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return (column == ColumnsEnum.USE_COL.getIndex()) ? Boolean.class : String.class;
	}
}

//	public void addItem(int idx, Vector<Object> v) {
//		rows.add(v);
//		fireTableRowsInserted(idx, idx);
//	}
//
//	public void removeItem(int idx) {
//		rows.remove(idx);
//		fireTableRowsDeleted(idx, idx);
//	}

//@Override
//public void updateTable(Vector<Vector<Object>> vectors) {
//	// int lastRow = rows.size();
//	while (rows.size() > 0) {
//		rows.remove(0);
//		fireTableRowsDeleted(0, 0);
//	}
//	if (vectors != null) {
//		for (Vector<Object> v : vectors) {
//			rows.add(v);
//		}
//		fireTableRowsInserted(0, vectors.size());
//	}
//}

//@Override
//public int getRowCount() {
//	return rows.size();
//}

//@Override
//public Object getValueAt(int rowIndex, int columnIndex) {
//	Vector<Object> v = rows.get(rowIndex);
//	return v.get(columnIndex);
//}

//@Override
//public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//	if (columnIndex == USE_COL) {
//		boolean isSelected = (Boolean) getValueAt(rowIndex, columnIndex);
//		Vector<Object> v = rows.get(rowIndex);
//		isSelected = !isSelected;
////		v.set(0,  b ? "TRUE" : "FALSE");
//		v.set(USE_COL, isSelected);
//
//		int counter = 2;
//		String ap = "";
//		for (int row = 1; row < rows.size(); row++) {
//			v = rows.get(row);
//			isSelected = (Boolean) getValueAt(row, USE_COL);
//
//			ap = isSelected ? String.format("C%02d", counter++) : "";
//			v.set(AP_COL, ap);
//			fireTableRowsUpdated(row, row);
//		}
//	}
//}