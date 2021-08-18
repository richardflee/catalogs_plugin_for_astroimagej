package com.github.richardflee.astroimagej.catalog_ui;

import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;

public interface CatalogDataListener {
	
	public void setQueryData(CatalogQuery query);
	
	public CatalogQuery getQueryData();
	
	public void setSettingsData(CatalogSettings settings);
	
	public CatalogSettings getSettingsData();
	
	public void updateStatus(String message, boolean isErrorMessage);
}
