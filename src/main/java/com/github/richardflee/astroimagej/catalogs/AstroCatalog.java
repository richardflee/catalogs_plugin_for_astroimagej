package com.github.richardflee.astroimagej.catalogs;

import java.util.List;

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;

/**
 * Interface implemented by Variable Star Plotter and other catalogs 
 */
public interface AstroCatalog {
	
	public List<FieldObject> runQuery(CatalogQuery query);
	
	public String getStatusMessage();
}
