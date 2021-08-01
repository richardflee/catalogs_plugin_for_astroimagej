package com.github.richardflee.astroimagej.catalog_ui;


import javax.swing.JScrollPane;
import javax.swing.JTable;

public class SimpleTable {

	private JTable table;
	private MyModel myModel;

	public SimpleTable(CatalogFormUI catalogUi) {

		CatalogFormUI mycatalog = catalogUi;
		JScrollPane spane = mycatalog.tableScrollPane;

		myModel = new MyModel();
		table = new JTable(myModel);

		catalogUi.setSimpleListener(myModel);

		table.setFillsViewportHeight(true);
		spane.setViewportView(table);
	}

	public static void main(String[] args) {

	}

}
