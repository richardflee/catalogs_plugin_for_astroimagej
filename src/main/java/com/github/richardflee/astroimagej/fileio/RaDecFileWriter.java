package com.github.richardflee.astroimagej.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Writes catalog table data to radec file format:
 * <p>Block 1: data = astroimagej radec format data to draw apertures on plate solve images</p>
 * <p>Block 2: comment = header + selected catalog table rows</p>
 * <p>Block 3: comment = header + row of catalog query data</p>
 * <p>Comment lines have leading char "#". A single "#" denotes break between blocks.</p>
 */
public class RaDecFileWriter extends AbstractRaDecFile {

	// flag data block
	private boolean isDataBlock = true;

	// result of of operation
	private String statusMessage = null;

	public RaDecFileWriter() {
		// compile data lines first
		isDataBlock = true;
	}

	/**
	 * Writes radec file to import apertures into astroimagej with filename format
	 * [objectid].[magband].radec.txt
	 * <p>
	 * Ref: https://www.astro.louisville.edu/software/astroimagej/
	 * </p>
	 * 
	 * @param selectedList list of target and selected reference objects in
	 *                     user-specified sort order
	 * @param query        parameters of on-line database query
	 * @return message  result of file write operation. 
	 */
	public String writeRaDecFile(List<FieldObject> selectedList, CatalogQuery query) {
		// converts query data to string list to write to radec file
		List<String> lines = compileRaDecList(selectedList, query);

		// write new radec file and update message
		File file = getFile(query);
		String filePath = file.toString();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
			for (String line : lines) {
				bw.append(line);
			}
		} catch (IOException e) {
			String message = String.format("ERROR: Error saving radec file: %s", filePath);
			setStatusMessage(message);
		}
		String message = String.format("Saved radec file: %s", filePath);
		setStatusMessage(message);
		return message;
	}

	/*
	 * Compiles radec filename pattern <object_id>.<filter>.<fov_amin>.radec.txt
	 * 
	 * @param query on-line query parameters
	 * 
	 * @return formatted filename <objectid>.<magband>.<fov_amin>.radec.txt
	 */
	private String compileFilename(CatalogQuery query) {
		String filename = String.join(".", Arrays.asList(query.getObjectId(), query.getMagBand(),
				String.format("%03d", query.getFovAmin().intValue()), "radec.txt"));
		return filename.replace(" ", "_");
	}

	/*
	 * Compiles file object to radec file path, ./astroimagej/radec. <p>Creates new
	 * folders as necessary</p>
	 * 
	 * @param query on-line query parameters
	 * 
	 * @return file object with path: ./astroimagej/radec/<filename>
	 */
	private File getFile(CatalogQuery query) {
		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "radec");
		dir.mkdirs();
		File file = new File(dir, compileFilename(query));
		return file;
	}

	/*
	 * Compiles a single data line in astroimagej radec format from a FieldObject
	 * 
	 * @param fo current target or reference field object
	 * 
	 * @return single data record ordered: RA, Dec, RefStar, Centroid, Mag <p>If
	 * current parameter is target object, indicate Ref = 0, mag = 99.99, otherwise
	 * ref = 1 and mag = catalog mag for this filter band</p>
	 */
	private String getFieldLine(FieldObject fo) {
		String line = AstroCoords.raHr_To_raHms(fo.getRaHr()) + ",";
		line += AstroCoords.decDeg_To_decDms(fo.getDecDeg()) + ",";
		if (isDataBlock) {
			line += fo.isTarget() ? "0,1,99.99" : String.format("1,1,%.3f", fo.getMag());
			isDataBlock = false;
		} else {
			line += String.format("1,1,%.3f", fo.getMag());
		}
		return line;
	}

	/*
	 * Compiles list of strings to write to radec file. List comprises 3 blocks
	 * separated by single char '#'. Comment lines start with '#' <p>Block 1: data
	 * lines: astroimagej radec format</p> <p>Block 2: comment lines: catalog table
	 * data</p> <p>Block 3: comment line: query data</p>
	 * 
	 * @param selectedList list of FieldObjects to convert to write to radec file
	 * 
	 * @param query catalog query data for this data set
	 * 
	 * @return data and comment line string array
	 */
	private List<String> compileRaDecList(List<FieldObject> selectedList, CatalogQuery query) {
		List<String> lines = new ArrayList<>();

		// data block
		lines.add("#RA, Dec, RefStar, Centroid, Mag\n");
		for (FieldObject fo : selectedList) {
			String line = getFieldLine(fo) + "\n";
			lines.add(line);
		}

		// table block
		lines.add("#\n#Ap, ObjectId, RA, Dec, Mag, MagErr, MagDelta, RadSep, Nobs\n");
		for (FieldObject fo : selectedList) {
			lines.add(compileTableLine(fo));
		}

		// query block
		lines.add(query.toFormattedString()[0]); // query item names
		lines.add(query.toFormattedString()[1]); // query data
		return lines;
	}

	// status line message gett & setter
	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public static void main(String[] args) {
		// compile result object from file
//		ApassFileReader fr = new ApassFileReader();
//		CatalogQuery query = new CatalogQuery();
//		QueryResult result = fr.runQueryFromFile(query);
//
//		RaDecFileWriter fw = new RaDecFileWriter();
//
//		List<FieldObject> selectedList = result.getFieldObjects();
//		for (FieldObject fo : selectedList) {
//			System.out.println(fw.compileTableLine(fo));
//		}
	}
}
