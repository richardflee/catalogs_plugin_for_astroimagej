/*
 * Created by JFormDesigner on Sat Jul 17 11:53:33 BST 2021
 */

package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.*;

import javax.swing.DefaultComboBoxModel;
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


/**
 * @author Richard Lee
 */
public class CatalogFormUI extends JDialog {
	private static final long serialVersionUID = 1L;

	private SimpleListener aListener;
	protected ActionHandler handler;

	public CatalogFormUI() {
		initComponents();

		aListener = new MyModel();

		handler = new ActionHandler(this);

		setUpActionListeners();

		setButtonsEnabled(false);

	}

	private void setUpActionListeners() {
		simbadButton.addActionListener(e -> System.out.println("Simbad"));

		saveQueryButton.addActionListener(e -> System.out.println("save query"));

		catalogQueryButton.addActionListener(e -> {
			handler.doCatalogQuery(aListener);
			setButtonsEnabled(true);			
		});

		updateButton.addActionListener(e -> handler.doUpdateTable(aListener));

		saveRaDecButton.addActionListener(e -> System.out.println("save radec"));

		importRaDecButton.addActionListener(e -> System.out.println("import radec"));

		clearButton.addActionListener(e -> {
			aListener.updateTable(null);
			totalLabel.setText("0");
			filteredLabel.setText("0");
			setButtonsEnabled(false);
			
		});

		closeButton.addActionListener(e -> System.exit(0));

	}

	private void setButtonsEnabled(boolean isEnabled) {
		updateButton.setEnabled(isEnabled);
		saveRaDecButton.setEnabled(isEnabled);
		clearButton.setEnabled(isEnabled);
	}

	public void setSimpleListener(SimpleListener aListener) {
		this.aListener = aListener;
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
		magSpinner = new JSpinner();
		isMagLimitsCheckBox = new JCheckBox();
		upperLabel = new JLabel();
		label16 = new JLabel();
		lowerLabel = new JLabel();
		panel6 = new JPanel();
		radSepRadioButton = new JRadioButton();
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
		label19 = new JLabel();
		label20 = new JLabel();
		totalLabel = new JLabel();
		filteredLabel = new JLabel();

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
					catalogCombo.setModel(new DefaultComboBoxModel<>(new String[] {
						"VSP",
						"APASS"
					}));

					//---- label11 ----
					label11.setText("Catalog:");

					//---- label12 ----
					label12.setText("Filter:");

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
									.addComponent(filterCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
								.addContainerGap(62, Short.MAX_VALUE))
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
									.addComponent(label12))
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
							.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
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
					panel3.setToolTipText("<html>\nSIMBAD query results:\n<p>Populates data fields with results of SIMBAD query </p>\n<p>Fields marked '.' indicate the search failed or no data</p>\n</html>");

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
								.addContainerGap(47, Short.MAX_VALUE))
					);
				}

				//======== panel4 ========
				{
					panel4.setBorder(new TitledBorder("Filter Mag"));
					panel4.setPreferredSize(new Dimension(190, 164));
					panel4.setToolTipText("<html>\nSIMBAD query results:\n<p>Populates data fields with results of SIMBAD query </p>\n<p>Fields marked '.' indicate the search failed or no data</p>\n</html>");

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
								.addContainerGap()
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
									.addComponent(simbadMagILabel))
								.addContainerGap(18, Short.MAX_VALUE))
					);
				}

				//======== panel5 ========
				{
					panel5.setBorder(new TitledBorder("Set Mag Limits"));
					panel5.setPreferredSize(new Dimension(190, 164));
					panel5.setToolTipText("<html>\nSIMBAD query results:\n<p>Populates data fields with results of SIMBAD query </p>\n<p>Fields marked '.' indicate the search failed or no data</p>\n</html>");

					//---- label6 ----
					label6.setText("Upper:");

					//---- label13 ----
					label13.setText("Lower:");

					//---- label14 ----
					label14.setText("Nominal:");

					//---- upperLimitSpinner ----
					upperLimitSpinner.setModel(new SpinnerNumberModel(0.0, -0.1, 5.0, 0.10000000149011612));

					//---- lowerLimitSpinner ----
					lowerLimitSpinner.setModel(new SpinnerNumberModel(0.0, -5.0, 0.1, 0.10000000149011612));

					//---- magSpinner ----
					magSpinner.setModel(new SpinnerNumberModel(10.0, 5.5, 25.0, 0.10000000149011612));

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
											.addComponent(magSpinner, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
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
					panel5Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lowerLimitSpinner, magSpinner, upperLimitSpinner});
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
									.addComponent(magSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
					panel6.setToolTipText("<html>\nSIMBAD query results:\n<p>Populates data fields with results of SIMBAD query </p>\n<p>Fields marked '.' indicate the search failed or no data</p>\n</html>");

					//---- radSepRadioButton ----
					radSepRadioButton.setText("Distance");
					radSepRadioButton.setSelected(true);

					//---- deltaMagRadioButton ----
					deltaMagRadioButton.setText("|Delta Mag|");

					//---- label17 ----
					label17.setText("Nobs (APASS)");

					//---- nObsSpinner ----
					nObsSpinner.setModel(new SpinnerNumberModel(1, 1, 10, 1));

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
											.addComponent(radSepRadioButton)))
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
								.addComponent(radSepRadioButton)
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
					closeButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- saveQueryButton ----
					saveQueryButton.setText("Save Query Data");
					saveQueryButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- catalogQueryButton ----
					catalogQueryButton.setText("Run Catalog Query");
					catalogQueryButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- updateButton ----
					updateButton.setText("Update Table");
					updateButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- saveRaDecButton ----
					saveRaDecButton.setText("Save RaDec File");
					saveRaDecButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- importRaDecButton ----
					importRaDecButton.setText("Import RaDec File");
					importRaDecButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- clearButton ----
					clearButton.setText("Clear");
					clearButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					GroupLayout panel7Layout = new GroupLayout(panel7);
					panel7.setLayout(panel7Layout);
					panel7Layout.setHorizontalGroup(
						panel7Layout.createParallelGroup()
							.addGroup(panel7Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel7Layout.createParallelGroup()
									.addGroup(GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
									.addGroup(panel7Layout.createSequentialGroup()
										.addGroup(panel7Layout.createParallelGroup()
											.addComponent(simbadButton, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
											.addComponent(saveQueryButton)
											.addComponent(catalogQueryButton)
											.addComponent(updateButton)
											.addComponent(saveRaDecButton)
											.addComponent(importRaDecButton)
											.addComponent(clearButton))
										.addGap(0, 0, Short.MAX_VALUE)))
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
								.addComponent(updateButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(saveRaDecButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(importRaDecButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
					);
				}

				//---- label19 ----
				label19.setText("Total catalog records:");

				//---- label20 ----
				label20.setText("Filtered records:");

				//---- totalLabel ----
				totalLabel.setText("000");

				//---- filteredLabel ----
				filteredLabel.setText("0");

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup()
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addComponent(panel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(contentPanelLayout.createParallelGroup()
										.addGroup(contentPanelLayout.createSequentialGroup()
											.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
										.addGroup(contentPanelLayout.createSequentialGroup()
											.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(panel6, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
										.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(contentPanelLayout.createParallelGroup()
										.addComponent(label20, GroupLayout.Alignment.TRAILING)
										.addComponent(label19, GroupLayout.Alignment.TRAILING))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addGroup(contentPanelLayout.createParallelGroup()
										.addComponent(totalLabel, GroupLayout.Alignment.TRAILING)
										.addComponent(filteredLabel, GroupLayout.Alignment.TRAILING))))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
								.addComponent(panel7, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(panel1, 245, 245, GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18)
									.addGroup(contentPanelLayout.createParallelGroup()
										.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
										.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
										.addComponent(panel6, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(label19)
								.addComponent(totalLabel))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(label20)
								.addComponent(filteredLabel))
							.addContainerGap(109, Short.MAX_VALUE))
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radSepRadioButton);
		buttonGroup1.add(deltaMagRadioButton);
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel panel1;
	private JLabel label1;
	private JTextField objectIdField;
	private JLabel label2;
	private JTextField raField;
	private JLabel label3;
	private JTextField decField;
	private JLabel label4;
	private JTextField fovField;
	private JLabel label5;
	private JTextField magLimitField;
	private JLabel label7;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JComboBox<String> catalogCombo;
	private JLabel label11;
	private JComboBox<String> filterCombo;
	private JLabel label12;
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
	protected JSpinner magSpinner;
	protected JCheckBox isMagLimitsCheckBox;
	private JLabel upperLabel;
	private JLabel label16;
	private JLabel lowerLabel;
	private JPanel panel6;
	protected JRadioButton radSepRadioButton;
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
	private JLabel label19;
	private JLabel label20;
	protected JLabel totalLabel;
	protected JLabel filteredLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
