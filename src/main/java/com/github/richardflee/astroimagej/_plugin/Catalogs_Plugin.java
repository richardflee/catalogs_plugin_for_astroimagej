package com.github.richardflee.astroimagej._plugin;

import java.awt.EventQueue;

import javax.swing.UIManager;

import com.github.richardflee.astroimagej.catalog_ui.ActionHandler;
import com.github.richardflee.astroimagej.catalog_ui.CatalogTableModel;
import com.github.richardflee.astroimagej.catalog_ui.CatalogUI;
import com.github.richardflee.astroimagej.data_objects.CatalogQuery;
import com.github.richardflee.astroimagej.data_objects.CatalogSettings;
import com.github.richardflee.astroimagej.fileio.PropertiesFileIO;

import ij.IJ;
import ij.plugin.PlugIn;

public class Catalogs_Plugin implements PlugIn {

	@Override
	public void run(String arg) {
		main(null);
	}

	public static void main(String[] args) {
		IJ.showMessage("radec", "constructo");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			IJ.showMessage("Failed to initialize Windows Look-Feel");
		}

		EventQueue.invokeLater(() -> {
			runCatalogsPlugin();
		});

	}

	public static void runCatalogsPlugin() {

		// Read last saved catalog dialog data from properties file
		// First run loads default WASP-12 data set
		//CatalogQuery query = PropertiesFileReader.readPropertiesFile();

		// Instantiates file writer objects
		//PropertiesFileWriter pfw = new PropertiesFileWriter();
		
		//RaDecFileWriter rdw = new RaDecFileWriter();

		// Opens catalog user interface with properties or default data
		
		PropertiesFileIO pf = new PropertiesFileIO();
		
		CatalogTableModel ctm = new CatalogTableModel();
		
		ActionHandler handler = new ActionHandler(pf);
		
		CatalogUI catalogUi = new CatalogUI(handler, ctm);
		
		handler.setCatalogTableListener(ctm);
		
		handler.setCatalogDataListener(catalogUi);
		
		CatalogQuery query = pf.getPropertiesQueryData();
		catalogUi.setQueryData(query);
		
		// apply properties target mag
		CatalogSettings settings = pf.getPropertiesSettingsData();
		catalogUi.setSettingsData(settings);
		

//		// sets up file writer as listeners to catalog query & save property file events
//		catalogUi.setPropsWriterListener(pfw);
//		catalogUi.setRaDecWriterListener(rdw);

		// finally set JDialog modal and visible after objects and form complete
		// initialisation
		// Set dialog modal to lock-out AIJ toolbar while vsp_demo open, otherwise bad
		// things can happen ..
		catalogUi.setModal(true);
		catalogUi.setVisible(true);
	}

}
