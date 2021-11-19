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
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * This class handles property file read and write requests. <p>Path to
 * prperties file: C:/Users/[user]/.astroimagej/catalogs_plugin.properties</p>
 */
public class PropertiesFileIO {

	private static final String CATALOG_PROPERTIES_FILE = "catalogs_plugin.properties";
	private static final String AIJ_PREFS_FILE = "AIJ_Prefs.txt";
	private static final double DEFAULT_TGT_MAG = 10.0;

	private String propertiesFilePath = null;
	private String aijPrefsFilePath = null;

	public PropertiesFileIO() {
		// set properties path
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		this.propertiesFilePath = Paths.get(homePath, ".astroimagej", CATALOG_PROPERTIES_FILE).toString();

		// AIJ_Prefs.txt
		this.aijPrefsFilePath = Paths.get(homePath, ".astroimagej", AIJ_PREFS_FILE).toString();

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
	 * Creates a CatalogQuery object from property file. <p>CatalogQuery items
	 * identified: "Query." + CatalogsEnum identifier</p>
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
			query.setRaHr(AstroCoords.raHmsToRaHr(raHms));

			String decDms = prop.getProperty(queryStr(QueryEnum.DEC_DMS));
			query.setDecDeg(AstroCoords.decDmsToDecDeg(decDms));

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
	 * Creates a CatalogSettings object from property file; target mag is the only
	 * non-default field. <p>CatalogSettings target mag: "Settings.TARGETMAG=" +
	 * [value]</p>
	 * @return CatalogSetings object encapsulating catalog dialog field values
	 */
	public CatalogSettings getPropertiesSettingsData() {

		CatalogSettings settings = new CatalogSettings();
		try (InputStream input = new FileInputStream(this.propertiesFilePath)) {
			Properties prop = new Properties();
			prop.load(input);

			// target mag
			String settingsProp = prop.getProperty(settingsStr("TARGET"));
			double data = Double.parseDouble(settingsProp);
			settings.setTargetMagSpinnerValue(data);

			// distance sort flag
			settingsProp = prop.getProperty(settingsStr("DISTANCE"));
			boolean flag = Boolean.parseBoolean(settingsProp);
			settings.setDistanceRadioButtonValue(flag);
			settings.setDeltaMagRadioButtonValue(!flag);
			
			// DSS settings flag
			settingsProp = prop.getProperty(settingsStr("DSS"));
			flag = Boolean.parseBoolean(settingsProp);
			settings.setSaveDssCheckBoxValue(flag);
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
	 * <p> Query items are identified by CatalalogsEnum values </p> <p> Example data
	 * format Query.DEC_DMS=+29\:40\:20.27 </p>
	 * @param query
	 *     current query parameters imported from catalog UI; default query
	 *     parameters if null
	 * @param settings
	 *     current settings parameters imported from catalog UI; default settings
	 *     parameters if null
	 */
	public String setPropertiesFileData(CatalogQuery query, CatalogSettings settings) {
		// double null => creating new properties file
		if ((query == null) && (settings == null)) {
			// default query
			query = new CatalogQuery();
			
			// default settings
			settings = new CatalogSettings();
			
			// initialise targetMag, sort and dss options
			settings.setTargetMagSpinnerValue(DEFAULT_TGT_MAG);
			settings.setDistanceRadioButtonValue(true);
			settings.setDeltaMagRadioButtonValue(false);
			settings.setSaveDssCheckBoxValue(true);
		}

		String message = String.format("Saved query data in properties file: \n%s", propertiesFilePath);
		try (OutputStream output = new FileOutputStream(propertiesFilePath)) {
			Properties prop = new Properties();

			// record all the query fields
			String objectId = query.getObjectId();
			prop.setProperty(queryStr(QueryEnum.OBJECT_ID), objectId);

			String raHms = AstroCoords.raHrToRaHms(query.getRaHr());
			prop.setProperty(queryStr(QueryEnum.RA_HMS), raHms);

			String decDms = AstroCoords.decDegToDecDms(query.getDecDeg());
			prop.setProperty(queryStr(QueryEnum.DEC_DMS), decDms);

			String fovStr = String.format("%.1f", query.getFovAmin());
			prop.setProperty(queryStr(QueryEnum.FOV_AMIN), fovStr);

			String magLimitStr = String.format("%.1f", query.getMagLimit());
			prop.setProperty(queryStr(QueryEnum.MAG_LIMIT), magLimitStr);

			String catalogStr = query.getCatalogType().toString();
			prop.setProperty(queryStr(QueryEnum.CATALOG_DROPDOWN), catalogStr);

			String filterStr = query.getMagBand();
			prop.setProperty(queryStr(QueryEnum.FILTER_DROPDOWN), filterStr);

			// settings fields
			String strVal = String.format("%.3f", settings.getTargetMagSpinnerValue());
			prop.setProperty(settingsStr("TARGET"), strVal);

			strVal = String.format("%b", settings.isDistanceRadioButtonValue());
			prop.setProperty(settingsStr("DISTANCE"), strVal);
			
			strVal = String.format("%b", settings.isSaveDssCheckBoxValue());
			prop.setProperty(settingsStr("DSS"), strVal);

			prop.store(output, null);
		} catch (IOException io) {
			message = String.format("Failed to write query data to properties file: \n%s", propertiesFilePath);
		}
		return message;
	}

	public ObservationSite getObservationSiteData() {
		ObservationSite site = null;
		try (InputStream input = new FileInputStream(this.aijPrefsFilePath)) {
			Properties prop = new Properties();
			prop.load(input);

			// import observation site parameters from AIJ_Prefs.txt & create new Site
			// object
			double siteLongDeg = Double.parseDouble(prop.getProperty(".coords.lon").toString());
			double siteLatDeg = Double.parseDouble(prop.getProperty(".coords.lat").toString());
			double siteElevation = Double.parseDouble(prop.getProperty(".coords.alt").toString());
			double utcOffsetHr = Double.parseDouble(prop.getProperty(".coords.nowTimeZoneOffset").toString());

			site = new ObservationSite(siteLongDeg, siteLatDeg, siteElevation, utcOffsetHr);
		} catch (NullPointerException | IOException ex) {
			String message = "Failed to read Observation Site data: \n" + this.aijPrefsFilePath;
			JOptionPane.showMessageDialog(null, message);
		}
		return site;
	}

	// returns formatted query properties item
	private String queryStr(QueryEnum en) {
		return "Query." + en.toString();
	}

	// returns formatted settings properties item
	private String settingsStr(String propType) {
		String strSettings = "Settings.";
		try {
			if (propType == "TARGET") {
				strSettings += "TARGET_MAG";
			} else if (propType == "DISTANCE") {
				strSettings += "DISTANCE_FLAG";
			} else if (propType == "DSS") {
				strSettings += "DSS_FLAG";
			} else {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			String message = String.format("Invlaid properties ref: \n%s\n", propType);
			JOptionPane.showMessageDialog(null, message);
		}
		return strSettings;
	}

	// properties filepath gett
	public String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	public static void main(String[] args) {

		PropertiesFileIO pf = new PropertiesFileIO();

		ObservationSite site = pf.getObservationSiteData();
		System.out.println(site.toString());

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
		// settings.resetDefaultSettings(8.88);
		System.out.println(String.format("Reset target mag=8.88, read settings properties= %.3f",
				settings.getTargetMagSpinnerValue()));

	}

}
