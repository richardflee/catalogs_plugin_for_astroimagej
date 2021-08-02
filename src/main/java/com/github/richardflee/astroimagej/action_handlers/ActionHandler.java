package com.github.richardflee.astroimagej.action_handlers;

import java.util.Vector;

import com.github.richardflee.astroimagej.catalog_ui.SimpleListener;
import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.QueryResult;

public class ActionHandler {

	private CatalogQuery query;
	private QueryResult result;
	
	private Vector<Vector<Object>> vectors;

	public ActionHandler() {

	}

	public void doCatalogQuery() {

		System.out.println("catalog query");

		ApassFileReader fr = new ApassFileReader();
		query = new CatalogQuery();
		result = fr.runQueryFromFile(query);
		System.out.println(result.toString());
	}
	
	public void doUpdateTable(SimpleListener aListener) {
		
		System.out.println("update table ");
		aListener.updateTable(result);
	}

}
