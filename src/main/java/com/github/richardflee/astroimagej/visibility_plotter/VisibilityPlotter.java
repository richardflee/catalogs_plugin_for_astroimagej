package com.github.richardflee.astroimagej.visibility_plotter;

import java.awt.Dialog;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;

import com.github.richardflee.astroimagej.query_objects.AltAzmObject;
import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Creates Altitude and StarTrack visibility plots at observer's location 
 * for the current target object on selected starting night  
 *
 */
public class VisibilityPlotter {

	private TimesConverter timesConverter = null;

	private JDialog trackDialog = null;
    private JDialog altitudeDialog = null;
    private static String chartTitle = null;

	public VisibilityPlotter(ObservationSite site) {
		this.timesConverter = new TimesConverter(site);
	}

	public void plotVisiblityCharts(BaseFieldObject fo, LocalDate civilDate) {
		// compiles a list of alt-azm objects with chart coordinate and time data
		// with 1 minute (60s) sample spacing
		List<AltAzmObject> altAzmObjects = getAltAzmObjects(fo, civilDate);
		
		// chart title <target_name> <start night>
		VisibilityPlotter.chartTitle = String.format("%s %s", AltAzmObject.getName(), civilDate.toString());
		
		// StarAlt plot
		JFreeChart starAltChart = StarAltPlotter.createChart(altAzmObjects, this.timesConverter.getSite());
		showAltitudeDialog(starAltChart);
		
		// StarTrack plot
		JFreeChart starTrackChart = StarTrackPlotter.createChart(altAzmObjects);
		showTrackDialog(starTrackChart);
	}
	

	private void showTrackDialog(JFreeChart chart) {
		this.trackDialog = createDialog("StarTrack Visibility Plot");
		this.trackDialog.setSize(new Dimension(750, 750));

		ChartPanel chartPanel = new ChartPanel(chart);
		this.trackDialog.add(chartPanel);
		this.trackDialog.setVisible(true);
	}
	
	private void showAltitudeDialog(JFreeChart chart) {
		this.altitudeDialog = createDialog("StarAlt Visibility Plot");
		this.altitudeDialog.setSize(new Dimension(1000, 750));

		ChartPanel chartPanel = new ChartPanel(chart);
		this.altitudeDialog.add(chartPanel);
		this.altitudeDialog.setVisible(true);	
	}
	
	private JDialog createDialog(String title) {
		JDialog dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		return dialog;
	}

	private List<AltAzmObject> getAltAzmObjects(BaseFieldObject fo, LocalDate civilDate) {

		List<AltAzmObject> altAzmList = new ArrayList<>();
		ObservationSite site = timesConverter.getSite();
		AltAzmGenerator gen = new AltAzmGenerator(fo, site);

		// noon on starting night
		LocalDateTime civilDateTime = LocalDateTime.of(civilDate, TimesConverter.LOCAL_TIME_NOON);
		Minute current = TimesConverter.convertCivilDateTimeToMinute(civilDateTime);

		// 24 hr in 1 minute steps
		for (int i = 0; i < 24 * 60; i++) {
			altAzmList.add(gen.getAltAzmObject(current));
			// LocalDateTime ldt = TimesConverter.convertMinuteToCivilDateTime(current);
			current = (Minute) current.next();
		}
		return altAzmList;
	}


	public static String getChartTitle() {
		return chartTitle;
	}

	public static void main(String[] args) {
		// site
		ObservationSite site;
		double siteLong = -85.5; // 71.05W
		double siteLat = 38.33; // 42.37N
		double siteElevation = 0.0;
		site = new ObservationSite(siteLong, siteLat, siteElevation, -5.0);

		// object
		String objectId = "wasp12";
		double raHr = AstroCoords.raHmsToRaHr("06:30:32.797");
		double decDeg = AstroCoords.decDmsToDecDeg("+29:40:20.27");
		BaseFieldObject fo = new BaseFieldObject(objectId, raHr, decDeg);

		// starting night
		LocalDate civilDate = LocalDate.of(2019, 1, 1);

		VisibilityPlotter visPlotter = new VisibilityPlotter(site);
		visPlotter.plotVisiblityCharts(fo, civilDate);
	}

}
