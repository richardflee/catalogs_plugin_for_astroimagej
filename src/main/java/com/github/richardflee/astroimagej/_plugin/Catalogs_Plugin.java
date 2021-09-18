package com.github.richardflee.astroimagej._plugin;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.richardflee.astroimagej.catalog_ui.ActionHandler;
import com.github.richardflee.astroimagej.catalog_ui.CatalogTableModel;
import com.github.richardflee.astroimagej.catalog_ui.CatalogUI;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;

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
		
		// apply properties target mag
		CatalogSettings settings = pf.getPropertiesSettingsData();
		catalogUi.setSettingsData(settings);

		// show JDialog as modal to lock-out AIJ toolbar while vsp_demo open
		catalogUi.setModal(true);
		catalogUi.setVisible(true);
	}

}
