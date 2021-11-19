package com.github.richardflee.astroimagej.visibility_plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.github.richardflee.astroimagej.query_objects.AltAzmObject;

/**
 * Plots object track observed at selected observation site and starting night
 * over a 24 hour period. <p>Plots altitude > 10° and labels star-track start
 * and finish times</p>
 */

public class StarTrackPlotter {
	// min plot altitude in deg
	private static final double MIN_PLOT_ALT = 10.0;

	// offsets start/finish times from track plot
	private static final int ANNOTATE_OFFSET = 5;

	// text annotate font & chart line widths
	private static Font font = new Font("SansSerif", Font.PLAIN, 18);
	private static Stroke lightStroke = new BasicStroke(0.5f);
	private static Stroke stroke = new BasicStroke(1.0f);
	private static Stroke plotStroke = new BasicStroke(1.5f);

	// fields
	private static List<AltAzmObject> altAzmObjects = null;

	/**
	 * Creates a XY JFreeChart chart depicted as a altitude-azimuth polar plot
	 * @param altAzmObjects
	 *     list of objects encapsulating X, Y plot coordinates
	 * @return JFreeChart XYLineChart depicting an altitude-azimuth polar plot
	 */
	public static JFreeChart createChart(List<AltAzmObject> altAzmObjects) {
		StarTrackPlotter.altAzmObjects = altAzmObjects;

		// creates JFreeChart star track chart
		XYDataset dataset = StarTrackPlotter.createDataset();

		JFreeChart chart = ChartFactory.createXYLineChart(VisibilityPlotter.getChartTitle(), "E-W deg", "N-S deg",
				dataset);

		// adds chart text, lines and axes
		StarTrackPlotter.formatTrackChart(chart.getXYPlot());
		return chart;
	}

	/*
	 * Compiles a list of alt-azm objects into a JFreeChart XYSeries
	 * @return JfreeChart XYSeriesCollection containing XYSeries encapsulating
	 * alt-azm track coordinates
	 */
	private static XYDataset createDataset() {
		XYSeries series = new XYSeries("track");

		for (AltAzmObject altAzmObject : StarTrackPlotter.altAzmObjects) {
			if (altAzmObject.getAltDeg() >= MIN_PLOT_ALT) {
				series.add(altAzmObject.getPolarX(), altAzmObject.getPolarY());
			}
		}
		return new XYSeriesCollection(series);
	}

	/*
	 * Formats XYSeriesLineGraph as polar plot
	 */
	private static void formatTrackChart(XYPlot xyPlot) {
		// configures plot renderer for blue, medium thick
		xyPlot.getRenderer().setSeriesStroke(0, plotStroke);
		xyPlot.getRenderer().setSeriesPaint(0, Color.BLUE);

		// configures XY axes
		setAxis(xyPlot.getDomainAxis(), true);
		setAxis(xyPlot.getRangeAxis(), false);

		// configures chart text and line annotations
		xyPlot = StarTrackPlotter.setTextAnnotations(xyPlot);
		xyPlot = StarTrackPlotter.setLineAnnotations(xyPlot);
		xyPlot = StarTrackPlotter.trackTimes(xyPlot);
	}

	/*
	 * Formats chart text annotations
	 */
	private static XYPlot setTextAnnotations(XYPlot xyPlot) {
		// overhead zenith indicator
		XYTextAnnotation zenith = new XYTextAnnotation("Z", 0.0, 0.0);
		zenith.setFont(StarTrackPlotter.font);
		xyPlot.addAnnotation(zenith);

		// marks NCP or SCP at observing site
		XYTextAnnotation pole = new XYTextAnnotation("Pole", 0.0, 50.0);
		pole.setFont(StarTrackPlotter.font);
		xyPlot.addAnnotation(pole);

		// chart north and east
		XYTextAnnotation north = new XYTextAnnotation("N", 0.0, 95.0);
		XYTextAnnotation east = new XYTextAnnotation("E", -95.0, 0.0);
		north.setFont(StarTrackPlotter.font);
		east.setFont(StarTrackPlotter.font);
		xyPlot.addAnnotation(north);
		xyPlot.addAnnotation(east);

		// horizon markers as ING StarTrack
		XYTextAnnotation horizon1 = new XYTextAnnotation("horizon", -67.2, 67.2);
		XYTextAnnotation horizon2 = new XYTextAnnotation("horizon", 67.2, 67.2);
		horizon1.setFont(StarTrackPlotter.font);
		horizon2.setFont(StarTrackPlotter.font);
		horizon1.setRotationAngle(-0.785);
		horizon2.setRotationAngle(0.785);
		xyPlot.addAnnotation(horizon1);
		xyPlot.addAnnotation(horizon2);

		return xyPlot;
	}

	/*
	 * Formats chart axes annotations
	 */
	private static XYPlot setLineAnnotations(XYPlot xyPlot) {

		// horizon circle
		XYShapeAnnotation unitCircle = new XYShapeAnnotation(new Ellipse2D.Double(-90, -90, 180, 180), stroke,
				Color.DARK_GRAY);
		xyPlot.addAnnotation(unitCircle);

		// cross lines with gap for central 'Z' marker
		XYShapeAnnotation eastLine = new XYShapeAnnotation(new Line2D.Double(-90, 0.0, -5, 0.001), lightStroke,
				Color.DARK_GRAY);
		XYShapeAnnotation westLine = new XYShapeAnnotation(new Line2D.Double(5, 0.0, 90.0, 0.001), lightStroke,
				Color.DARK_GRAY);
		XYShapeAnnotation northLine = new XYShapeAnnotation(new Line2D.Double(0.0, 5.0, 0.001, 90.0), lightStroke,
				Color.DARK_GRAY);
		XYShapeAnnotation southLine = new XYShapeAnnotation(new Line2D.Double(0.0, -5.0, 0.001, -90.0), lightStroke,
				Color.DARK_GRAY);
		xyPlot.addAnnotation(eastLine);
		xyPlot.addAnnotation(westLine);
		xyPlot.addAnnotation(northLine);
		xyPlot.addAnnotation(southLine);

		return xyPlot;

	}

	/*
	 * Sets up XYChart axes ±100°
	 */
	private static void setAxis(ValueAxis axis, boolean vertical) {
		NumberAxis ax = (NumberAxis) axis;
		ax.setRange(-100.0, 100.0);
		ax.setTickUnit(new NumberTickUnit(30.0));
		ax.setVerticalTickLabels(vertical);
	}

	/*
	 * Start and finish time markers, format HH:mm
	 */
	private static XYPlot trackTimes(XYPlot xyPlot) {
		// count number of plot items with altitude >= 10 deg
		long count = StarTrackPlotter.altAzmObjects.stream().filter(p -> p.getAltDeg() >= MIN_PLOT_ALT).count();

		// find the first object with altitude >= 10 deg
		AltAzmObject start = StarTrackPlotter.altAzmObjects.stream().filter(p -> p.getAltDeg() >= MIN_PLOT_ALT)
				.findFirst().get();

		// skips to last item in plot set
		AltAzmObject finish = StarTrackPlotter.altAzmObjects.stream().filter(p -> p.getAltDeg() >= MIN_PLOT_ALT)
				.skip(count - 1).findFirst().get();

		// formats start and end times HH:mm
		String strStart = Solar.LDT_FOMATTER.format(start.getCivilDateTime());
		String strFinish = Solar.LDT_FOMATTER.format(finish.getCivilDateTime());

		// start time annotation
		XYTextAnnotation annotate = new XYTextAnnotation(strStart, start.getPolarX(),
				start.getPolarY() + ANNOTATE_OFFSET);
		annotate.setFont(StarTrackPlotter.font);
		xyPlot.addAnnotation(annotate);

		// finish time annotation, y-offset if overlaps with start
		boolean overlap = Math.abs(finish.getPolarX() - start.getPolarX()) < 2 * ANNOTATE_OFFSET; 
		int offsetY = (overlap) ?  - ANNOTATE_OFFSET : ANNOTATE_OFFSET;
		annotate = new XYTextAnnotation(strFinish, finish.getPolarX(), finish.getPolarY() + offsetY);
		annotate.setFont(StarTrackPlotter.font);
		xyPlot.addAnnotation(annotate);

		return xyPlot;
	}
}
