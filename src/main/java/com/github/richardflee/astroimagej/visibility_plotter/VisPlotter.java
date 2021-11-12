package com.github.richardflee.astroimagej.visibility_plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.JDialog;

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

	public void showChart(BaseFieldObject fo, LocalDate civilDate) {

		this.coords = new CoordsConverter(fo, site);

		this.chartTitle = String.format("%s %s", fo.getObjectId(), civilDate.toString());

		// altitude-time dataset
		XYDataset dataset = createDataset(civilDate);

		// solar times
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

	private void createChartDialog(ChartPanel chartPanel) {

		// clears open chart dialog
		closeChart();

		// create dialog
		this.dialog = new JDialog();
		this.dialog.setContentPane(chartPanel);
		this.dialog.setSize(new Dimension(1000, 750));
		this.dialog.setTitle("Visibility Plot");
		this.dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// prevents blocking by modal chatUi dialog ...
		dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

		// shoe dialog
		dialog.setVisible(true);
	}

	private JFreeChart createChart(XYDataset dataset, SolarTimes solarTimes) {
		boolean toolTips = true;
		JFreeChart chart = ChartFactory.createTimeSeriesChart(this.chartTitle, "Civil Time (Hr)", "Altitude (Deg)",
				dataset, false, toolTips, false);

		XYPlot plot = chart.getXYPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(0.0, 90.0);

		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunSet()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightEnds()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightStarts()));
		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunRise()));

		return chart;
	}

	private Marker riseSetMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(1.0f));
		return marker;
	}

	private Marker twilightMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(0.5f));
		return marker;
	}

	private XYDataset createDataset(LocalDate civilDate) {
		TimeSeries series = new TimeSeries("Visibility Plot");

		LocalDateTime civilDateTime = LocalDateTime.of(civilDate, TimesConverter.LOCAL_TIME_NOON);
		Minute current = TimesConverter.convertCivilDateTimeToMinute(civilDateTime);

		for (int i = 0; i < 24 * 60; i++) {
			try {
				double altValue = getCurrentAlt(current, civilDate);
				series.add(current, new Double(altValue));
				current = (Minute) current.next();
			} catch (SeriesException e) {
				System.err.println("Error adding to series");
			}
		}
		return new TimeSeriesCollection(series);
	}

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
