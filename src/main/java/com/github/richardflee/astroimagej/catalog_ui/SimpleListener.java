package com.github.richardflee.astroimagej.catalog_ui;

import com.github.richardflee.astroimagej.query_objects.QueryResult;

@FunctionalInterface
public interface SimpleListener {
	// public void updateTable(Vector<Vector<Object>> vectors);
	public void updateTable(QueryResult result);

}
