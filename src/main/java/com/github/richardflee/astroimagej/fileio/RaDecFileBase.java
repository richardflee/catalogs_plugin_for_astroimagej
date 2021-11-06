package com.github.richardflee.astroimagej.fileio;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Base class for radec file reader and writer handles catalog table data.
 * 
 * <p>Data arrays are indexed by ColumnsEnum getIndex() to coordinate
 * table data read / write operations</p>
 */
class RaDecFileBase {

	private String statusMessage = "ERROR: Coding error";

	/*
	 * Returns single row of catalog table data in a comma-delimited string
	 * <p>Evaluates ColumnsEnum getIndex() to assign array indices</p>
	 * @param fo FieldObject single table row data
	 * @return line comprising comma-separated table data
	 */
	protected String compileTableLine(FieldObject fo) {
		// assign terms[ColumnsEnum index] to corresponding FieldObject fo values
		String[] terms = new String[ColumnsEnum.size];
		terms[ColumnsEnum.AP_COL.getIndex()] = String.format("#%s", fo.getApertureId());
		terms[ColumnsEnum.OBJECTID_COL.getIndex()] = String.format("%s", fo.getObjectId());
		terms[ColumnsEnum.RA2000_COL.getIndex()] = String.format("%s", AstroCoords.raHrToRaHms(fo.getRaHr()));
		terms[ColumnsEnum.DEC2000_COL.getIndex()] = String.format("%s", AstroCoords.decDegToDecDms(fo.getDecDeg()));
		terms[ColumnsEnum.MAG_COL.getIndex()] = String.format("%.3f", fo.getMag());
		terms[ColumnsEnum.MAG_ERR_COL.getIndex()] = String.format("%.3f", fo.getMagErr());
		terms[ColumnsEnum.MAG_DIFF_COL.getIndex()] = String.format("%.3f", fo.getDeltaMag());
		terms[ColumnsEnum.DIST_AMIN_COL.getIndex()] = String.format("%.2f", fo.getRadSepAmin());
		terms[ColumnsEnum.NOBS_COL.getIndex()] = String.format("%2d", fo.getnObs());

		// append "\n" last term
		terms[ColumnsEnum.USE_COL.getIndex()] = String.format("%d\n", fo.isSelected() ? 1: 0);
		
		// combines terms in single string with "," separator, omitting separator for
		// the last term
		String line = Arrays.asList(terms).stream().collect(Collectors.joining(","));
		return line;
	}

	/*
	 * Assembles a FieldObject from single table data line. <p>Evaluates ColumnsEnum
	 * getIndex() to assign array indices</p>
	 * @param tableLine single table data row comma-delimited string
	 * @return FieldObject compiled from tableData values
	 */
	protected FieldObject compileFieldObject(String tableLine) {

		FieldObject fo = new FieldObject();
		String[] terms = tableLine.replace(" ", "").split(",");
		
		// strip leading "#" from aperture id
		// assign target flag if Ap = T01
		String apertureId = terms[ColumnsEnum.AP_COL.getIndex()];
		fo.setApertureId(apertureId.replace("#", ""));
		fo.setTarget(apertureId.contains("T"));

		String objectId = terms[ColumnsEnum.OBJECTID_COL.getIndex()];
		fo.setObjectId(objectId);

		String raHms = terms[ColumnsEnum.RA2000_COL.getIndex()];
		fo.setRaHr(AstroCoords.raHmsToRaHr(raHms));

		String decDms = terms[ColumnsEnum.DEC2000_COL.getIndex()];
		fo.setDecDeg(AstroCoords.decDmsToDecDeg(decDms));

		double mag = Double.valueOf(terms[ColumnsEnum.MAG_COL.getIndex()]);
		fo.setMag(mag);

		double magErr = Double.valueOf(terms[ColumnsEnum.MAG_ERR_COL.getIndex()]);
		fo.setMagErr(magErr);

		double deltaMag = Double.valueOf(terms[ColumnsEnum.MAG_DIFF_COL.getIndex()]);
		fo.setDeltaMag(deltaMag);

		double radSepAmin = Double.valueOf(terms[ColumnsEnum.DIST_AMIN_COL.getIndex()]);
		fo.setRadSepAmin(radSepAmin);

		int nObs = Integer.valueOf(terms[ColumnsEnum.NOBS_COL.getIndex()]);
		fo.setnObs(nObs);
		
		int selectNum = Integer.valueOf(terms[ColumnsEnum.USE_COL.getIndex()]);
		fo.setSelected(selectNum == 1);

		return fo;
	}

	// status message getter , setter

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static void main(String[] args) {
		String[] dataLines = { 
				"#T01, wasp12, 06:30:32.80, +29:40:20.27, 10.000, 0.567, 0.000, 0.00,  1, 1",
				"#C02, star04, 06:30:31.03, +29:41:18.43, 9.120, 0.090, -0.880, 1.04,  4, 1", 
				"#C03, star05, 06:30:31.03, +29:41:18.43, 9.120, 0.090, -0.880, 1.04,  3, 0" 
				};

		RaDecFileBase f = new RaDecFileBase();

		FieldObject fo0 = f.compileFieldObject(dataLines[0]);
		FieldObject fo1 = f.compileFieldObject(dataLines[1]);
		FieldObject fo2 = f.compileFieldObject(dataLines[2]);
		
		
		System.out.println(fo0.toString());
		System.out.println();
		System.out.println(fo1.toString());
		System.out.println();
		System.out.println(fo2.toString());
	}

}
