package com.github.richardflee.astroimagej.fileio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.richardflee.astroimagej.enums.ColumnsEnum;
import com.github.richardflee.astroimagej.enums.RaDecFilesEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;

/**
 * Writes catalog table data to radec file format: 
 * 
 * <p> Block 1: data = astroimagej radec format data
 * to draw apertures on plate solve images </p>
 * 
 * <p>Block 2: data lines encoding catalog table data</p> 
 * 
 * <p>Block 3: data line encoding  catalog query data</p> 
 * 
 * <p>Block 4: text line comprises vsp chart chart uri for current query</p>
 */
public class RaDecFileWriter extends RaDecFileBase {

	// flag data block
	private boolean isDataBlock = true;

	public RaDecFileWriter() {
		super();
		// compile data lines first
		isDataBlock = true;
		
		// first use, create new radec and dss folders in user.dir path
		File dir = new File(System.getProperty("user.dir"), "radec");
		dir.mkdirs();
		dir = new File(System.getProperty("user.dir"), "dss");
		dir.mkdirs();
	}

	/**
	 * Writes radec file to import apertures into astroimagej with filename format
	 * [objectid].[magband].radec.txt <p> Ref:
	 * https://www.astro.louisville.edu/software/astroimagej/ </p>
	 * @param selectedList
	 *     list of target and selected reference objects in user-specified sort
	 *     order
	 * @param query
	 *     parameters of on-line database query
	 * @return message result of file write operation.
	 */
	public String writeRaDecFile(QueryResult result) {
		// converts query data to string list to write to radec file
		List<String> lines = compileRaDecList(result);

		// write new radec file and update message
		File file = getFile(result.getQuery());
		String filePath = file.toString();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
			for (String line : lines) {
				bw.append(line);
			}
		} catch (IOException e) {
			String message = String.format("ERROR: Error saving radec file: %s", filePath);
			setStatusMessage(message);
		}
		String statusMessage = String.format("Saved radec file: %s", filePath);
		setStatusMessage(statusMessage);
		return statusMessage;
	}

	/*
	 * Compiles radec filename pattern <object_id>.<filter>.<fov_amin>.radec.txt
	 * @param query on-line query parameters
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
	 * @param query on-line query parameters
	 * @return file object with path: ./astroimagej/radec/<filename>
	 */
	private File getFile(CatalogQuery query) {
		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "radec");
		File file = new File(dir, compileFilename(query));
		return file;
	}

	/*
	 * Compiles a single data line in astroimagej radec format from a FieldObject
	 * @param fo current target or reference field object
	 * @return single data record ordered: RA, Dec, RefStar, Centroid, Mag <p>If
	 * current parameter is target object, indicate Ref = 0, mag = 99.99, otherwise
	 * ref = 1 and mag = catalog mag for this filter band</p>
	 */
	private String getFieldLine(FieldObject fo) {
		String line = AstroCoords.raHrToRaHms(fo.getRaHr()) + ",";
		line += AstroCoords.decDegToDecDms(fo.getDecDeg()) + ",";
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
	 * @param selectedList list of FieldObjects to convert to write to radec file
	 * @param query catalog query data for this data set
	 * @return data and comment line string array
	 */
	private List<String> compileRaDecList(QueryResult result) {
		List<String> lines = new ArrayList<>();

		List<FieldObject> acceptedList = result.getFieldObjects()
				.stream()
				.filter(p -> p.isAccepted() == true)
				.collect(Collectors.toList());

		// astrominagej radec data block
		// append accepted AND selected records
		lines.add("#RA, Dec, RefStar, Centroid, Mag\n");
		for (FieldObject fo : acceptedList) {
			if (fo.isSelected() == true) {
				String line = getFieldLine(fo) + "\n";
				lines.add(line);
			}
		}
		

		// table block start
		// append accepted records, includes selected & de-selected table records
		lines.add(RaDecFilesEnum.DATA_TABLE_START.getStrVal());
		lines.add("#Ap, ObjectId, RA, Dec, Mag, MagErr, MagDelta, RadSep, Nobs\n");
		for (FieldObject fo : acceptedList) {
			lines.add(compileTableLine(fo));
		}
		// table block end
		lines.add(RaDecFilesEnum.DATA_TABLE_END.getStrVal());

		// query line
		lines.add(RaDecFilesEnum.QUERY_DATA_LINE.getStrVal());
		lines.add(result.getQuery().toFormattedString()[0]); // query item names
		lines.add(result.getQuery().toFormattedString()[1]); // query data

		// chart line
		lines.add(RaDecFilesEnum.CHART_URI_LINE.getStrVal());
		String chartUri = result.getChartUri();
		lines.add(String.format("#%s\n", chartUri));
		return lines;
	}


	public static void main(String[] args) {
		// compile result object from file
		RaDecFileWriter fw = new RaDecFileWriter();
		CatalogQuery query = new CatalogQuery();
		
		double targetMag = 13.579;
		CatalogSettings settings = new CatalogSettings(targetMag);		
		QueryResult result = new QueryResult(query, settings);
		
		// import apass file data 
		String src = "c:/temp/apass_data.txt";
		List<FieldObject> fieldObjects = new ArrayList<>();
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(new File(src)))) {
			while ((line = br.readLine()) != null) {
				FieldObject fo = new FieldObject();
				String[] terms = line.split("\t");

				for (int idx = 0; idx < terms.length; idx++) {
					String term = terms[idx];
					if (idx == ColumnsEnum.OBJECTID_COL.getIndex()) {
						fo.setObjectId(term);
					} else if (idx == ColumnsEnum.RA2000_COL.getIndex()) {
						Double raHr = Double.parseDouble(term);
						fo.setRaHr(raHr);
					} else if (idx == ColumnsEnum.DEC2000_COL.getIndex()) {
						Double decDeg = Double.parseDouble(term);
						fo.setDecDeg(decDeg);
					} else if (idx == ColumnsEnum.MAG_COL.getIndex()) {
						fo.setMag(Double.parseDouble(term));
					} else if (idx == ColumnsEnum.MAG_ERR_COL.getIndex()) {
						fo.setMagErr(Double.parseDouble(term));
					} else if (idx == ColumnsEnum.NOBS_COL.getIndex()) {
						fo.setnObs(Integer.parseInt(term));
					}
				}
				fo.setTarget(false);
				fieldObjects.add(fo);
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//fieldObjects.get(1).setSelected(false);
		FieldObject selecto = fieldObjects.get(1);
		System.out.println(String.format("Deselected star: %s",  selecto.getObjectId()));
		selecto.setSelected(false);
		System.out.println();
		
		result.appendFieldObjects(fieldObjects);
				
		List<String> lines = fw.compileRaDecList(result);		
		fw.writeRaDecFile(result);
		
		System.out.println(fw.getStatusMessage());
		System.out.println();
		lines.stream().forEach(System.out::print);
		
//		// query
//		int matchIndex = fw.getIndex(lines, RaDecFilesEnum.QUERY_DATA_LINE);
//		System.out.println(lines.get(matchIndex + 2));
//		
//		//target
//		matchIndex = fw.getIndex(lines, RaDecFilesEnum.DATA_TABLE_START);
//		System.out.print(lines.get(matchIndex + 2));
//		
//		// 1st reference object
//		System.out.print(lines.get(matchIndex + 3));
//		
//		// last ref object
//		matchIndex = fw.getIndex(lines, RaDecFilesEnum.DATA_TABLE_END);
//		System.out.print(lines.get(matchIndex - 1));
//		System.out.println();
//		
//		// chart uri
//		matchIndex = fw.getIndex(lines, RaDecFilesEnum.CHART_URI_LINE);
//		System.out.print(lines.get(matchIndex + 1));
	}

}
