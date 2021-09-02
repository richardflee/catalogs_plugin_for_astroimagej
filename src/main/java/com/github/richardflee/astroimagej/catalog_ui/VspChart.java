package com.github.richardflee.astroimagej.catalog_ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.github.richardflee.astroimagej.fileio.ApassFileReader;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Plots VSP chart with apertures centred on selected catalog table records
 */
public class VspChart {
	// chart size
	private static final int SCALED_WIDTH = 650;

	// empirical chart size constants
	// top-left corner chart area
	private static final double X_LEFT = 9.0;
	private static final double Y_TOP = 97.0;

	// semi-width, vertical and horizontal widths appear unequal
	private static final double L_WIDTH = 0.5 * SCALED_WIDTH - X_LEFT;
	private static final double L_HEIGHT = L_WIDTH + 1.0;

	// centre coords
	private static final double X0 = X_LEFT + L_WIDTH;
	private static final double Y0 = Y_TOP + L_HEIGHT;

	// aperture diameter
	private static final int AP_WIDTH = 22;

	// 2D graphics
	private Graphics2D g2d = null;
	private BufferedImage scaledImage = null;

	// query result data
	private double fovAmin = 0.0;
	private FieldObject target = null;
	private String chartUri = null;

	// status message
	private String statusMessage = null;

	/**
	 * Downloads and scales vsp chart
	 * @param result
	 *     query result parameters
	 */
	public VspChart(QueryResult result) {
		this.fovAmin = result.getQuery().getFovAmin();
		this.target = result.getTargetObject();
		this.chartUri = result.getChartUri();

		// download and scale vsp chart as buffered image or null if download fails
		this.scaledImage = loadImage(chartUri);
		if (this.scaledImage == null) {
			String statusMessage = String.format("ERROR: Error in downoading chart: %s", chartUri);
			setStatusMessage(statusMessage);
			return;
		}

		// create graphics object to draw aperture shapes and text
		this.g2d = scaledImage.createGraphics();
		this.g2d.setStroke(new BasicStroke(3));
		this.g2d.setFont(new Font("Consolas", Font.BOLD, 14));
	}

	public void drawChart(List<FieldObject> fieldObjects) {
		// chart download already failed
		if (this.scaledImage == null) {
			return;
		}

		// draw reference apertures
		for (int i = 1; i < fieldObjects.size(); i++) {
			drawAperture(fieldObjects.get(i));
		}

		// displays chart dialog
		showVspChart(scaledImage);
	}

	/*
	 * Downloads and resizes chart; stored as a buffered image
	 * @param chartUri vsp chart uri
	 * @return reference to download vsp chart; null if download fails
	 */
	private BufferedImage loadImage(String chartUri) {
		BufferedImage downloadImage = null;
		try {
			downloadImage = ImageIO.read(new URL(chartUri));
		} catch (IOException ex) {
		}

		// scale image width to SCALED_WIDTH, retain aspect ratio
		int sourceWidth = downloadImage.getWidth();
		int scaledHeight = Math.toIntExact(downloadImage.getHeight() * SCALED_WIDTH / sourceWidth);

		BufferedImage scaledDownloadImage = new BufferedImage(SCALED_WIDTH, scaledHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = scaledDownloadImage.createGraphics();
		g2d.drawImage(downloadImage, 0, 0, SCALED_WIDTH, scaledHeight, null);
		g2d.dispose();

		return scaledDownloadImage;
	}

	/*
	 * Draws a red or green aperture circle, centred on reference or target object
	 * coordinates respectively with adjacent id. Aperture id in form T01, C02, C03
	 * .
	 * @param fo reference to current field object, encapsulates object coordinates
	 */
	private void drawAperture(FieldObject fo) {
		// sets red or green for reference or target object
		Color color = (fo.isTarget() == true) ? Color.GREEN : Color.red;
		g2d.setColor(color);

		// aperture centre, offset from enclosing square
		int x0 = chtX(fo) - AP_WIDTH / 2;
		int y0 = chtY(fo) - AP_WIDTH / 2;
		g2d.drawOval(x0, y0, AP_WIDTH, AP_WIDTH);

		// aperture id label offset right and down
		g2d.drawString(fo.getApertureId(), (int) (x0 + 1.0 * AP_WIDTH), (int) (y0 + 1.5 * AP_WIDTH));
	}

	// maps object ra coordinate to chart x coordinate
	private int chtX(FieldObject fo) {
		double raHr = fo.getRaHr();
		double raHr0 = this.target.getRaHr();
		double chartX = X0 + (raHr - raHr0) * scaleX(fo);
		return (int) (chartX);
	}
	
	// maps object dec coordinate to chart coordinate
	private int chtY(FieldObject fo) {
		double decDeg = fo.getDecDeg();
		double decDeg0 = this.target.getDecDeg();
		return (int) (Y0 + (decDeg - decDeg0) * scaleY());
	}
	
	// scales chart x to object ra in pix/arcmin
	// note dependency on object dec
	private double scaleX(FieldObject fo) {
		double decRad = Math.toRadians(fo.getDecDeg());
		
		// convertion hr -> deg -> arcmin
		// L_WIDTH is semi-width of chart area
		// y increases down direction, dec increases up direction => "-" scale factor
		return -15.0 * 60 * 2 * L_WIDTH * Math.cos(decRad) / getFovAmin();
	}

	private double scaleY() {
		// conversion deg -> arcmin
		// L_HEIGHT is semi-height of chart area
		// x increases left-right, ra increases right-left => "-" scale factor
		return -60 * 2 * L_HEIGHT / getFovAmin();
	}


	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	 public double getFovAmin() {
	 return fovAmin;
	 }
	
	 public void setFovAmin(QueryResult result) {
	 this.fovAmin = result.getQuery().getFovAmin();
	 }


	public void setG2d(Graphics2D g2d) {
		this.g2d = g2d;
	}

	private void showVspChart(BufferedImage scaledImage) {
		JDialog dialog = new JDialog();
		dialog.getContentPane().add(new JLabel(new ImageIcon(scaledImage)));
		dialog.setSize(new Dimension(scaledImage.getWidth(), scaledImage.getHeight()));
		dialog.setVisible(true);

	}

	public static void main(String[] args) {
		double tgtMag0 = 12.345;

		// build default catalog result object, init new result object
		CatalogQuery query = new CatalogQuery();
		QueryResult result = new QueryResult(query);

		// build default CatalogSettngs object, assign to result_settings
		CatalogSettings settings = new CatalogSettings(tgtMag0);
		result.setSettings(settings);

		// compile ref object list from apass file
		ApassFileReader fr = new ApassFileReader();
		List<FieldObject> referenceObjects = fr.runQueryFromFile(query);
		result.appendFieldObjects(referenceObjects);

		result.getQuery().setRaHr(AstroCoords.raHms_To_raHr("06:30:32.80"));
		result.getQuery().setDecDeg(AstroCoords.decDms_To_decDeg("29:40:20.3"));
		result.getQuery().setFovAmin(10.0);
		
		// chart X26835JN: wasp12 / 06:30:32.80 / 29:40:20.3 / 10' fov / maglimit = 18.5 / N- E = up-left
		result.setChartUri("https://app.aavso.org/vsp/chart/X26835JN.png?type=chart");

		// **********************************************************************************************************

		VspChart vspChart = new VspChart(result);
		vspChart.drawChart(result.getFieldObjects());
	}

}
