package com.github.richardflee.astroimagej.visibility_plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.github.richardflee.astroimagej.query_objects.AltAzmObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.query_objects.SolarTimes;

public class StarAltPlotter {

	// fields
	private static List<AltAzmObject> altAzmObjects = null;

	public static JFreeChart createChart(List<AltAzmObject> altAzmObjects, ObservationSite site) {
		StarAltPlotter.altAzmObjects = altAzmObjects;

		// creates JFreeChart star track chart
		XYDataset dataset = StarAltPlotter.createDataset();
		boolean toolTips = true;
		JFreeChart chart = ChartFactory.createTimeSeriesChart(VisibilityPlotter.getChartTitle(), "Civil Time (Hr)",
				"Altitude (Deg)", dataset, false, toolTips, false);

		// initialise plot ans set y-axis scale 0-90deg
		XYPlot plot = chart.getXYPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(0.0, 90.0);

		// solar times sunset, sunrise & twilight
		Solar solar = new Solar(site);
		LocalDate civilDate = altAzmObjects.get(0).getCivilDateTime().toLocalDate();
		SolarTimes solarTimes = solar.getCivilSunTimes(civilDate);

		// draw vertical sunset, sunrise & twilight markers
		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunSet()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightEnds()));
		plot.addDomainMarker(twilightMarker(solarTimes.getCivilTwilightStarts()));
		plot.addDomainMarker(riseSetMarker(solarTimes.getCivilSunRise()));

		return chart;
	}

	private static XYDataset createDataset() {
		TimeSeries series = new TimeSeries("VisibilityPlot");

		for (AltAzmObject altAzmObject : StarAltPlotter.altAzmObjects) {
			double altDeg = altAzmObject.getAltDeg();
			altDeg = (altDeg >= 0) ? altDeg : 0.0;
			Minute current = altAzmObject.getCurrentMinute();
			series.add(current, altDeg);
		}
		return new TimeSeriesCollection(series);
	}

	/*
	 * Creates a marker object to indicate sunset or sunrise times
	 */
	private static Marker riseSetMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(1.0f));
		return marker;
	}

	/*
	 * Creates a marker object ot indicate twilight end or start times
	 */
	private static Marker twilightMarker(LocalDateTime civilDateTime) {
		long millis = TimesConverter.convertCivilDateTimeToMillis(civilDateTime);
		Marker marker = new ValueMarker(millis);
		marker.setPaint(Color.DARK_GRAY);
		marker.setStroke(new BasicStroke(0.5f));
		return marker;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
