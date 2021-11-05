/*
 * Created by JFormDesigner on Sat Jul 17 11:53:33 BST 2021
 */

package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.time.LocalDate;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.enums.QueryEnum;
import com.github.richardflee.astroimagej.listeners.CatalogDataListener;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SimbadResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;
import com.github.richardflee.astroimagej.utils.InputsVerifier;

/**
 * This class creates the main catalogs_plugin user interface. User options are:
 * <p> Specify and run on-line astronomical database queries </p>
 * 
 * <p> Sort and filter query results </p> <p> Options to save and reload data to
 * and from astroimagej compatible radec file </p>
 * 
 * <p> Note: form design and layout in JFormDesigner v7.0.4 </p>
 * 
 * <p>Note that query result table and item select/deselect option is delegated to
 * CatalogTable class </p>
 */
public class CatalogUI extends JDialog implements CatalogDataListener {
	private static final long serialVersionUID = 1L;

	// statusLine font
	private Font statusLineFont;

	// references to catable tables data model and button click event handler
	protected CatalogTableModel catalogTableModel = null;
	protected ActionHandler handler = null;
	protected CatalogSettings settings = null;

	// enable / disable catalog ui buttons flag
	private boolean isTableData;

	/**
	 * Initialises catalog_ui object references
	 */
	public CatalogUI(ActionHandler handler, CatalogTableModel catalogTableModel) {
		
		// JFormDesigner
		initComponents();

		statusLineFont = new Font("Tahoma", Font.PLAIN, 13);

		// catalog table & data model
		this.catalogTableModel = catalogTableModel;
		new CatalogTable(this, catalogTableModel);
		
		DatePicker datePicker = new DatePicker();
		datePicker.setDate(LocalDate.now());
		visDatePickerPanel.add(datePicker);

		// button click event handlers
		this.handler = handler;

		// configures button click events and query text input verifiers
		setUpActionListeners();

		// populate catalog combo
		catalogCombo.addItem(CatalogsEnum.APASS.toString());
		catalogCombo.addItem(CatalogsEnum.VSP.toString());
		
		// start with Distance sort option selected
		distanceRadioButton.setSelected(true);
		deltaMagRadioButton.setSelected(false);

		// start in "no data" state
		this.isTableData = false;
		setButtonsEnabled(isTableData);
		
		
		
		
		
	}

	/**
	 * Prints status message line, error messages indicated by ERROR in text line
	 * 
	 * @param statusMessage red text if message contains ERROR, otherwise black text
	 *                      for info messages
	 */
	@Override
	public void updateStatus(String statusMessage) {
		// sets text colour and font
		Color fontColor = statusMessage.toUpperCase().contains("ERROR") ? Color.RED : Color.BLACK;
		statusTextField.setForeground(fontColor);
		statusTextField.setFont(statusLineFont);
		statusTextField.setText(statusMessage);
	}

	/**
	 * Populates SIMBAD Data and Filter Mag sections with result of Simbad query.
	 * Marks fields "." if no magnitude data for this objectId
	 * 
	 * @param simbadResult coordinates and catalog mags for user specified objectId;
	 *                     null if objectId is not found in Simbad database
	 */
	@Override
	public void setSimbadData(SimbadResult simbadResult) {
		// null => reset simbad data
		if (simbadResult == null) {
			simbadIdLabel.setText(".");
			simbadRaLabel.setText(".");
			simbadDecLabel.setText(".");
			simbadMagBLabel.setText(".");
			simbadMagVLabel.setText(".");
			simbadMagRLabel.setText(".");
			simbadMagILabel.setText(".");
			return;
		}

		// Simbad catalog match for user objectId
		// update catalog ra
		String raHms = AstroCoords.raHr_To_raHms(simbadResult.getSimbadRaHr());
		raField.setText(raHms);
		raField.setForeground(Color.black);

		// update catalog dec
		String decDms = AstroCoords.decDeg_To_decDms(simbadResult.getSimbadDecDeg());
		decField.setText(decDms);
		decField.setForeground(Color.black);

		// update info labels
		simbadIdLabel.setText(simbadResult.getSimbadId());
		simbadRaLabel.setText(raHms);
		simbadDecLabel.setText(decDms);

		// handle no data, usually R and I bands
		String mag = (simbadResult.getMagB() == null) ? "." : simbadResult.getMagB().toString();
		simbadMagBLabel.setText(mag);

		mag = (simbadResult.getMagV() == null) ? "." : simbadResult.getMagV().toString();
		simbadMagVLabel.setText(mag);

		mag = (simbadResult.getMagR() == null) ? "." : simbadResult.getMagR().toString();
		simbadMagRLabel.setText(mag);

		mag = (simbadResult.getMagI() == null) ? "." : simbadResult.getMagI().toString();
		simbadMagILabel.setText(mag);
	}

	/**
	 * Updates catalog UI query settings section with current query object data
	 * 
	 * @param query encapsulates user input query parameters
	 */
	@Override
	public void setQueryData(CatalogQuery query) {
		objectIdField.setText(query.getObjectId());
		raField.setText(AstroCoords.raHr_To_raHms(query.getRaHr()));
		decField.setText(AstroCoords.decDeg_To_decDms(query.getDecDeg()));
		fovField.setText(String.format("%.1f", query.getFovAmin()));
		magLimitField.setText(String.format("%.1f", query.getMagLimit()));

		// selected catalog
		CatalogsEnum en = query.getCatalogType();
		String selectedCatalog = en.toString().toUpperCase();
		catalogCombo.setSelectedItem(selectedCatalog);

		// populate filterCombo
		String selectedFilter = query.getMagBand();
		populateFilterCombo(selectedCatalog, selectedFilter);
		// filterCombo.setSelectedItem(query.getMagBand());
	}
	
	/**
	 * Compiles valid user form inputs into a CatalogQuery object.
	 * 
	 * @return CatalogQuery object; null if any user input is not valid
	 */
	@Override
	public CatalogQuery getQueryData() {
		
		// return null query if invalid input
		if (!verifyAllInputs()) {
			return null;
		}
		
		// copy text field data
		CatalogQuery query = new CatalogQuery();
		query.setObjectId(objectIdField.getText());
		query.setRaHr(AstroCoords.raHms_To_raHr(raField.getText()));
		query.setDecDeg(AstroCoords.decDms_To_decDeg(decField.getText()));
		query.setFovAmin(Double.valueOf(fovField.getText()));
		query.setMagLimit(Double.valueOf(magLimitField.getText()));

		// copy combo data
		query.setCatalogType(CatalogsEnum.getEnum(catalogCombo.getSelectedItem().toString()));
		query.setMagBand(filterCombo.getSelectedItem().toString());

		return query;
	}

	/*
	 * Updates catalog ui sort and filter sections with current settings data
	 * 
	 * @param settings encapsulates sort and filter user input parameters
	 */
	@Override
	public void setSettingsData(CatalogSettings settings) {
		
		// user input nominal target magnitude
		targetMagSpinner.setValue(settings.getTargetMagSpinnerValue());

		// set catalogui controls to default values
		// mag limits
		upperLimitSpinner.setValue(settings.getUpperLimitSpinnerValue());
		lowerLimitSpinner.setValue(settings.getLowerLimitSpinnerValue());
		isMagLimitsCheckBox.setSelected(settings.isMagLimitsCheckBoxValue());

		// sort option
		distanceRadioButton.setSelected(settings.isDistanceRadioButtonValue());
		deltaMagRadioButton.setSelected(settings.isDeltaMagRadioButtonValue());

		// number of observations
		nObsSpinner.setValue(settings.getnObsSpinnerValue());

		// mag limit labels
		upperLabel.setText(settings.getUpperLabelValue());
		lowerLabel.setText(settings.getLowerLabelValue());

		// record numbers
		totalRecordsField.setText(String.format("%s", settings.getTotalRecordsValue()));
		filteredRecordsField.setText(String.format("%s", settings.getFilteredRecordsValue()));
		selectedRecordsField.setText(String.format("%s", settings.getSelectedRecordsValue()));

		// update enable buttons flag
		this.isTableData = settings.isTableData();
	}

	/*
	 * Copies catalog ui user filter and sort selections to CatalogSettings object values 
	 * 
	 * @return compiled CatalogSettings object
	 */
	@Override
	public CatalogSettings getSettingsData() {
		CatalogSettings settings = new CatalogSettings();
		
		// target mag
		settings.setTargetMagSpinnerValue((Double) targetMagSpinner.getValue());

		// mag limits
		settings.setMagLimitsCheckBoxValue(isMagLimitsCheckBox.isSelected());
		settings.setUpperLimitSpinnerValue(Double.valueOf(upperLimitSpinner.getValue().toString()));
		settings.setLowerLimitSpinnerValue(Double.valueOf(lowerLimitSpinner.getValue().toString()));

		// sort option
		settings.setDistanceRadioButtonValue(distanceRadioButton.isSelected());
		settings.setDeltaMagRadioButtonValue(deltaMagRadioButton.isSelected());

		// number of observations
		settings.setnObsSpinnerValue((int) nObsSpinner.getValue());
		
		settings.setSaveDssCheckBoxValue(isSaveDssCheckBox.isSelected());

		return settings;
	}
	
	
	@Override
	public void setObservationSiteData(ObservationSite site) {
		if (site == null) {
			site= new ObservationSite();
		}
		
		String data = AstroCoords.degToDms(site.getSiteLongitudeDeg());
		siteLongField.setText(data);
		
		data = AstroCoords.degToDms(site.getSiteLatitudeDeg());
		siteLatField.setText(data);
		
		data = String.format("%4.0f", site.getSiteAlt());
		siteAltField.setText(data);
		
		data = String.format("%3.1f", site.getUtcOffsetHr());
		utcOffsetField.setText(data);			
	}

	@Override
	public ObservationSite getObservationSiteData() {
		ObservationSite site = new ObservationSite();
		
		double data = AstroCoords.dmsToDeg(siteLongField.getText());
		site.setSiteLongitudeDeg(data);
		
		data = AstroCoords.dmsToDeg(siteLatField.getText());
		site.setSiteLatitudeDeg(data);
		
		data = Double.valueOf(siteAltField.getText());
		site.setSiteAlt(data);
		
		data = Double.valueOf(utcOffsetField.getText());
		site.setUtcOffsetHr(data);		
		return site;
	}
	
	/*
	 * Handles change in catalogCombo selection. Clears existing filterCombo list
	 * and loads a new list based on CatalogType enum
	 * 
	 * @param ie event indicates an item was selected in the catalogCombo control
	 */
	private void selectCatalog(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED) {
			// get current catalogCombo selection & catalog type
			String selectedCatalog = catalogCombo.getSelectedItem().toString().toUpperCase();
			CatalogsEnum en = CatalogsEnum.valueOf(selectedCatalog);

			// populate filterCombo and select first item
			populateFilterCombo(selectedCatalog, en.getMagBands().get(0));
		}
	}

	/*
	 * Clears current and imports new filter list in the filter selection combo
	 * 
	 * @param selectedCatalog uppercase name of current catalog selected in catalog
	 * combo
	 * 
	 * @param selectedFilter filter name of current filter selection in filter combo
	 */
	private void populateFilterCombo(String selectedCatalog, String selectedFilter) {
		// clear filters list
		filterCombo.removeAllItems();

		// retrieve catalog from enum
		CatalogsEnum en = CatalogsEnum.valueOf(selectedCatalog);

		// import filter list for selected catalog & select specified filter
		en.getMagBands().forEach(item -> filterCombo.addItem(item));
		filterCombo.setSelectedItem(selectedFilter);
	}

	/*
	 * Configures button and combo event listeners and sets data/no-data button
	 * states
	 */
	private void setUpActionListeners() {
		simbadButton.addActionListener(e -> {
			handler.doSimbadQuery();
			setButtonsEnabled(this.isTableData);			
		});

		saveQueryButton.addActionListener(e -> {
			handler.doSaveQuerySettingsData();
			setButtonsEnabled(this.isTableData);			
		});

		catalogQueryButton.addActionListener(e -> {
			handler.doCatalogQuery();
			setButtonsEnabled(this.isTableData);
		});

		updateButton.addActionListener(e -> {
			handler.doUpdateTable();
			setButtonsEnabled(this.isTableData);
		});

		saveRaDecButton.addActionListener(e -> {
			handler.doSaveRaDecFile();
			setButtonsEnabled(this.isTableData);			
		});

		importRaDecButton.addActionListener(e -> {
			handler.doImportRaDecFile();
			setButtonsEnabled(this.isTableData);
		});

		clearButton.addActionListener(e -> {
			handler.doClearTable();
			setButtonsEnabled(false);
		});
		
		visPlotterButton.addActionListener(e -> {
			handler.doPlotVisibility();
		});

		closeButton.addActionListener(e -> System.exit(0));

		// verifies user inputs in query text fields
		CatalogInputsVerifier inputVerifier = new CatalogInputsVerifier(this);
		objectIdField.addActionListener(e -> inputVerifier.verifyObjectId(objectIdField.getText()));
		raField.addActionListener(e -> inputVerifier.verifyRaHms(raField.getText()));
		decField.addActionListener(e -> inputVerifier.verifyDecDms(decField.getText()));
		fovField.addActionListener(e -> inputVerifier.verifyFov(fovField.getText()));
		magLimitField.addActionListener(e -> inputVerifier.verifyMagLimit(magLimitField.getText()));

		// handles change in selected catalog (VSP, APASS ..)
		catalogCombo.addItemListener(ie -> selectCatalog(ie));
	}

	/*
	 * Tests all input fileds are in range before compiling on-line database query
	 * 
	 * @return true of all query data inputs are in range, false otherwise
	 */
	private boolean verifyAllInputs() {
		boolean isValid = InputsVerifier.isValidObjectId(objectIdField.getText());
		isValid = isValid && InputsVerifier.isValidCoords(raField.getText(), QueryEnum.RA_HMS);
		isValid = isValid && InputsVerifier.isValidCoords(decField.getText(), QueryEnum.DEC_DMS);
		isValid = isValid && InputsVerifier.isValidFov(fovField.getText());
		isValid = isValid && InputsVerifier.isValidMagLimit(magLimitField.getText());
		return isValid;
	}

	/*
	 * Enables or disables [Update] and [Clear] buttons
	 * 
	 * @param isEnabled true if there is table data, false if table data is null;
	 */
	private void setButtonsEnabled(boolean isTableData) {
		// enable buttons when no data table is empty
		simbadButton.setEnabled(!isTableData);
		saveQueryButton.setEnabled(!isTableData);
		catalogQueryButton.setEnabled(!isTableData);
		saveRaDecButton.setEnabled(!isTableData);
		
		// enable buttons when data table is populated
		saveRaDecButton.setEnabled(isTableData);
		updateButton.setEnabled(isTableData);
		clearButton.setEnabled(isTableData);
		
		// save dss checkbox in sync with save radec
		isSaveDssCheckBox.setEnabled(isTableData);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		panel1 = new JPanel();
		label1 = new JLabel();
		objectIdField = new JTextField();
		label2 = new JLabel();
		raField = new JTextField();
		label3 = new JLabel();
		decField = new JTextField();
		label4 = new JLabel();
		fovField = new JTextField();
		label5 = new JLabel();
		magLimitField = new JTextField();
		label7 = new JLabel();
		label8 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		catalogCombo = new JComboBox<>();
		label11 = new JLabel();
		filterCombo = new JComboBox<>();
		label12 = new JLabel();
		isSaveDssCheckBox = new JCheckBox();
		panel2 = new JPanel();
		tableScrollPane = new JScrollPane();
		panel3 = new JPanel();
		idLabel = new JLabel();
		raLabel = new JLabel();
		decLabel = new JLabel();
		simbadIdLabel = new JLabel();
		simbadRaLabel = new JLabel();
		simbadDecLabel = new JLabel();
		panel4 = new JPanel();
		idLabel2 = new JLabel();
		idLabel3 = new JLabel();
		idLabel4 = new JLabel();
		idLabel5 = new JLabel();
		simbadMagBLabel = new JLabel();
		simbadMagVLabel = new JLabel();
		simbadMagRLabel = new JLabel();
		simbadMagILabel = new JLabel();
		panel5 = new JPanel();
		label6 = new JLabel();
		label13 = new JLabel();
		label14 = new JLabel();
		upperLimitSpinner = new JSpinner();
		lowerLimitSpinner = new JSpinner();
		targetMagSpinner = new JSpinner();
		isMagLimitsCheckBox = new JCheckBox();
		upperLabel = new JLabel();
		label16 = new JLabel();
		lowerLabel = new JLabel();
		panel6 = new JPanel();
		distanceRadioButton = new JRadioButton();
		deltaMagRadioButton = new JRadioButton();
		label17 = new JLabel();
		nObsSpinner = new JSpinner();
		panel7 = new JPanel();
		simbadButton = new JButton();
		closeButton = new JButton();
		saveQueryButton = new JButton();
		catalogQueryButton = new JButton();
		updateButton = new JButton();
		saveRaDecButton = new JButton();
		importRaDecButton = new JButton();
		clearButton = new JButton();
		visPlotterButton = new JButton();
		statusTextField = new JTextField();
		panel9 = new JPanel();
		label15 = new JLabel();
		label18 = new JLabel();
		label22 = new JLabel();
		totalRecordsField = new JTextField();
		selectedRecordsField = new JTextField();
		filteredRecordsField = new JTextField();
		panel10 = new JPanel();
		label19 = new JLabel();
		label20 = new JLabel();
		label23 = new JLabel();
		siteLongField = new JTextField();
		siteAltField = new JTextField();
		siteLatField = new JTextField();
		label21 = new JLabel();
		utcOffsetField = new JTextField();
		panel11 = new JPanel();
		label24 = new JLabel();
		label26 = new JLabel();
		sunSetField = new JTextField();
		twilightEndField = new JTextField();
		label27 = new JLabel();
		twilightStartField = new JTextField();
		label25 = new JLabel();
		sunRiseField = new JTextField();
		visDatePickerPanel = new JPanel();

		//======== this ========
		setTitle("On-line Catalog Query");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setBorder(null);

				//======== panel1 ========
				{
					panel1.setBorder(new TitledBorder("Catalog Query settings"));

					//---- label1 ----
					label1.setText("ObjectID");

					//---- objectIdField ----
					objectIdField.setText("WASP-12");
					objectIdField.setFocusCycleRoot(true);
					objectIdField.setBackground(Color.white);
					objectIdField.setToolTipText("<html>\nEnter target name\n<p> Note: This field is <b>not </b>changed after a SIMBAD query</p>\n<p>Examples: WASP-12, wasp_12, Wasp12</p>\n</html>");

					//---- label2 ----
					label2.setText("RA:");

					//---- raField ----
					raField.setText("06:30:32.80");
					raField.setToolTipText("<html>\nEnter target J2000 RA (hours), in sexagesimal format  HH:MM:SS.SS\n<p>Note 1: Use  ':' delimiter</p>\n<p>Note 2: RA updates after a successful SIMBAD query</p>\n<p>RA range: 00:00:00 to 23:59:59.99</p>\n<p>Examples: 12:34:56.78,  1: 2:3.456, => 01:02:03.46</p>\n</html>");

					//---- label3 ----
					label3.setText("Dec:");

					//---- decField ----
					decField.setText("+20:40:20.27");
					decField.setToolTipText("<html>\nEnter target J2000 DEC (degrees), in sexagesimal format  \u00b1DD:MM:SS.SS\n<p>Note 1: Use  ':' delimiter</p>\n<p>Note 2: DEC updates after a successful SIMBAD query</p>\n<p>DEC range: 00:00:00 to \u00b190:00:00</p>\n<p>Examples: 12:34:56.78 => +12:34:56.78,  -1:2:3.456, => -01:02:03.46</p>\n</html>");

					//---- label4 ----
					label4.setText("Fov:");

					//---- fovField ----
					fovField.setText("60.0");
					fovField.setToolTipText("<html>\nEnter square field-of-view width (arcmin)\n<p>FOV range: 1.0 to 1199.9 amin\n<p>Examples: 25.0, 1150.0</p>\n</html>");

					//---- label5 ----
					label5.setText("Limit:");

					//---- magLimitField ----
					magLimitField.setText("17.0");
					magLimitField.setToolTipText("<html>\nEnter magnitude limit\n<p>Range: 1.0 to 99.9 mag\n<p>Example: 15.5</p>\n</html>");

					//---- label7 ----
					label7.setText(" HH:MM:SS.SS");

					//---- label8 ----
					label8.setText("\u00b1DD:MM:SS.SS");

					//---- label9 ----
					label9.setText("arcmin");

					//---- label10 ----
					label10.setText("mag");

					//---- catalogCombo ----
					catalogCombo.setToolTipText("<html>\nSelect on-line astronomical database from list\n</html>");

					//---- label11 ----
					label11.setText("Catalog:");

					//---- filterCombo ----
					filterCombo.setToolTipText("<html>\nSelect photometric filter from list\n<p>Listed filters depends on selected catalog</p>\n</html>");

					//---- label12 ----
					label12.setText("Filter:");

					//---- isSaveDssCheckBox ----
					isSaveDssCheckBox.setText("Save DSS Fits File");
					isSaveDssCheckBox.setSelected(true);

					GroupLayout panel1Layout = new GroupLayout(panel1);
					panel1.setLayout(panel1Layout);
					panel1Layout.setHorizontalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addGap(15, 15, 15)
								.addGroup(panel1Layout.createParallelGroup()
									.addComponent(label1, GroupLayout.Alignment.TRAILING)
									.addComponent(label2, GroupLayout.Alignment.TRAILING)
									.addComponent(label3, GroupLayout.Alignment.TRAILING)
									.addComponent(label4, GroupLayout.Alignment.TRAILING)
									.addComponent(label5, GroupLayout.Alignment.TRAILING)
									.addComponent(label11, GroupLayout.Alignment.TRAILING)
									.addComponent(label12, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(isSaveDssCheckBox))
									.addGroup(panel1Layout.createSequentialGroup()
										.addGroup(panel1Layout.createParallelGroup()
											.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
											.addGroup(panel1Layout.createSequentialGroup()
												.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
													.addComponent(fovField, GroupLayout.Alignment.LEADING)
													.addComponent(decField, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
													.addComponent(raField, GroupLayout.Alignment.LEADING)
													.addComponent(magLimitField))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(panel1Layout.createParallelGroup()
													.addComponent(label7)
													.addComponent(label8)
													.addComponent(label9)
													.addComponent(label10)))
											.addComponent(objectIdField, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
										.addGap(0, 0, Short.MAX_VALUE)))
								.addContainerGap())
					);
					panel1Layout.setVerticalGroup(
						panel1Layout.createParallelGroup()
							.addGroup(panel1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label1)
									.addComponent(objectIdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label2))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(raField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label7))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label3))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(decField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label8))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label4))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(fovField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label9))))
								.addGroup(panel1Layout.createParallelGroup()
									.addGroup(panel1Layout.createSequentialGroup()
										.addGap(16, 16, 16)
										.addComponent(label5))
									.addGroup(panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(magLimitField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label10))))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label11))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label12)
									.addComponent(isSaveDssCheckBox))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel2 ========
				{
					panel2.setBorder(LineBorder.createBlackLineBorder());

					//======== tableScrollPane ========
					{
						tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					}

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addComponent(tableScrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
					);
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addComponent(tableScrollPane)
					);
				}

				//======== panel3 ========
				{
					panel3.setBorder(new TitledBorder("SIMBAD Data"));
					panel3.setPreferredSize(new Dimension(190, 164));

					//---- idLabel ----
					idLabel.setText("ObjectId:");

					//---- raLabel ----
					raLabel.setText("RA:");

					//---- decLabel ----
					decLabel.setText("DEC:");

					//---- simbadIdLabel ----
					simbadIdLabel.setText(".");

					//---- simbadRaLabel ----
					simbadRaLabel.setText("HH:MM:SS.SS");

					//---- simbadDecLabel ----
					simbadDecLabel.setText("DD:MM:SS.SS");

					GroupLayout panel3Layout = new GroupLayout(panel3);
					panel3.setLayout(panel3Layout);
					panel3Layout.setHorizontalGroup(
						panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
								.addGroup(panel3Layout.createParallelGroup()
									.addGroup(panel3Layout.createSequentialGroup()
										.addGap(25, 25, 25)
										.addGroup(panel3Layout.createParallelGroup()
											.addComponent(raLabel, GroupLayout.Alignment.TRAILING)
											.addComponent(decLabel, GroupLayout.Alignment.TRAILING)))
									.addGroup(panel3Layout.createSequentialGroup()
										.addContainerGap()
										.addComponent(idLabel)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel3Layout.createParallelGroup()
									.addComponent(simbadIdLabel)
									.addComponent(simbadRaLabel)
									.addComponent(simbadDecLabel))
								.addGap(0, 37, Short.MAX_VALUE))
					);
					panel3Layout.setVerticalGroup(
						panel3Layout.createParallelGroup()
							.addGroup(panel3Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(simbadIdLabel)
									.addComponent(idLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(raLabel)
									.addComponent(simbadRaLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(decLabel)
									.addComponent(simbadDecLabel))
								.addContainerGap(29, Short.MAX_VALUE))
					);
				}

				//======== panel4 ========
				{
					panel4.setBorder(new TitledBorder("Filter Mag"));
					panel4.setPreferredSize(new Dimension(190, 164));

					//---- idLabel2 ----
					idLabel2.setText("MagB:");

					//---- idLabel3 ----
					idLabel3.setText("MagV:");

					//---- idLabel4 ----
					idLabel4.setText("MagR:");

					//---- idLabel5 ----
					idLabel5.setText("MagI:");

					//---- simbadMagBLabel ----
					simbadMagBLabel.setText(".");

					//---- simbadMagVLabel ----
					simbadMagVLabel.setText(".");

					//---- simbadMagRLabel ----
					simbadMagRLabel.setText(".");

					//---- simbadMagILabel ----
					simbadMagILabel.setText(".");

					GroupLayout panel4Layout = new GroupLayout(panel4);
					panel4.setLayout(panel4Layout);
					panel4Layout.setHorizontalGroup(
						panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(idLabel2, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel3, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel4, GroupLayout.Alignment.TRAILING)
									.addComponent(idLabel5, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel4Layout.createParallelGroup()
									.addComponent(simbadMagBLabel)
									.addComponent(simbadMagVLabel)
									.addComponent(simbadMagRLabel)
									.addComponent(simbadMagILabel))
								.addContainerGap(45, Short.MAX_VALUE))
					);
					panel4Layout.setVerticalGroup(
						panel4Layout.createParallelGroup()
							.addGroup(panel4Layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel2)
									.addComponent(simbadMagBLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel3)
									.addComponent(simbadMagVLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel4)
									.addComponent(simbadMagRLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(idLabel5)
									.addComponent(simbadMagILabel)))
					);
				}

				//======== panel5 ========
				{
					panel5.setBorder(new TitledBorder("Set Mag Limits"));
					panel5.setPreferredSize(new Dimension(190, 164));

					//---- label6 ----
					label6.setText("Upper:");

					//---- label13 ----
					label13.setText("Lower:");

					//---- label14 ----
					label14.setText("Nominal:");

					//---- upperLimitSpinner ----
					upperLimitSpinner.setModel(new SpinnerNumberModel(0.0, -0.1, 5.0, 0.10000000149011612));
					upperLimitSpinner.setToolTipText("<html>\nSet the target mag upper limit\n<p>Setting Upper = 0 disables this limit</p>\n<p>Range: 0 - 5 mag in 0.1 mag increment</p>\n</html>");

					//---- lowerLimitSpinner ----
					lowerLimitSpinner.setModel(new SpinnerNumberModel(0.0, -5.0, 0.1, 0.10000000149011612));
					lowerLimitSpinner.setToolTipText("<html>\nSet the target mag lower limit\n<p>Setting Lower = 0 disables this limit</p>\n<p>Range: -5 - 0 mag in 0.1 mag increment</p>\n</html>\n");

					//---- targetMagSpinner ----
					targetMagSpinner.setModel(new SpinnerNumberModel(10.0, 5.5, 25.0, 0.10000000149011612));
					targetMagSpinner.setToolTipText("<html>\nSet the nominal target mag for the selected filter band\n<p>Use the scroll control  or type value in text box</p>\n<p>Range: 5.5 - 25 mag in 0.1 mag increment</p>\n</html>");

					//---- isMagLimitsCheckBox ----
					isMagLimitsCheckBox.setText("Apply mag limits");
					isMagLimitsCheckBox.setSelected(true);

					//---- upperLabel ----
					upperLabel.setText("N/A");

					//---- label16 ----
					label16.setText(".");

					//---- lowerLabel ----
					lowerLabel.setText("N/A");

					GroupLayout panel5Layout = new GroupLayout(panel5);
					panel5.setLayout(panel5Layout);
					panel5Layout.setHorizontalGroup(
						panel5Layout.createParallelGroup()
							.addGroup(panel5Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel5Layout.createParallelGroup()
									.addGroup(panel5Layout.createSequentialGroup()
										.addGroup(panel5Layout.createParallelGroup()
											.addComponent(label14, GroupLayout.Alignment.TRAILING)
											.addComponent(label6, GroupLayout.Alignment.TRAILING)
											.addComponent(label13, GroupLayout.Alignment.TRAILING))
										.addGap(0, 20, Short.MAX_VALUE)
										.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addComponent(upperLimitSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
											.addComponent(lowerLimitSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
											.addComponent(targetMagSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel5Layout.createParallelGroup()
											.addComponent(upperLabel)
											.addComponent(label16)
											.addComponent(lowerLabel))
										.addContainerGap(15, Short.MAX_VALUE))
									.addGroup(panel5Layout.createSequentialGroup()
										.addComponent(isMagLimitsCheckBox)
										.addGap(0, 0, Short.MAX_VALUE))))
					);
					panel5Layout.setVerticalGroup(
						panel5Layout.createParallelGroup()
							.addGroup(panel5Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label6)
									.addComponent(upperLimitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(upperLabel))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label14)
									.addComponent(targetMagSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label16))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel5Layout.createParallelGroup()
									.addComponent(label13)
									.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(lowerLimitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lowerLabel)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(isMagLimitsCheckBox)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel6 ========
				{
					panel6.setBorder(new TitledBorder("Ascending Sort"));
					panel6.setPreferredSize(new Dimension(190, 164));

					//---- distanceRadioButton ----
					distanceRadioButton.setText("Distance");
					distanceRadioButton.setSelected(true);
					distanceRadioButton.setToolTipText("<html>\nSort table records in ascending order of\n<p>radial separation to objectId coordinates</p>\n<p>Separation: arcmin</p>\n</html>");

					//---- deltaMagRadioButton ----
					deltaMagRadioButton.setText("|Delta Mag|");
					deltaMagRadioButton.setToolTipText("<html>\nSort table records in ascending order of\n<p>absolute difference in magnitude</p>\n<p>|Delta Mag| = |reference_mag - target_mag|</p>\n</html>\n");

					//---- label17 ----
					label17.setText("Nobs (APASS)");

					//---- nObsSpinner ----
					nObsSpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));
					nObsSpinner.setToolTipText("<html>\nSet minimum number of observations APASS catalog\n<p>Range: 1 to 10 (defaults to Nobs =1)</p>\n</html>\n");

					GroupLayout panel6Layout = new GroupLayout(panel6);
					panel6.setLayout(panel6Layout);
					panel6Layout.setHorizontalGroup(
						panel6Layout.createParallelGroup()
							.addGroup(panel6Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel6Layout.createParallelGroup()
									.addGroup(panel6Layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addGroup(panel6Layout.createParallelGroup()
											.addComponent(deltaMagRadioButton)
											.addComponent(distanceRadioButton)))
									.addGroup(panel6Layout.createSequentialGroup()
										.addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
											.addComponent(label17)
											.addGroup(panel6Layout.createSequentialGroup()
												.addGap(10, 10, 10)
												.addComponent(nObsSpinner)))
										.addGap(0, 0, Short.MAX_VALUE))))
					);
					panel6Layout.setVerticalGroup(
						panel6Layout.createParallelGroup()
							.addGroup(panel6Layout.createSequentialGroup()
								.addGap(4, 4, 4)
								.addComponent(distanceRadioButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(deltaMagRadioButton)
								.addGap(18, 18, 18)
								.addComponent(label17)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(nObsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel7 ========
				{

					//---- simbadButton ----
					simbadButton.setText("SIMBAD Query");
					simbadButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- closeButton ----
					closeButton.setText("Close");
					closeButton.setToolTipText("<html>\nCloses Catalog Query dialog and returns control to AstroImageJ application\n<p>WARNING: Discaards any unsaved settings</p>\n</html>");

					//---- saveQueryButton ----
					saveQueryButton.setText("Save Query Data");
					saveQueryButton.setToolTipText("<html>\nSaves the current Catalog Query settings\n</html>");

					//---- catalogQueryButton ----
					catalogQueryButton.setText("Run Catalog Query");
					catalogQueryButton.setToolTipText("<html>\nSaves current Catalog Query settings and runs a query\n<p>on the selected online Catalog database with these settings</p>\n</html>");

					//---- updateButton ----
					updateButton.setText("Update Table");
					updateButton.setToolTipText("<html>\nUpdates table with current Sort and data filter settings\n</html>");

					//---- saveRaDecButton ----
					saveRaDecButton.setText("Save RaDec File");
					saveRaDecButton.setToolTipText("<html>\nSaves a radec format file to import apertures into AstroImageJ\n<p>File is saved in folder:  ./AstroImage/radec</p>\n<p> Filename format: [objectId].[filter].[fov_amin].radec.txt</p>\n<p> e.g. ./AstroImageJ/radec/wasp12.Rc.020.radec.txt</p>\n</html>");

					//---- importRaDecButton ----
					importRaDecButton.setText("Import RaDec File");
					importRaDecButton.setToolTipText("<html>\nReads user selected radec file and loads data into the Catalog Table  \n</html>");

					//---- clearButton ----
					clearButton.setText("Clear");
					clearButton.setToolTipText("<html>\nClears current query result data and  removes catalog table records\n</html>");

					//---- visPlotterButton ----
					visPlotterButton.setText("Plot Altitude");
					visPlotterButton.setToolTipText("<html>\nCloses Catalog Query dialog and returns control to AstroImageJ application\n<p>WARNING: Discaards any unsaved settings</p>\n</html>");

					GroupLayout panel7Layout = new GroupLayout(panel7);
					panel7.setLayout(panel7Layout);
					panel7Layout.setHorizontalGroup(
						panel7Layout.createParallelGroup()
							.addGroup(panel7Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel7Layout.createParallelGroup()
									.addGroup(panel7Layout.createSequentialGroup()
										.addGroup(panel7Layout.createParallelGroup()
											.addComponent(simbadButton, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
											.addComponent(saveQueryButton)
											.addComponent(catalogQueryButton)
											.addComponent(importRaDecButton))
										.addGap(0, 0, Short.MAX_VALUE))
									.addGroup(GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addGroup(panel7Layout.createParallelGroup()
											.addComponent(saveRaDecButton, GroupLayout.Alignment.TRAILING)
											.addComponent(closeButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
											.addComponent(updateButton, GroupLayout.Alignment.TRAILING)
											.addComponent(clearButton, GroupLayout.Alignment.TRAILING)
											.addComponent(visPlotterButton, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))))
								.addContainerGap())
					);
					panel7Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {catalogQueryButton, clearButton, closeButton, importRaDecButton, saveQueryButton, saveRaDecButton, simbadButton, updateButton});
					panel7Layout.setVerticalGroup(
						panel7Layout.createParallelGroup()
							.addGroup(panel7Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(simbadButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(saveQueryButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(catalogQueryButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(importRaDecButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(saveRaDecButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addGap(127, 127, 127)
								.addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addGap(23, 23, 23)
								.addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
								.addComponent(visPlotterButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
					);
				}

				//---- statusTextField ----
				statusTextField.setEditable(false);
				statusTextField.setToolTipText("/html>\nStatus line\n<html>");
				statusTextField.setBackground(new Color(240, 240, 240));

				//======== panel9 ========
				{
					panel9.setBorder(new TitledBorder("Record counts"));
					panel9.setPreferredSize(new Dimension(190, 164));

					//---- label15 ----
					label15.setText("Total");

					//---- label18 ----
					label18.setText("Selected");

					//---- label22 ----
					label22.setText("Filtered");

					//---- totalRecordsField ----
					totalRecordsField.setToolTipText("<html>\nSet the target mag upper limit\n<p>Setting Upper = 0 disables this limit</p>\n<p>Range: 0 - 5 mag in 0.1 mag increment</p>\n</html>");
					totalRecordsField.setEditable(false);
					totalRecordsField.setText("1000");

					//---- selectedRecordsField ----
					selectedRecordsField.setToolTipText("<html>\nSet the target mag lower limit\n<p>Setting Lower = 0 disables this limit</p>\n<p>Range: -5 - 0 mag in 0.1 mag increment</p>\n</html>\n");
					selectedRecordsField.setEditable(false);
					selectedRecordsField.setText("0");

					//---- filteredRecordsField ----
					filteredRecordsField.setToolTipText("<html>\nSet the nominal target mag for the selected filter band\n<p>Use the scroll control  or type value in text box</p>\n<p>Range: 5.5 - 25 mag in 0.1 mag increment</p>\n</html>");
					filteredRecordsField.setEditable(false);
					filteredRecordsField.setText("0");

					GroupLayout panel9Layout = new GroupLayout(panel9);
					panel9.setLayout(panel9Layout);
					panel9Layout.setHorizontalGroup(
						panel9Layout.createParallelGroup()
							.addGroup(panel9Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(label15)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(totalRecordsField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label22)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(filteredRecordsField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label18)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(selectedRecordsField, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))
					);
					panel9Layout.setVerticalGroup(
						panel9Layout.createParallelGroup()
							.addGroup(panel9Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel9Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label15)
									.addComponent(totalRecordsField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(filteredRecordsField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label22)
									.addComponent(label18)
									.addComponent(selectedRecordsField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel10 ========
				{
					panel10.setBorder(new TitledBorder("Geographic Location and UTC Offset"));
					panel10.setPreferredSize(new Dimension(190, 164));

					//---- label19 ----
					label19.setText("Lon:");

					//---- label20 ----
					label20.setText("Alt:");

					//---- label23 ----
					label23.setText("Lat:");

					//---- siteLongField ----
					siteLongField.setToolTipText("<html>\nSet the target mag upper limit\n<p>Setting Upper = 0 disables this limit</p>\n<p>Range: 0 - 5 mag in 0.1 mag increment</p>\n</html>");
					siteLongField.setEditable(false);
					siteLongField.setText("000:00:00.00");

					//---- siteAltField ----
					siteAltField.setToolTipText("<html>\nSet the target mag lower limit\n<p>Setting Lower = 0 disables this limit</p>\n<p>Range: -5 - 0 mag in 0.1 mag increment</p>\n</html>\n");
					siteAltField.setEditable(false);
					siteAltField.setText("1000");

					//---- siteLatField ----
					siteLatField.setToolTipText("<html>\nSet the nominal target mag for the selected filter band\n<p>Use the scroll control  or type value in text box</p>\n<p>Range: 5.5 - 25 mag in 0.1 mag increment</p>\n</html>");
					siteLatField.setEditable(false);
					siteLatField.setText("+00:00:00.00");

					//---- label21 ----
					label21.setText("UTC offset:");

					//---- utcOffsetField ----
					utcOffsetField.setToolTipText("<html>\nSet the target mag lower limit\n<p>Setting Lower = 0 disables this limit</p>\n<p>Range: -5 - 0 mag in 0.1 mag increment</p>\n</html>\n");
					utcOffsetField.setEditable(false);
					utcOffsetField.setText("0.0");

					GroupLayout panel10Layout = new GroupLayout(panel10);
					panel10.setLayout(panel10Layout);
					panel10Layout.setHorizontalGroup(
						panel10Layout.createParallelGroup()
							.addGroup(panel10Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(label19)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(siteLongField, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label23)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(siteLatField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label20)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(siteAltField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(label21)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(utcOffsetField, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))
					);
					panel10Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {siteLatField, siteLongField});
					panel10Layout.setVerticalGroup(
						panel10Layout.createParallelGroup()
							.addGroup(panel10Layout.createSequentialGroup()
								.addGroup(panel10Layout.createParallelGroup()
									.addGroup(panel10Layout.createSequentialGroup()
										.addGap(13, 13, 13)
										.addGroup(panel10Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(label21)
											.addComponent(utcOffsetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
									.addGroup(GroupLayout.Alignment.TRAILING, panel10Layout.createSequentialGroup()
										.addContainerGap()
										.addGroup(panel10Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(label19)
											.addComponent(label23)
											.addComponent(siteLatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label20)
											.addComponent(siteAltField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(siteLongField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel11 ========
				{
					panel11.setBorder(new TitledBorder("Civil Solar Times "));
					panel11.setPreferredSize(new Dimension(190, 164));

					//---- label24 ----
					label24.setText("Sunset:");

					//---- label26 ----
					label26.setText("Twi End:");

					//---- sunSetField ----
					sunSetField.setToolTipText("<html>\nSet the target mag upper limit\n<p>Setting Upper = 0 disables this limit</p>\n<p>Range: 0 - 5 mag in 0.1 mag increment</p>\n</html>");
					sunSetField.setEditable(false);
					sunSetField.setText("18:11");

					//---- twilightEndField ----
					twilightEndField.setToolTipText("<html>\nSet the nominal target mag for the selected filter band\n<p>Use the scroll control  or type value in text box</p>\n<p>Range: 5.5 - 25 mag in 0.1 mag increment</p>\n</html>");
					twilightEndField.setEditable(false);
					twilightEndField.setText("18:11");

					//---- label27 ----
					label27.setText("Twi Start:");

					//---- twilightStartField ----
					twilightStartField.setToolTipText("<html>\nSet the nominal target mag for the selected filter band\n<p>Use the scroll control  or type value in text box</p>\n<p>Range: 5.5 - 25 mag in 0.1 mag increment</p>\n</html>");
					twilightStartField.setEditable(false);
					twilightStartField.setText("18:11");

					//---- label25 ----
					label25.setText("Sunrise");

					//---- sunRiseField ----
					sunRiseField.setToolTipText("<html>\nSet the target mag upper limit\n<p>Setting Upper = 0 disables this limit</p>\n<p>Range: 0 - 5 mag in 0.1 mag increment</p>\n</html>");
					sunRiseField.setEditable(false);
					sunRiseField.setText("18:11");

					GroupLayout panel11Layout = new GroupLayout(panel11);
					panel11.setLayout(panel11Layout);
					panel11Layout.setHorizontalGroup(
						panel11Layout.createParallelGroup()
							.addGroup(panel11Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(label24)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(sunSetField, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label26)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(twilightEndField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label27)
								.addGap(5, 5, 5)
								.addComponent(twilightStartField, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(label25)
								.addGap(5, 5, 5)
								.addComponent(sunRiseField, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
					panel11Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {sunSetField, twilightEndField});
					panel11Layout.setVerticalGroup(
						panel11Layout.createParallelGroup()
							.addGroup(panel11Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel11Layout.createParallelGroup()
									.addGroup(panel11Layout.createSequentialGroup()
										.addGap(3, 3, 3)
										.addComponent(label25))
									.addComponent(sunRiseField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGroup(panel11Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addGroup(panel11Layout.createParallelGroup()
											.addGroup(panel11Layout.createSequentialGroup()
												.addGap(3, 3, 3)
												.addComponent(label27))
											.addComponent(twilightStartField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGroup(panel11Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(label24)
											.addComponent(sunSetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label26)
											.addComponent(twilightEndField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== visDatePickerPanel ========
				{
					visDatePickerPanel.setBorder(null);
					visDatePickerPanel.setLayout(new FlowLayout());
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(statusTextField, GroupLayout.DEFAULT_SIZE, 1300, Short.MAX_VALUE)
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(panel10, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
										.addComponent(panel11, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
										.addGroup(contentPanelLayout.createSequentialGroup()
											.addComponent(panel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(18, 18, 18)
											.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
												.addComponent(panel9, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
												.addGroup(contentPanelLayout.createSequentialGroup()
													.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
												.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addGroup(contentPanelLayout.createSequentialGroup()
													.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(panel6, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
												.addComponent(visDatePickerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
							.addContainerGap())
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup()
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addGroup(contentPanelLayout.createSequentialGroup()
											.addComponent(panel1, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
												.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
												.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
												.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
												.addComponent(panel6, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(panel9, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(visDatePickerPanel, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
										.addComponent(panel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(panel11, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(panel10, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
								.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(statusTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel panel1;
	private JLabel label1;
	protected JTextField objectIdField;
	private JLabel label2;
	protected JTextField raField;
	private JLabel label3;
	protected JTextField decField;
	private JLabel label4;
	protected JTextField fovField;
	private JLabel label5;
	protected JTextField magLimitField;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	protected JComboBox<String> catalogCombo;
	private JLabel label11;
	protected JComboBox<String> filterCombo;
	private JLabel label12;
	protected JCheckBox isSaveDssCheckBox;
	private JPanel panel2;
	protected JScrollPane tableScrollPane;
	private JPanel panel3;
	private JLabel idLabel;
	private JLabel raLabel;
	private JLabel decLabel;
	private JLabel simbadIdLabel;
	private JLabel simbadRaLabel;
	private JLabel simbadDecLabel;
	private JPanel panel4;
	private JLabel idLabel2;
	private JLabel idLabel3;
	private JLabel idLabel4;
	private JLabel idLabel5;
	private JLabel simbadMagBLabel;
	private JLabel simbadMagVLabel;
	private JLabel simbadMagRLabel;
	private JLabel simbadMagILabel;
	private JPanel panel5;
	private JLabel label6;
	private JLabel label13;
	private JLabel label14;
	protected JSpinner upperLimitSpinner;
	protected JSpinner lowerLimitSpinner;
	protected JSpinner targetMagSpinner;
	protected JCheckBox isMagLimitsCheckBox;
	protected JLabel upperLabel;
	private JLabel label16;
	protected JLabel lowerLabel;
	private JPanel panel6;
	protected JRadioButton distanceRadioButton;
	protected JRadioButton deltaMagRadioButton;
	private JLabel label17;
	protected JSpinner nObsSpinner;
	private JPanel panel7;
	protected JButton simbadButton;
	protected JButton closeButton;
	protected JButton saveQueryButton;
	protected JButton catalogQueryButton;
	protected JButton updateButton;
	protected JButton saveRaDecButton;
	protected JButton importRaDecButton;
	protected JButton clearButton;
	protected JButton visPlotterButton;
	private JTextField statusTextField;
	private JPanel panel9;
	private JLabel label15;
	private JLabel label18;
	private JLabel label22;
	protected JTextField totalRecordsField;
	protected JTextField selectedRecordsField;
	protected JTextField filteredRecordsField;
	private JPanel panel10;
	private JLabel label19;
	private JLabel label20;
	private JLabel label23;
	protected JTextField siteLongField;
	protected JTextField siteAltField;
	protected JTextField siteLatField;
	private JLabel label21;
	protected JTextField utcOffsetField;
	private JPanel panel11;
	private JLabel label24;
	private JLabel label26;
	protected JTextField sunSetField;
	protected JTextField twilightEndField;
	private JLabel label27;
	protected JTextField twilightStartField;
	private JLabel label25;
	protected JTextField sunRiseField;
	private JPanel visDatePickerPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
