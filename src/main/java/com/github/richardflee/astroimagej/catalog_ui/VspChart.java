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

public class VspChart {

	private static final int SCALED_WIDTH = 650;
	private static final double X_LEFT = 9.0;
	private static final double Y_TOP = 97.0;

	private static final double L_WIDTH = 0.5 * SCALED_WIDTH - X_LEFT;
	private static final double L_HEIGHT = L_WIDTH + 1.0;
	
	private static final double X0 = X_LEFT + L_WIDTH;
	private static final double Y0 = Y_TOP + L_HEIGHT;

	private static final int AP_WIDTH = 22;
	
	private double fovAmin = 0.0;
	private FieldObject target = null;
	private Graphics2D g2d = null;

	public VspChart() {

	}

	private BufferedImage loadImage(String urlStr) {
		BufferedImage bimg = null;

		try {
			bimg = ImageIO.read(new URL(urlStr));
		} catch (IOException ex) {
		}
		return bimg;
	}

	private BufferedImage scaleImage(BufferedImage srcImage) {

		int sourceWidth = srcImage.getWidth();
		int scaledHeight = Math.toIntExact(srcImage.getHeight() * SCALED_WIDTH / sourceWidth);

		BufferedImage destImage = new BufferedImage(SCALED_WIDTH, scaledHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = destImage.createGraphics();
		g2d.drawImage(srcImage, 0, 0, SCALED_WIDTH, scaledHeight, null);
		g2d.dispose();

		return destImage;
	}

	private double scaleX(FieldObject fo) {
		double decRad = Math.toRadians(fo.getDecDeg());
		return -15.0 * 60 * 2 * L_WIDTH * Math.cos(decRad) / getFovAmin();
	}
	
	private double scaleY() {
		return -60 * 2 * L_HEIGHT / getFovAmin();
	}
	
	private int chtX(FieldObject fo) {
		FieldObject target = getTarget();
		double raHr = fo.getRaHr();
		double raHr0 = target.getRaHr();
		double chartX = X0 + (raHr - raHr0) * scaleX(fo);
		return (int) (chartX);
	}

	
	private int chtY(FieldObject fo) {
		FieldObject target = getTarget();
		double decDeg = fo.getDecDeg();
		double decDeg0 = target.getDecDeg();
		return (int) (Y0 + (decDeg - decDeg0) * scaleY());
	}
	
	
	

	public double getFovAmin() {
		return fovAmin;
	}

	public void setFovAmin(QueryResult result) {
		this.fovAmin = result.getQuery().getFovAmin();
	}
	
	
	
	
	public FieldObject getTarget() {
		return target;
	}

	public void setTarget(FieldObject target) {
		this.target = target;
	}
	
	

	public Graphics2D getG2d() {
		return g2d;
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
	
	private void drawAperture(FieldObject fo) {
		
		Color color = (fo.isTarget() == true) ? Color.GREEN : Color.red;
		g2d.setColor(color);
		
		int x0 = chtX(fo) - AP_WIDTH / 2;
		int y0 = chtY(fo) - AP_WIDTH / 2;		
		g2d.drawOval(x0, y0, AP_WIDTH, AP_WIDTH);
		g2d.drawString(fo.getApertureId(), (int) (x0 + 1.0 * AP_WIDTH), (int) (y0 + 1.5 * AP_WIDTH));
		
		
	}

	public static void main(String[] args) {

		// result.getFieldObjects().stream().forEach(System.out::println);;
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
		

		// **********************************************************************************************************
		
		VspChart vspChart = new VspChart();
		String urlStr = "https://app.aavso.org/vsp/chart/X26835JN.png?type=chart";

		BufferedImage sourceImage = vspChart.loadImage(urlStr);
		if (sourceImage == null) {
			String statusMessage = String.format("ERROR: Error in downoading chart: %s", urlStr);
			System.out.println(statusMessage);
			return;
		}
		

		BufferedImage scaledImage = vspChart.scaleImage(sourceImage);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.setStroke(new BasicStroke(3));
	    g2d.setFont(new Font("TimesRoman", Font.BOLD, 12));
		
		vspChart.setFovAmin(result);
		vspChart.setG2d(g2d);
		vspChart.setTarget(result.getTargetObject());
		
		FieldObject target = result.getTargetObject();
		vspChart.drawAperture(target);
		
		List<FieldObject> fieldObjects = result.getFieldObjects();
		
		for (int i = 1; i < fieldObjects.size(); i++) {
			FieldObject fo = fieldObjects.get(i);
			vspChart.drawAperture(fo);
			System.out.println(fo.getObjectId());
		}
		vspChart.showVspChart(scaledImage);
	}

}
