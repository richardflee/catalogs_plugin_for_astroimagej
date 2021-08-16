package com.github.richardflee.astroimagej.fileio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;
import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.utils.AstroCoords;

public class PropertiesFileIO {
	
	// full path to properties file, example: C:/Users/[user]/.astroimagej/catalogs_plugin.properties
	private String propertiesFilePath = null;
	
	public PropertiesFileIO() {
		// set properties path
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		this.propertiesFilePath = 
				Paths.get(homePath, ".astroimagej", "catalogs_plugin.properties").toString();
		System.out.println((this.propertiesFilePath));
	}

	/**
	 * Class method imports user form settings in catalogs_plugin.properties in
	 * user.home/.astroimagej
	 * 
	 * @return CatalogQuery object encapsulating catalog dialog field values
	 */
	public CatalogQuery getPropertiesFileData() {
		
		CatalogQuery query = new CatalogQuery();
		try (InputStream input = new FileInputStream(this.propertiesFilePath)) {
			Properties prop = new Properties();
			prop.load(input);

			// copy field values to corresponding FormData fields
			// target name
			//String id = queryStr(QueryEnum.OBJECT_ID);
			
			String objectId = prop.getProperty(queryStr(QueryEnum.OBJECT_ID));
			query.setObjectId(objectId);
			
			String raHms = prop.getProperty(queryStr(QueryEnum.RA_HMS));
			query.setRaHr(AstroCoords.raHms_To_raHr(raHms));
			
			String decDms = prop.getProperty(queryStr(QueryEnum.DEC_DMS));
			query.setDecDeg(AstroCoords.decDms_To_decDeg(decDms));
			
			String fovStr = prop.getProperty(queryStr(QueryEnum.FOV_AMIN));
			query.setFovAmin(Double.parseDouble(fovStr));
			
			String magLimitStr = prop.getProperty(queryStr(QueryEnum.MAG_LIMIT));
			query.setMagLimit(Double.parseDouble(magLimitStr));
			
			String catalogStr = prop.getProperty(queryStr(QueryEnum.CATALOG_DROPDOWN));
			query.setCatalogType(CatalogsEnum.valueOf(catalogStr));
			
			String filterStr = prop.getProperty(queryStr(QueryEnum.FILTER_DROPDOWN));
			query.setMagBand(filterStr);

		} catch (IOException ex) {
			String msg = "Failed to read properties file: \n" + PropertiesFileReader.getPropertiesPath();
		}
		return query;
	}

	
	
	public CatalogSettings readPropertiesSettingsData() {
		
		
		return null;
	}
	
	/**
	 * Writes query and selected settings fields to catalogs_plugin.properties file
	 * <p>Example data format  query.filter=V, serrings.targetmag=12.000</p>
	 * 
	 * @param query current query parameters imported from catalog UI; default query parameters if null
	 * @param settings current settings parameters imported from catalog UI; default settings parameters if null
	 */
	public void setPropertiesFileData(CatalogQuery query, CatalogSettings settings) {
		String message = null;
		
		// sets default query & settings objects as necessary
		if (query == null) {
			System.out.println("null query");
			query = new CatalogQuery();
		}
		if (settings == null) {
			System.out.println("null settings");
			settings = new CatalogSettings();
		}
		
		try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
			Properties prop = new Properties();
			
			// record all the query fields
			prop.setProperty(queryStr(QueryEnum.OBJECT_ID), query.getObjectId());
			prop.setProperty(queryStr(QueryEnum.RA_HMS), AstroCoords.raHr_To_raHms(query.getRaHr()));
			prop.setProperty(queryStr(QueryEnum.DEC_DMS), AstroCoords.decDeg_To_decDms(query.getDecDeg()));
			prop.setProperty(queryStr(QueryEnum.FOV_AMIN), String.format("%.1f", query.getFovAmin()));
			prop.setProperty(queryStr(QueryEnum.MAG_LIMIT), String.format("%.1f", query.getMagLimit()));
			prop.setProperty(queryStr(QueryEnum.CATALOG_DROPDOWN), query.getCatalogType().toString());
			prop.setProperty(queryStr(QueryEnum.FILTER_DROPDOWN), query.getMagBand());
			
			// subset settings field
			prop.setProperty(settingsStr(), String.format("%.3f", settings.getTargetMagSpinnerValue()));
			
			prop.store(output, null);
			message = "Saved current dialog settings to properties file: \n" + this.propertiesFilePath;
		} catch (IOException io) {
			message = "Failed to update properties file: \n" + PropertiesFileReader.getPropertiesPath();
		}
	}
	
	private String queryStr(QueryEnum en) {
		return "Query." + en.toString();
	}
	
	private String settingsStr() {
		return "Settings.TARGETMAG";
	}
	
	

	public static void main(String[] args) {
		PropertiesFileIO pf = new PropertiesFileIO();
		
		CatalogQuery query = new CatalogQuery();
		
		query.setObjectId("fred");
		
		query.setRaHr(1);
		
		pf.setPropertiesFileData(query,  null);
		
		query = pf.getPropertiesFileData();
		
		
		String x = QueryEnum.OBJECT_ID.toString();
		
		

	}

}
