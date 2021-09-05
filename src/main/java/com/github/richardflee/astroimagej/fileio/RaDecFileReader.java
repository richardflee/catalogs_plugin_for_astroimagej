package com.github.richardflee.astroimagej.fileio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.richardflee.astroimagej.enums.RaDecFilesEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.CatalogSettings;
import com.github.richardflee.astroimagej.query_objects.FieldObject;
import com.github.richardflee.astroimagej.query_objects.QueryResult;

/**
 * Imports radec file data and populates catlog table and creates new query
 * object. Radec file format: <p> Block 1: data = astroimagej radec format data
 * to draw apertures on plate solve images </p> <p> Block 2: comment = header +
 * selected catalog table rows </p> <p> Block 3: comment = header + row of
 * catalog query data </p> <p> Comment lines have leading char "#". A single "#"
 * denotes break between blocks. </p>
 */
public class RaDecFileReader extends RaDecFileBase {

	private String radecFilepath = null;
	// private double targetMag;

	public RaDecFileReader() {
	}

	/**
	 * Compiles a QueryResult object from user-selected radec file. <p>Determines
	 * sort order from radec table data</p>
	 * @return QueryResult object in distance or mag difference sort order
	 */
	public QueryResult importRaDecResult() {
		// open file dialog, file = null => cancel
		File file = radecFileDialog();
		if (file == null) {
			String statusMessage = "Cancel pressed, no file selected";
			setStatusMessage(statusMessage);
			return null;
		}
		// map radec file contents into line array
		List<String> radecLines = loadRaDecLines(file);

		// extract catalog query data
		CatalogQuery radecQuery = getRaDecQuery(radecLines);

		// extract target object, first row in table data
		FieldObject radecTarget = getRaDecTargetObject(radecLines);

		// extract remainder of table data into field object list
		List<FieldObject> radecFieldObjects = getRaDecFieldObjects(radecLines);
		
		// extract chart uri
		String chartUri = getChartUriLine(radecLines);

		// CatalogSettings object, initialised with radec targetMag value
		// auto-selects sort radio button based on table sort order
		double targetMag = radecTarget.getMag();
		CatalogSettings radecSettings = getRaDecSettings(radecFieldObjects, targetMag);

		// compile and return QueryResult object
		QueryResult radecResult = new QueryResult(radecQuery, radecSettings);
		radecResult.appendFieldObjects(radecFieldObjects);
		
		// add chart uri
		radecResult.setChartUri(chartUri);

		String statusMessage = String.format("Imported radec file: %s", file.getAbsoluteFile());
		setStatusMessage(statusMessage);
		return radecResult;
	}
	
	/*
	 * Compiles CatalogQuery object from data line embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return radec file catalog query object
	 */
	private CatalogQuery getRaDecQuery(List<String> radecLines) {
		String dataLine = matchQueryLine(radecLines);
		return CatalogQuery.fromFormattedString(dataLine);
	}
	
	/*
	 * Compiles target object from data line embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return radec file target object, first table line
	 */
	private FieldObject getRaDecTargetObject(List<String> radecLines) {
		String dataLine = matchTargetLine(radecLines);
		return compileFieldObject(dataLine);
	}
	
	
	/*
	 * Compiles list of reference field objects data lines embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return radec file table lines comprising field object data
	 */
	private List<FieldObject> getRaDecFieldObjects(List<String> radecLines) {
		List<FieldObject> tableRows = new ArrayList<>();
		List<String> tableLines = matchTableLines(radecLines);
		for (String line : tableLines) {
			FieldObject fo = compileFieldObject(line);
			tableRows.add(fo);
		}
		return tableRows;
	}
	
	/*
	 * Finds chart uri text embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return chart uri data
	 */
	private String getChartUriLine(List<String> radecLines) {
		int matchIndex = matchIndex(radecLines, RaDecFilesEnum.CHART_URI_LINE);
		// data 1 row below marker
		return radecLines.get(matchIndex + 1);
	}
	
	/*
	 * Finds line of CatalogQuery data embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return query data line
	 */
	private String matchQueryLine(List<String> radecLines) {
		int matchIndex = matchIndex(radecLines, RaDecFilesEnum.QUERY_DATA_LINE);
		// query data 2 rows below marker
		return radecLines.get(matchIndex + 2);
	}
	
	/*
	 * Finds line of target object data embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return target object data line
	 */
	private String matchTargetLine(List<String> radecLines) {
		int matchIndex = matchIndex(radecLines, RaDecFilesEnum.DATA_TABLE_START);
		// target data 2 rows below marker
		return radecLines.get(matchIndex + 2);
	}
	
	
	/*
	 * Finds line array of field object data embedded in radec line array
	 * 
	 * @param radecLines text array mapped from selected radec file
	 * @return text array comprising field object data
	 */
	private List<String> matchTableLines(List<String> radecLines) {
		// start index 3 row below DATA_TABLE_START 
		int matchIndex = matchIndex(radecLines, RaDecFilesEnum.DATA_TABLE_START);
		int startIndex = matchIndex + 3;
		
		// end index same row as DATA_TABLE_END
		matchIndex = matchIndex(radecLines, RaDecFilesEnum.DATA_TABLE_END);
		int endIndex = matchIndex;
		return radecLines.subList(startIndex, endIndex);
	}
	

	/*
	 * Opens file dialog configured for radec folder and file type
	 * @return file reference to selected file, or null if Cancel pressed
	 */
	private File radecFileDialog() {
		// configures file chooser dialog start folder and file type
		File file = new File(System.getProperty("user.dir"), "radec");
		JFileChooser jfc = new JFileChooser(file);
		jfc.setDialogTitle("Select radec file");
		jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RaDec files (*.txt)", "txt");
		jfc.addChoosableFileFilter(filter);

		// sets file object to selected text file or null if Cancel
		file = null;
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = jfc.getSelectedFile();
			setRadecFilepath(file.getAbsolutePath());
		}
		return file;
	}

	/*
	 * Converts user selected radec text file to text list
	 * @param file reference to selected file
	 * @return text file contents copied to String array
	 */
	private List<String> loadRaDecLines(File file) {
		List<String> lines = new ArrayList<>();

		Path path = file.toPath();
		try (Stream<String> stream = Files.lines(path)) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			// error statusMessage
			String statusMessage = String.format("ERROR: Error reading radec file: %s", path.toString());
			setStatusMessage(statusMessage);
		}
		return lines;
	}

	

	/*
	 * Compiles a CatalogSetting object with radec target mag and infers table sort
	 * order.
	 * @param radecFieldObjects list of reference field objects sorted relative to
	 * target object
	 * @param targetMag radec targe mag value
	 * @return QueryResult object encapsulating contents of user selected radec file
	 */
	private CatalogSettings getRaDecSettings(List<FieldObject> radecFieldObjects, double targetMag) {
		// initialise default settings
		CatalogSettings settings = new CatalogSettings();

		// set target mag from field value
		settings.setTargetMagSpinnerValue(targetMag);

		// sets distance and delta mag sort settings inferred from fieldObjects list
		boolean sortedByDeltaMag = isSortedByDeltaMag(radecFieldObjects);
		settings.setDeltaMagRadioButtonValue(sortedByDeltaMag);
		settings.setDistanceRadioButtonValue(!sortedByDeltaMag);

		return settings;
	}

	/**
	 * Tests if |mag diff| is sorted in ascending order
	 * @param radecFieldObjects
	 *     sorted list of field objeccts
	 * @return true if sorted in order of increasing |mag diff|, false otherwise
	 */
	private boolean isSortedByDeltaMag(List<FieldObject> radecFieldObjects) {
		// delta mag sort
		boolean sortedByDeltaMag = true;
		for (int idx = 1; idx < radecFieldObjects.size(); idx++) {
			double currentDeltaMag = Math.abs(radecFieldObjects.get(idx).getDeltaMag());
			double previousDeltaMag = Math.abs(radecFieldObjects.get(idx - 1).getDeltaMag());
			sortedByDeltaMag = sortedByDeltaMag && (currentDeltaMag >= previousDeltaMag);
		}
		return sortedByDeltaMag;
	}


	public String getRadecFilepath() {
		return radecFilepath;
	}

	public void setRadecFilepath(String radecFilepath) {
		this.radecFilepath = radecFilepath;
	}

	/*
	 * Finds first line in line array matching enum text
	 * 
	 *  Example: en=RaDecFilesEnum.DATA_TABLE_START matches #*** DATA_TABLE_START 
	 * 
	 * @param lines text array contents of selected rade file
	 * @param en enum specifying line ot match
	 * @return matching index in lines array; -1 if no match found 
	 */
	private int matchIndex(List<String> lines, RaDecFilesEnum en) {
		int matchIndex = -1;
		for (int idx = 0; idx < lines.size(); idx++) {
			if (lines.get(idx).contains(en.toString()) == true) {
				matchIndex = idx;
				break;
			}
		}
		return matchIndex;
	}

	public static void main(String[] args) {
		RaDecFileReader fr = new RaDecFileReader();

		String path = "C:\\Users\\rlee1\\eclipse-workspace\\astroimagej-plugin\\catalogs_plugin\\radec\\wasp_12.V.060.radec.txt";
		File file = new File(path);
		List<String> lines = fr.loadRaDecLines(file);

		// query
		int matchIndex = fr.matchIndex(lines, RaDecFilesEnum.QUERY_DATA_LINE);
		String dataLine = lines.get(matchIndex + 2);
		CatalogQuery query = CatalogQuery.fromFormattedString(dataLine);
//		System.out.println(query.toString());
//		System.out.println();

		// table data
		matchIndex = fr.matchIndex(lines, RaDecFilesEnum.DATA_TABLE_START);
		int startIndex = matchIndex + 2;

		matchIndex = fr.matchIndex(lines, RaDecFilesEnum.DATA_TABLE_END);
		int endIndex = matchIndex;
		List<String> tableLines = lines.subList(startIndex, endIndex);

		// target
		String targetLine = tableLines.get(0);
		FieldObject target = fr.compileFieldObject(targetLine);
		double targetMag = target.getMag();
//		System.out.println((target.toString()));
//		System.out.println(targetMag);
//		System.out.println();
		
		// ref field objects
		List<FieldObject> fieldObjects = new ArrayList<>();
		for (String line : tableLines) {
			FieldObject fo = fr.compileFieldObject(line);
			if (fo.isTarget() == false) {
				fieldObjects.add(fo);
			}
		}
//		fieldObjects.stream().forEach(System.out::println);
		//System.out.println();
		
		// settings
		CatalogSettings settings = fr.getRaDecSettings(fieldObjects, targetMag);
		
		// chart uri
		matchIndex = fr.matchIndex(lines, RaDecFilesEnum.CHART_URI_LINE);
		dataLine = lines.get(matchIndex + 1);
		String chartUri = dataLine.replaceAll("#", "");
//		System.out.println(chartUri);
//		System.out.println();
		
		// compile query result
		QueryResult result = new QueryResult(query, settings);
		result.appendFieldObjects(fieldObjects);
		result.setSettings(settings);
		result.setChartUri(chartUri);
		System.out.println(result.toString());
		
		QueryResult radecResult = fr.importRaDecResult();
		System.out.println(radecResult.toString());
	}

}
