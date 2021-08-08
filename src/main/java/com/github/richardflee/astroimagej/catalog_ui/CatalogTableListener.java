package com.github.richardflee.astroimagej.catalog_ui;

import java.util.List;

import com.github.richardflee.astroimagej.query_objects.FieldObject;

/**
 * Interface for button click updateTable events 
 *
 */
@FunctionalInterface
public interface CatalogTableListener {
	public void updateTable(List<FieldObject> fieldObjects);

}
