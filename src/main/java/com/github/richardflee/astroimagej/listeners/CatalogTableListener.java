package com.github.richardflee.astroimagej.listeners;

import java.util.List;

import com.github.richardflee.astroimagej.query_objects.FieldObject;

/**
 * Interface to handle button click updateTable events 
 *
 */
@FunctionalInterface
public interface CatalogTableListener {
	public void updateTable(List<FieldObject> fieldObjects);

}
