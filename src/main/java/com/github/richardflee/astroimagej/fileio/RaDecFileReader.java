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
 * object. Radec file format:
 * 
 * <p> Block 1: data = astroimagej radec format data to draw apertures on plate
 * solve images </p>
 * 
 * <p> Block 2: comment = header + selected catalog table rows </p>
 * 
 * <p> Block 3: comment = header + row of catalog query data </p>
 * 
 * <p> Comment lines have leading char "#". A single "#" denotes break between
 * blocks. </p>
 */
public class RaDecFileReader extends RaDecFileBase {

	// private List<String> lines = null;
	private String radecFilepath = null;
	// private boolean raDecFileSelected = false;

	/**
	 * Opens file dialog in ./astroimgej/radec folder with txt file filter. <p> If a
	 * file is selected, sets raDecFileSelected flag true, opens selected file and
	 * converts to text array. </p>
	 * 
	 * @throws IOException pass file error message to ActionHandler
	 *                     doImportRaDecFile method
	 */
	public RaDecFileReader() {
	}

	public QueryResult readRaDecData() {
		// radec file into line array
		List<String> radecLines = null;

		// open file dialog, file = null => cancel
		// sets field raDecFileSelected flag = true if user selects file in dialog
		File file = radecFileDialog();
		if (file == null) {
			String statusMessage = "Cancel pressed, no file selected";
			setStatusMessage(statusMessage);
			return null;
		}

		radecLines = loadRaDecLines(file);

		// extracts query data from the last array line
		CatalogQuery query = getQueryData(radecLines);

		// initialise new result and add radec records
		QueryResult result = new QueryResult(query);
		// block 2 radec data
		List<String> resultLines = getResultLines(radecLines);
		for (String resultLine : resultLines) {
			FieldObject fo = compileFieldObject(resultLine);
			if (fo.isTarget()) {
				result.setTargetObject(fo);
			} else {
				// fo.setApertureId(fo.getApertureId().replace("#", ""));
				result.getFieldObjects().add(fo);
			}
		}

		result.radecSettings(new CatalogSettings());

		String statusMessage = String.format("Imported radec file: %s", file.getAbsoluteFile());
		setStatusMessage(statusMessage);
		// setStatusMessage(statusMessage);

		return result;
	}

	/**
	 * Compiles a QueryResult object from blocks 2 and 3 radec data
	 * 
	 * @return catalog table data mapped to a QueryResult object
	 */

	/**
	 * Compiles a CatalogQuery object from block 3 radec data
	 * 
	 * @return CatalogQuery object
	 */
	private CatalogQuery getQueryData(List<String> lines) {
		// locate single data line and convert to a new query object
		String dataLine = getQueryLine(lines);
		return CatalogQuery.fromFormattedString(dataLine);
	}

	/*
	 * Converts user selected radec text file to text list
	 * 
	 * @param file reference to selected file
	 * 
	 * @return text file contents copied to String array
	 */
	private List<String> loadRaDecLines(File file) {
		List<String> lines = new ArrayList<>();

		Path path = file.toPath();
		try (Stream<String> stream = Files.lines(path)) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			// handle status line eror mmessage in doImportRaDec
			String statusMessage = String.format("ERROR: Error reading radec file: %s", path.toString());
			setStatusMessage(statusMessage);
		}
		return lines;
	}

	/*
	 * Opens file dialog configured for radec folder and file type
	 * 
	 * @return file reference to selected file, or null if Cancel pressed
	 */
	private File radecFileDialog() {
//		// sets ui theme
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception ex) {
//			System.err.println("Failed to initialize Windows Look-Feel");
//		}

		// configures file chooser dialog start folder and file type
		File file = new File(System.getProperty("user.dir"), "radec");
		JFileChooser jfc = new JFileChooser(file);
		jfc.setDialogTitle("Select radec file");
		jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("RaDec files (*.txt)", "txt");
		jfc.addChoosableFileFilter(filter);

		// sets file object to selected text file or null if Cancel
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = jfc.getSelectedFile();
			setRadecFilepath(file.getAbsolutePath());
		} else {
			file = null;
		}
		return file;
	}

	/*
	 * Extracts radec block 2 table data in text array
	 * 
	 * @param lines text array comprising full radec data set
	 * 
	 * @return text array comprising radec table data set
	 */
	private List<String> getResultLines(List<String> allTableLines) {
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
	 * 
	 * @param lines text array comprising full radec data set
	 * 
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

	// radec filepath getter
	public String getRadecFilepath() {
		return radecFilepath;
	}

	public void setRadecFilepath(String radecFilepath) {
		this.radecFilepath = radecFilepath;
	}

	public static void main(String[] args) {
		RaDecFileReader fr = new RaDecFileReader();

		QueryResult result = fr.readRaDecData();
		if (result != null) {
			result.getFieldObjects().stream().forEach(System.out::println);
			System.out.println();

			CatalogQuery query = result.getQuery();
			System.out.println(query.toString());
		} else {
			System.out.println("cancel pressed");
		}
	}

}
