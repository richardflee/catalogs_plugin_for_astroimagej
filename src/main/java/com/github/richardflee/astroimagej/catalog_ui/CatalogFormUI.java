/*
 * Created by JFormDesigner on Sat Jul 17 11:53:33 BST 2021
 */

package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.*;

/**
 * @author Richard Lee
 */
public class CatalogFormUI extends JDialog {
	public CatalogFormUI(Window owner) {
		super(owner);
		initComponents();
		
		saveButton.addActionListener(e -> dispose());
	
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
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
		simbadButton = new JButton();
		catalogButton = new JButton();
		panel2 = new JPanel();
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
		closeButton = new JButton();
		panel5 = new JPanel();
		label6 = new JLabel();
		label13 = new JLabel();
		label14 = new JLabel();
		spinner1 = new JSpinner();
		spinner2 = new JSpinner();
		spinner3 = new JSpinner();
		magCheckBox = new JCheckBox();
		label15 = new JLabel();
		label16 = new JLabel();
		lowerMagLabel = new JLabel();
		panel6 = new JPanel();
		radialRadioButton = new JRadioButton();
		magRadioButton = new JRadioButton();
		saveButton = new JButton();

		//======== this ========
		setTitle("VSP Demo v0.1");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

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

					//---- simbadButton ----
					simbadButton.setText("SIMBAD");
					simbadButton.setToolTipText("<html>\nRuns a search for ObjectID on the SIMBAD online database\n<p>If the search is successful:</p>\n<p>  - updates RA and Dec fields with SIMBAD coordinates</p>\n<p>  - updates SIMBAD Data and Filter Magnitudes fields</p>\n<p>Note: ObjectID value is unchanged</p>\n</html>");

					//---- catalogButton ----
					catalogButton.setText("Catalog");
					catalogButton.setToolTipText("<html>\nRuns queries on the selected online Catalog and DSS database over\n<p>a search region specified by FOV, RA and Dec parameters</p>\n<p>  - saves catalog ref star parameters in a radec file for the selected filter band</p>\n<p>  - saves a DSS FITS file for the specified region</p>\n</html>\n");

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
										.addComponent(catalogButton))
									.addGroup(panel1Layout.createSequentialGroup()
										.addComponent(catalogCombo, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(simbadButton))
									.addGroup(panel1Layout.createSequentialGroup()
										.addGroup(panel1Layout.createParallelGroup()
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
										.addGap(0, 74, Short.MAX_VALUE)))
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
										.addGap(0, 0, Short.MAX_VALUE))
									.addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(simbadButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(catalogButton)))
								.addContainerGap())
					);
				}

				//======== panel2 ========
				{

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGap(0, 547, Short.MAX_VALUE)
					);
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addGap(0, 0, Short.MAX_VALUE)
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
								.addContainerGap(64, Short.MAX_VALUE))
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

				//---- closeButton ----
				closeButton.setText("Close");

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

					//---- spinner1 ----
					spinner1.setModel(new SpinnerNumberModel(0.0, 0.0, 5.0, 0.10000000149011612));

					//---- spinner2 ----
					spinner2.setModel(new SpinnerNumberModel(0.0, -5.0, 0.0, 0.10000000149011612));

					//---- spinner3 ----
					spinner3.setModel(new SpinnerNumberModel(10.0, 5.5, 25.0, 0.10000000149011612));

					//---- magCheckBox ----
					magCheckBox.setText("Apply mag limits");
					magCheckBox.setSelected(true);

					//---- label15 ----
					label15.setText("17.6");

					//---- label16 ----
					label16.setText("15.1");

					//---- lowerMagLabel ----
					lowerMagLabel.setText("13.5");

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
										.addGap(0, 19, Short.MAX_VALUE)
										.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addComponent(spinner1, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
											.addComponent(spinner2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
											.addComponent(spinner3, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panel5Layout.createParallelGroup()
											.addComponent(label15)
											.addComponent(label16)
											.addComponent(lowerMagLabel))
										.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(panel5Layout.createSequentialGroup()
										.addComponent(magCheckBox)
										.addGap(0, 0, Short.MAX_VALUE))))
					);
					panel5Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {spinner1, spinner2, spinner3});
					panel5Layout.setVerticalGroup(
						panel5Layout.createParallelGroup()
							.addGroup(panel5Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label6)
									.addComponent(spinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label15))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(label14)
									.addComponent(spinner3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(label16))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel5Layout.createParallelGroup()
									.addComponent(label13)
									.addGroup(panel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(spinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lowerMagLabel)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(magCheckBox)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//======== panel6 ========
				{
					panel6.setBorder(new TitledBorder("Ascending Sort"));
					panel6.setPreferredSize(new Dimension(190, 164));
					panel6.setToolTipText("<html>\nSIMBAD query results:\n<p>Populates data fields with results of SIMBAD query </p>\n<p>Fields marked '.' indicate the search failed or no data</p>\n</html>");

					//---- radialRadioButton ----
					radialRadioButton.setText("Distance");
					radialRadioButton.setSelected(true);

					//---- magRadioButton ----
					magRadioButton.setText("|Delta Mag|");

					GroupLayout panel6Layout = new GroupLayout(panel6);
					panel6.setLayout(panel6Layout);
					panel6Layout.setHorizontalGroup(
						panel6Layout.createParallelGroup()
							.addGroup(panel6Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(panel6Layout.createParallelGroup()
									.addComponent(magRadioButton)
									.addComponent(radialRadioButton))
								.addContainerGap(19, Short.MAX_VALUE))
					);
					panel6Layout.setVerticalGroup(
						panel6Layout.createParallelGroup()
							.addGroup(panel6Layout.createSequentialGroup()
								.addGap(4, 4, 4)
								.addComponent(radialRadioButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(magRadioButton)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					);
				}

				//---- saveButton ----
				saveButton.setText("Save");
				saveButton.setToolTipText("<html>\nSaves current dialog settings\n</html>\n");

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
									.addGroup(contentPanelLayout.createSequentialGroup()
										.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
									.addGroup(contentPanelLayout.createSequentialGroup()
										.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(panel6, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
									.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addComponent(saveButton, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
				contentPanelLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {closeButton, saveButton});
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPanelLayout.createParallelGroup()
								.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel4, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(panel5, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel6, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
							.addGap(18, 18, 18)
							.addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(closeButton)
								.addComponent(saveButton))
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(panel2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());

		//---- buttonGroup1 ----
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radialRadioButton);
		buttonGroup1.add(magRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
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
	private JButton simbadButton;
	private JButton catalogButton;
	private JPanel panel2;
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
	private JButton closeButton;
	private JPanel panel5;
	private JLabel label6;
	private JLabel label13;
	private JLabel label14;
	private JSpinner spinner1;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JCheckBox magCheckBox;
	private JLabel label15;
	private JLabel label16;
	private JLabel lowerMagLabel;
	private JPanel panel6;
	private JRadioButton radialRadioButton;
	private JRadioButton magRadioButton;
	private JButton saveButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
