package com.github.richardflee.astroimagej.catalog_ui;

import java.util.List;

import com.github.richardflee.astroimagej.query_objects.FieldObject;

@FunctionalInterface
public interface CatalogTableListener {
	// public void updateTable(Vector<Vector<Object>> vectors);
	public void updateTable(List<FieldObject> fieldObjects);

}
