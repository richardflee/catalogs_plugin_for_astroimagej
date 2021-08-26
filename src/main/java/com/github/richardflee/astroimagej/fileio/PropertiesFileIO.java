package com.github.richardflee.astroimagej.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * This class handles property file read and write requests functioning as
 * CatalogQuery and CatalogSettings getters and setters.
 *
 */
public class PropertiesFileIO {
	// full path to properties file, example:
	// C:/Users/[user]/.astroimagej/catalogs_plugin.properties
	private String propertiesFilePath = null;

	public PropertiesFileIO() {
		// set properties path
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		this.propertiesFilePath = Paths.get(homePath, ".astroimagej", "catalogs_plugin.properties").toString();

		// if no properties file, create new file with default query & settings data
		File file = new File(propertiesFilePath);
		if (!file.exists()) {
			setPropertiesFileData(null, null);

			// info new properties file
			String message = String.format("Created a new properties file: \n%s\n", propertiesFilePath);
			JOptionPane.showMessageDialog(null, message);
		}
	}

	/**
	 * Creates a CatalogQuery object from property file.
	 * <p>
	 * Query items identified: "Query." + CatalogsEnum identifier
	 * </p>
	 * 
	 * @return CatalogQuery object encapsulating catalog dialog field values
	 */
	public CatalogQuery getPropertiesQueryData() {

		CatalogQuery query = new CatalogQuery();
		try (InputStream input = new FileInputStream(this.propertiesFilePath)) {
			Properties prop = new Properties();
			prop.load(input);

			// read identified property item and assign query field value
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
			// error dialog
			String message = String.format("Failed to read properties file query data: \n%s\n", propertiesFilePath);
			message += "Loaded default query data";
			JOptionPane.showMessageDialog(null, message);

			// return default settings
			query = new CatalogQuery();
		}
		return query;
	}

	/**
	 * Creates a CatalogSettings object from property file with target mag the only
	 * non-default field.
	 * <p>
	 * Settings items identified: "Settings." + identifier
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @return CatalogQuery object encapsulating catalog dialog field values
	 */
	public CatalogSettings getPropertiesSettingsData() {

		CatalogSettings settings = new CatalogSettings();
		try (InputStream input = new FileInputStream(this.propertiesFilePath)) {
			Properties prop = new Properties();
			prop.load(input);

			// target mag is the only input property
			String targetStr = prop.getProperty(settingsStr());
			settings.setTargetMagSpinnerValue(Double.parseDouble(targetStr));
		} catch (IOException ex) {
			// error dialog
			String message = String.format("Failed to read properties file settings data: \n%s\n", propertiesFilePath);
			message += "Loaded default settings data";
			JOptionPane.showMessageDialog(null, message);

			// return default settings
			settings = new CatalogSettings();
		}
		return settings;
	}

	/**
	 * Writes query and selected settings fields to catalogs_plugin.properties file.
	 * <p>
	 * Query items are identified by CatalalogsEnum values
	 * </p>
	 * <p>
	 * Example data format Query.DEC_DMS=+29\:40\:20.27
	 * </p>
	 * 
	 * @param query    current query parameters imported from catalog UI; default
	 *                 query parameters if null
	 * @param settings current settings parameters imported from catalog UI; default
	 *                 settings parameters if null
	 */
	public String setPropertiesFileData(CatalogQuery query, CatalogSettings settings) {
		// if necessary, sets default query & settings objects
		if (query == null) {
			query = new CatalogQuery();
		}
		if (settings == null) {
			settings = new CatalogSettings();
		}

		String message = String.format("Saved query data in properties file: \n%s", propertiesFilePath);
		;
		try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
			Properties prop = new Properties();

			// record all the query fields
			String objectId = query.getObjectId();
			prop.setProperty(queryStr(QueryEnum.OBJECT_ID), objectId);

			String raHms = AstroCoords.raHr_To_raHms(query.getRaHr());
			prop.setProperty(queryStr(QueryEnum.RA_HMS), raHms);

			String decDms = AstroCoords.decDeg_To_decDms(query.getDecDeg());
			prop.setProperty(queryStr(QueryEnum.DEC_DMS), decDms);

			String fovStr = String.format("%.1f", query.getFovAmin());
			prop.setProperty(queryStr(QueryEnum.FOV_AMIN), fovStr);

			String magLimitStr = String.format("%.1f", query.getMagLimit());
			prop.setProperty(queryStr(QueryEnum.MAG_LIMIT), magLimitStr);

			String catalogStr = query.getCatalogType().toString();
			prop.setProperty(queryStr(QueryEnum.CATALOG_DROPDOWN), catalogStr);

			String filterStr = query.getMagBand();
			prop.setProperty(queryStr(QueryEnum.FILTER_DROPDOWN), filterStr);

			// subset settings field
			prop.setProperty(settingsStr(), String.format("%.3f", settings.getTargetMagSpinnerValue()));

			prop.store(output, null);
		} catch (IOException io) {
			message = String.format("Failed to write query data to properties file: \n%s", propertiesFilePath);
		}
		return message;
	}

	// returns formatted query properties item
	private String queryStr(QueryEnum en) {
		return "Query." + en.toString();
	}

	// returns formatted settings properties item
	private String settingsStr() {
		return "Settings.TARGETMAG";
	}

	// properties filepath gett
	public String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	public static void main(String[] args) {
		PropertiesFileIO pf = new PropertiesFileIO();

		CatalogQuery query = new CatalogQuery();
		query.setObjectId("fred");
		query.setRaHr(1);
		pf.setPropertiesFileData(query, null);
		query = pf.getPropertiesQueryData();

		System.out.println(String.format("Set objectId=fred, read query properties= %s", query.getObjectId()));
		System.out.println(String.format("Set raHr=1,        read query properties= %.6f", query.getRaHr()));
		System.out.println();

		CatalogSettings settings = new CatalogSettings();
		settings.setTargetMagSpinnerValue(9.99);
		pf.setPropertiesFileData(query, settings);

		System.out.println(String.format("Set target mag=9.99,   read settings properties= %.3f",
				settings.getTargetMagSpinnerValue()));
		settings.resetDefaultSettings(8.88);
		System.out.println(String.format("Reset target mag=8.88, read settings properties= %.3f",
				settings.getTargetMagSpinnerValue()));

	}

}
