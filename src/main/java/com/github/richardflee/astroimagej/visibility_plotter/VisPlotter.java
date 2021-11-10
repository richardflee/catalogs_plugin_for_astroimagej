package com.github.richardflee.astroimagej.visibility_plotter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.utils.AstroCoords;

public class VisPlotter extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	// field variables
	private ObservationSite site = null;
	private TimesConverter timesConverter = null;
	private CoordsConverter coords = null;

	public VisPlotter(ObservationSite site) {
		super("Visibility Plot");
		this.site = site;
		this.timesConverter = new TimesConverter(site);
	}

	public void plotAltitude(BaseFieldObject fo, LocalDate civilDate) {
		this.coords = new CoordsConverter(fo, site);

		XYDataset dataset = createDataset(civilDate);
		JFreeChart chart = createChart(dataset);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
	}

	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Visibility Plot", "Civil Time (Hr)", "Altitude (Deg)",
				dataset, false, false, false);

		XYPlot plot = chart.getXYPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(0.0, 90.0);

		return chart;
	}

	private double getCurrentAlt(Minute current, LocalDate civilDate) {

		LocalDateTime civilDateTime = TimesConverter.convertMinuteToCivilDateTime(current, civilDate);
		LocalDateTime utcDateTime = this.timesConverter.convertCivilDateTimeToUtc(civilDateTime);
		double alt = coords.getAltAzm(utcDateTime).get(CoordsConverter.CoordsEnum.ALT_DEG);
		return (alt >= 0.0) ? alt : 0.0;
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
		LocalDate civilDate = LocalDate.of(2019,  1,  1);

		VisPlotter demo = new VisPlotter(site);
		demo.plotAltitude(fo, civilDate);
		demo.pack();
		demo.setVisible(true);
	}

}
