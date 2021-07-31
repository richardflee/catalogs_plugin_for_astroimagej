package com.github.richardflee.astroimagej.catalog_ui;

import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

class MyModel extends AbstractTableModel {

	private Vector<Vector<Object>> rows; 
	private String[] headers = { "Box", "Value" };

	public MyModel() {

		rows = new Vector<>();

		Vector<Object> v = new Vector<>();
		v.add(true);
		v.add("true");
		rows.add(v);

		v = new Vector<>();
		v.add(false);
		v.add("false");
		rows.add(v);
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
			v.set(0, !b);
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

public class SimpleTable  {

	private JTable table;
	private MyModel model;

	public SimpleTable(CatalogFormUI catalogUi) {

		CatalogFormUI mycatalog = catalogUi;
		JScrollPane spane = mycatalog.tableScrollPane;
		
		model = new MyModel();
		table = new JTable(model);
		model.addTableModelListener(e -> doChange(e));
		
		table.setFillsViewportHeight(true);
		spane.setViewportView(table);
	}


	private void doChange(TableModelEvent e) {

		int row = e.getFirstRow();
		int column = e.getColumn();

		if (column == 0) {
			TableModel model = (TableModel) e.getSource();
			Object data = model.getValueAt(row, column);
			model.setValueAt(data, row, column + 1);
		}
	}

	public static void main(String[] args) {

	}

}



//@Override
//public void updateTable(Vector<Vector<Object>> vectors) {
//	for (Vector<Object> v : vectors) {
//		model.addRow(v);
//		System.out.println(v.toString());
//	}
//}

//private JTable createTable() {
//
//	
//
//	// 
//
//	return table;
//}
