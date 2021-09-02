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
	private double targetMag;

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
		FieldObject radecTarget = getRaDecFieldObjects(radecLines, true).get(0);

		// extract remainder of table data into field object list
		List<FieldObject> radecFieldObjects = getRaDecFieldObjects(radecLines, false);

		// CatalogSettings object, initialised with radec targetMag value
		// auto-selects sort radio button based on table sort order
		this.targetMag = radecTarget.getMag();
		CatalogSettings radecSettings = getRaDecSettings(radecFieldObjects);

		// compile and return QueryResult object
		QueryResult radecResult = new QueryResult(radecQuery);
		radecResult.appendFieldObjects(radecFieldObjects);
		radecResult.setSettings(radecSettings);

		String statusMessage = String.format("Imported radec file: %s", file.getAbsoluteFile());
		setStatusMessage(statusMessage);
		return radecResult;
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
	 * Compiles a CatalogQuery object from block 3 radec data
	 * @return CatalogQuery object
	 */
	private CatalogQuery getRaDecQuery(List<String> radecLines) {
		// locate single data line and convert to a new query object
		String dataLine = getQueryLine(radecLines);
		return CatalogQuery.fromFormattedString(dataLine);
	}

	/*
	 * If isTarget is true, returns target field object in a single element array,
	 * otherwise returns array of reference field objects
	 * @param radecLines text array comprising radec file contents
	 * @param isTarget flag to return single target or multiple reference field
	 * objects
	 * @return array list containing eithe target field object or a list of
	 * reference field objects
	 */
	private List<FieldObject> getRaDecFieldObjects(List<String> radecLines, boolean isTarget) {
		List<FieldObject> radecRows = new ArrayList<>();

		List<String> tableLines = getTableLines(radecLines);
		for (String line : tableLines) {
			FieldObject row = compileFieldObject(line);
			if (row.isTarget() == isTarget) {
				radecRows.add(row);
			}
		}
		return radecRows;
	}

	/*
	 * Compiles a CatalogSetting object with radec target mag and infers table sort order.
	 * @param radecFieldObjects list of reference field objects sorted relative to target object
	 * @param targetMag radec targe mag value
	 * @return QueryResult object encapsulating contents of user selected radec file
	 */
	private CatalogSettings getRaDecSettings(List<FieldObject> radecFieldObjects) {
		// initialise default settings
		CatalogSettings settings = new CatalogSettings();

		// set target mag from field value
		settings.setTargetMagSpinnerValue(this.targetMag);

		// sets distance and delta mag sort settings inferred from fieldObjects list
		boolean sortedByDeltaMag = isSortedByDeltaMag(radecFieldObjects);
		settings.setDeltaMagRadioButtonValue(sortedByDeltaMag);
		settings.setDistanceRadioButtonValue(!sortedByDeltaMag);

		return settings;
	}

	/**
	 * Tests if |mag diff| is sorted in ascending order 
	 * 
	 * @param radecFieldObjects sorted list of field objeccts
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

	/*
	 * Extracts radec block 2 table data in text array
	 * @param lines text array comprising full radec data set
	 * @return text array comprising radec table data set
	 */
	private List<String> getTableLines(List<String> allTableLines) {
		int idx = 0;
		// first "#" marks start of radec block 2
		while (!(allTableLines.get(idx).equals("#"))) {
			idx++;
		}
		// fromIndex marks start of table data, skips marker and header lines
		int fromIndex = idx + 2;

		// reverse search for 2nd "#" char, marks end of block 2 data
		idx = allTableLines.size() - 1;
		while (!(allTableLines.get(idx).equals("#"))) {
			idx--;
		}
		int toIndex = idx;
		return allTableLines.subList(fromIndex, toIndex);
	}

	/*
	 * Extracts radec block 3 table data in single text line
	 * @param lines text array comprising full radec data set
	 * @return text text line compring radec query data set
	 */
	private String getQueryLine(List<String> allTableLines) {
		// reverse search to find last "#" marker
		int idx = allTableLines.size() - 1;
		while (!(allTableLines.get(idx).equals("#"))) {
			idx--;
		}
		// skips marker and header and returns data line
		int dataIndex = idx + 2;
		return allTableLines.get(dataIndex);
	}

	
	public String getRadecFilepath() {
		return radecFilepath;
	}
	
	public void setRadecFilepath(String radecFilepath) {
		this.radecFilepath = radecFilepath;
	}
	
	public static void main(String[] args) {
		RaDecFileReader fr = new RaDecFileReader();
		QueryResult result = fr.importRaDecResult();
		System.out.println(result.toString());
	}


}
