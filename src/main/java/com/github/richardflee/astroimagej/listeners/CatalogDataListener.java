package com.github.richardflee.astroimagej.listeners;

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;

public interface CatalogDataListener {
	
	public void setQueryData(CatalogQuery query);
	
	public CatalogQuery getQueryData();
	
	public void setSettingsData(CatalogSettings settings);
	
	public CatalogSettings getSettingsData();
	
	public void updateStatus(String statusMessage);
	
	public void setSimbadData(SimbadResult simbadResult);
}
