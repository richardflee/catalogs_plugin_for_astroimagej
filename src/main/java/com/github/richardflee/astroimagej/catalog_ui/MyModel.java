package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

class MyModel extends AbstractTableModel implements SimpleListener {
	private static final long serialVersionUID = 1L;
	private Vector<Vector<Object>> rows;
	private String[] headers = { "Box", "Value" };

	public MyModel() {
		rows = new Vector<>();
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	@Override
	public void updateTable(Vector<Vector<Object>> vectors) {
		int lastRow = rows.size();
		while (rows.size() > 0) {
			rows.remove(0);
			fireTableRowsDeleted(0, 0);
		}
		if (vectors != null) {
			for (Vector<Object> v : vectors) {
				rows.add(v);
			}
			fireTableRowsInserted(0, vectors.size());
		}
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Vector<Object> v = rows.get(rowIndex);
		return v.get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			boolean b = (Boolean) getValueAt(rowIndex, columnIndex);
			Vector<Object> v = rows.get(rowIndex);
			b = !b;
			v.set(0, b);
			v.set(1,  b ? "true" : "false");
			fireTableRowsUpdated(rowIndex, rowIndex);
		}

	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true; // (column == 0);
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return (c == 0) ? Boolean.class : String.class;
	}

	public void addItem(int idx, Vector<Object> v) {
		rows.add(v);
		fireTableRowsInserted(idx, idx);
	}

	public void removeItem(int idx) {
		rows.remove(idx);
		fireTableRowsDeleted(idx, idx);
	}

}