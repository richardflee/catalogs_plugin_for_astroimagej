package com.github.richardflee.astroimagej.visibility_plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Plots single object altitude over 24 hour period with sunset, sunrise and
 * twilight time markers. The geographic location is specified in AstroImageJ
 * Coordinate Converter. 
 * <p>JFreeChart: https://www.jfree.org/jfreechart/</p>
 */
public class VisPlotter {

	// field variables
	private ObservationSite site = null;
	private TimesConverter timesConverter = null;
	private CoordsConverter coords = null;
	private Solar solar = null;

	private JDialog dialog;

	private String chartTitle = null;

	public VisPlotter(ObservationSite site) {
		this.site = site;
		this.timesConverter = new TimesConverter(site);
		this.solar = new Solar(site);
	}

	/**
	 * Creates and shows altitude chart
	 * 
	 * @param fo object of interest
	 * @param civilDate starting night
	 */
	public void showChart(BaseFieldObject fo, LocalDate civilDate) {

		this.coords = new CoordsConverter(fo, site);
		this.chartTitle = String.format("%s %s", fo.getObjectId(), civilDate.toString());

		// altitude-time dataset
		XYDataset dataset = createDataset(civilDate);

		// solar times sunset, sunrise & twilight
		SolarTimes solarTimes = solar.getCivilSunTimes(civilDate);

		// alt-time JFree chart panel
		JFreeChart chart = createChart(dataset, solarTimes);
		ChartPanel chartPanel = new ChartPanel(chart);
		createChartDialog(chartPanel);
	}

	/**
	 * Closes visPlot chart
	 */
	public void closeChart() {
		if (this.dialog != null) {
			this.dialog.dispose();
		}
	}

	/*
	 * Creates non-modal chart dialog Altitude-Time plot 
	 * 
	 * @param chartPanel JFreeChart panel
	 */
	private void createChartDialog(ChartPanel chartPanel) {
		// clears open chart dialog
		closeChart();

		// create dialog
		this.dialog = new JDialog();
		this.dialog.setContentPane(chartPanel);
		this.dialog.setSize(new Dimension(1000, 750));
		this.dialog.setTitle("Visibility Plot");
		this.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// prevents blocking by modal chartUi dialog ...
		dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

		// show dialog
		dialog.setVisible(true);
	}

	/*
	 * Creates altitude time chart
	 */
	private JFreeChart createChart(XYDataset dataset, SolarTimes solarTimes) {
		boolean toolTips = true;
		JFreeChart chart = ChartFactory.createTimeSeriesChart(this.chartTitle, "Civil Time (Hr)", "Altitude (Deg)",
				dataset, false, toolTips, false);

		// initialise plot ans set y-axis scale 0-90deg
		XYPlot plot = chart.getXYPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(0.0, 90.0);

		// draw vertical sunset, sunrise & twilight markers
		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunSet()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightEnds()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightStarts()));
		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunRise()));

		return chart;
	}

	/*
	 * Creates a marker object to indicate sunset or sunrise times
	 */
	private Marker riseSetMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(1.0f));
		return marker;
	}

	/*
	 * Creates a marker object ot indicate twilight end or start times
	 */
	private Marker twilightMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(0.5f));
		return marker;
	}

	/*
	 * Compiles altitude -time results into a time series collection 
	 */
	private XYDataset createDataset(LocalDate civilDate) {
		TimeSeries series = new TimeSeries("Visibility Plot");

		// noon on starting night
		LocalDateTime civilDateTime = LocalDateTime.of(civilDate, TimesConverter.LOCAL_TIME_NOON);
		Minute current = TimesConverter.convertCivilDateTimeToMinute(civilDateTime);
		
		// 24 hr in 1 minute steps
		for (int i = 0; i < 24 * 60; i++) {
			try {
				// computes altitude at this instant and adds to series
				double altValue = getCurrentAlt(current, civilDate);
				series.add(current, new Double(altValue));
				// increments curent time by 1 minute
				current = (Minute) current.next();
			} catch (SeriesException e) {
				System.err.println("Error adding to series");
				// info new properties file
				String message = "Error adding altitude data to JFreeChart series";
				JOptionPane.showMessageDialog(null, message);
			}
		}
		return new TimeSeriesCollection(series);
	}

	/*
	 * Computes current object altitude; 0 if altitude is below horizon
	 */
	private double getCurrentAlt(Minute current, LocalDate civilDate) {
		LocalDateTime civilDateTime = TimesConverter.convertMinuteToCivilDateTime(current, civilDate);
		LocalDateTime utcDateTime = this.timesConverter.convertCivilDateTimeToUtc(civilDateTime);
		double alt = coords.getAltAzm(utcDateTime).get(CoordsConverter.CoordsEnum.ALT_DEG);
		return (alt >= 0.0) ? alt : 0.0;
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
		LocalDate civilDate = LocalDate.of(2019, 3, 1);

		VisPlotter visPlot = new VisPlotter(site);
		visPlot.showChart(fo, civilDate);
	}

}
