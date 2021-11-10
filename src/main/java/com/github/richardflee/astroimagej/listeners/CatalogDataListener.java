package com.github.richardflee.astroimagej.listeners;

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;

/**
 * Specifies interface to read from and write to catalog ui controls
 *
 */

public interface CatalogDataListener {
	
	// maps CatalogQuery object data  & catalog ui query controls
	public void setQueryData(CatalogQuery query);
	public CatalogQuery getQueryData();
	
	// maps CatalogSettings data & catalog ui filter and sort controls
	public void setSettingsData(CatalogSettings settings);
	public CatalogSettings getSettingsData();
	
	// Coordinates Converter observatory and starting night data
	public void setObservationSiteData(ObservationSite site);
	public void setSolarTimes(SolarTimes solarTimes);
	
	// maps result of query Simabd on-line database to catalog ui controls
	public void setSimbadData(SimbadResult simbadResult);
	
	// interface to display current status 
	public void updateStatus(String statusMessage);
	
}
