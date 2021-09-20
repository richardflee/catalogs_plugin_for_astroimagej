package com.github.richardflee.astroimagej.listeners;

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;

/**
 * Specifies interface to read from and write to catalog ui controls
 *
 */

public interface CatalogDataListener {
	
	// maps CatalogQuery object data to catalog ui query controls
	public void setQueryData(CatalogQuery query);
	
	// maps catalog ui query values to CatalogQuery object
	public CatalogQuery getQueryData();
	
	// maps CatalogSettings data to catalog ui filter and sort controls
	public void setSettingsData(CatalogSettings settings);
	
	// maps catalog ui sort and filter values to CatalogSetings object
	public CatalogSettings getSettingsData();
	
	// maps result of query Simabd on-line database to catalog ui controls
	public void setSimbadData(SimbadResult simbadResult);
	
	// interface to display current status 
	public void updateStatus(String statusMessage);
}
