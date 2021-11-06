package com.github.richardflee.astroimagej._plugin;

import java.awt.EventQueue;
import java.time.LocalDate;

import javax.swing.UIManager;

import com.github.richardflee.astroimagej.catalog_ui.ActionHandler;
import com.github.richardflee.astroimagej.catalog_ui.CatalogTableModel;
import com.github.richardflee.astroimagej.catalog_ui.CatalogUI;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;
import com.github.richardflee.astroimagej.visibility_plotter.Solar;

import ij.IJ;
import ij.plugin.PlugIn;

/**
 * AstroImageJ plugin to import on-line photometry data and save to radec format files.
 * 
 * <p>Note: class name (Catalogs_Plugin) requires underscore  ('_') character to appear 
 * as an option under AIJ Plugins menu.</p>
 * 
 * <p>Refer ImageJ documentation 'Developing Plugins for ImageJ 1.x 
 * https://imagej.net/Developing_Plugins_for_ImageJ_1.x</p>
 */

public class Catalogs_Plugin implements PlugIn {
	
	private final static String VERSION_NUMBER = "ONEJAR-1.00c";

	/**
	 * ImageJ plug-in interface
	 */
	@Override
	public void run(String arg) {
		main(null);
	}

	/**
	 * Runs as a modal Java dialog, invoked from Astroimagej toolbar / Plugins
	 */
	public static void main(String[] args) {
		// sets os 'look-and-feel' 
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			IJ.showMessage("Failed to initialize System Look-Feel");
		}

		// runs app in EDT (event dispatching thread)
		EventQueue.invokeLater(() -> {
			runCatalogsPlugin();
		});

	}

	public static void runCatalogsPlugin() {
		// Handles properties file read/write
		PropertiesFileIO pf = new PropertiesFileIO();
		
		// Swing table data model
		CatalogTableModel ctm = new CatalogTableModel();
		
		// Handles ui button events
		ActionHandler handler = new ActionHandler(pf);
		
		// User  interface
		CatalogUI catalogUi = new CatalogUI(handler, ctm);
		
		// configure listener interfaces
		handler.setCatalogTableListener(ctm);
		handler.setCatalogDataListener(catalogUi);

		// initialise CatalogQuery object with properties data
		// or default data if properties file not found
		CatalogQuery query = pf.getPropertiesQueryData();
		catalogUi.setQueryData(query);
		
		// Coordinates Converter observatory & utc data; null if data error
		ObservationSite site = pf.getObservationSiteData();
		catalogUi.setObservationSiteData(site);
		
		Solar solar = new Solar(site);
		SolarTimes solarTimes = solar.getCivilSunTimes(LocalDate.now());
		catalogUi.setSolarTimes(solarTimes);
		
		// apply properties target mag
		CatalogSettings settings = pf.getPropertiesSettingsData();
		catalogUi.setSettingsData(settings);

		// show JDialog as modal to lock-out AIJ toolbar while vsp_demo open
		catalogUi.setTitle(String.format("Catalogs Plugin %s", VERSION_NUMBER));
		catalogUi.setModal(true);
		catalogUi.setVisible(true);
	}

}
