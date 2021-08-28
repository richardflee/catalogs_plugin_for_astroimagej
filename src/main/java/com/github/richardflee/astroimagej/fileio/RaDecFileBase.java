package com.github.richardflee.astroimagej.fileio;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Base class for radec file reader and writer handles catalog table data.
 * <p>Data arrays are indexed by ColumnsEnum getIndex() to maintain sync between 
 * table data read / write operations</p>
 */
class RaDecFileBase {
	
	// result of of operation
		private String statusMessage = null;

	/*
	 * Returns single row of catalog table data in a comma-delimited string
	 * <p>Evaluates ColumnsEnum getIndex() to assign array indices</p>
	 * 
	 * @param fo FieldObject single table row data
	 * @return line comprising comma-separated table data 
	 */
	protected String compileTableLine(FieldObject fo) {
		// assign terms[ColumnsEnum index] to corresponding FieldObject fo values
		String[] terms = new String[ColumnsEnum.size - 1];		
		terms[ColumnsEnum.AP_COL.getIndex()] = String.format("#%s", fo.getApertureId());
		terms[ColumnsEnum.OBJECTID_COL.getIndex()] = String.format("%s", fo.getObjectId());
		terms[ColumnsEnum.RA2000_COL.getIndex()] = String.format("%s", AstroCoords.raHr_To_raHms(fo.getRaHr()));
		terms[ColumnsEnum.DEC2000_COL.getIndex()] = String.format("%s", AstroCoords.decDeg_To_decDms(fo.getDecDeg()));
		terms[ColumnsEnum.MAG_COL.getIndex()] = String.format("%.3f", fo.getMag());
		terms[ColumnsEnum.MAG_ERR_COL.getIndex()] = String.format("%.3f", fo.getMagErr());
		terms[ColumnsEnum.MAG_DIFF_COL.getIndex()] = String.format("%.3f", fo.getDeltaMag());
		terms[ColumnsEnum.DIST_AMIN_COL.getIndex()] = String.format("%.2f", fo.getRadSepAmin());
		
		// append "\n" last term
		terms[ColumnsEnum.NOBS_COL.getIndex()] = String.format("%2d\n", fo.getnObs());
		
		// combines terms in single string with "," separator, omitting separator for the last term
		String line = Arrays.asList(terms).stream().collect(Collectors.joining(","));
		return line;
	}
	
	/*
	 * Assembles a FieldObject from single table data line.
	 * <p>Evaluates ColumnsEnum getIndex() to assign array indices</p>
	 * 
	 * @param tableLine single table data row comma-delimited string
	 * @return FieldObject compiled from tableData values
	 */
	protected FieldObject compileFieldObject(String tableLine) {
		
		FieldObject fo = new FieldObject();
		String[] terms = tableLine.replace(" ", "").split(",");
		
		String apertureId = terms[ColumnsEnum.AP_COL.getIndex()];
		fo.setApertureId(apertureId);
		
		String objectId = terms[ColumnsEnum.OBJECTID_COL.getIndex()];
		fo.setObjectId(objectId);
		
		String raHms = terms[ColumnsEnum.RA2000_COL.getIndex()];
		fo.setRaHr(AstroCoords.raHms_To_raHr(raHms));
		
		String decDms = terms[ColumnsEnum.DEC2000_COL.getIndex()];
		fo.setDecDeg(AstroCoords.decDms_To_decDeg(decDms));
		
		double mag = Double.valueOf(terms[ColumnsEnum.MAG_COL.getIndex()]);
		fo.setMag(mag);
				
		double magErr = Double.valueOf(terms[ColumnsEnum.MAG_ERR_COL.getIndex()]);
		fo.setMagErr(magErr);
		
		double deltaMag = Double.valueOf(terms[ColumnsEnum.MAG_DIFF_COL.getIndex()]);
		fo.copyDeltaMag(deltaMag);
		
		double radSepAmin = Double.valueOf(terms[ColumnsEnum.DIST_AMIN_COL.getIndex()]);
		fo.setRadSepAmin(radSepAmin);
		
		int nObs = Integer.valueOf(terms[ColumnsEnum.NOBS_COL.getIndex()]);
		fo.setnObs(nObs);
		
		// set boolean states
		fo.setSelected(true);
		boolean isTarget = apertureId.contains("T");
		fo.setTarget(isTarget);
		
		return fo;
	}
	
	
	//status message getter , setter
	
	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static void main(String[] args) {
		String[] dataLines = 
			{"#T01, wasp12, 06:30:32.80, +29:40:20.27, 10.000, 0.567, 0.000, 0.00,  1",
			  "#C02, star04, 06:30:31.03, +29:41:18.43, 9.120, 0.090, -0.880, 1.04,  4"					
			};
		
		RaDecFileBase f = new RaDecFileBase();
		
		FieldObject fo0 = f.compileFieldObject(dataLines[0]);
		FieldObject fo1 = f.compileFieldObject(dataLines[1]);
		System.out.println(fo0.toString());
		System.out.println(fo1.toString());
	}
}
