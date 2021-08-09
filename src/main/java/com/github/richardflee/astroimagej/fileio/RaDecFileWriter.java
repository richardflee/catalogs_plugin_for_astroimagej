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
import com.github.richardflee.astroimagej.query_objects.QueryResult;
import com.github.richardflee.astroimagej.utils.AstroCoords;

public class RaDecFileWriter {

	private boolean isDataBlock;
	
	public RaDecFileWriter() {
		// compile data lines first
		isDataBlock = true;
	}
	
	
	/**
	 * Writes radec file to import apertures into astroimagej with filename format [objectid].[magband].radec.txt
	 * <p> Ref: https://www.astro.louisville.edu/software/astroimagej/</p>
	 * 
	 * @param selectedList list of target and selected reference objects in user-specified sort order 
	 * @param query parameters of on-line database query
	 * @return message with result of file write operation.
	 */
	public String writeRaDecFile(List<FieldObject> selectedList, CatalogQuery query) {

		// converts query data to string list to write to radec file
		List<String> lines = compileRaDecList(selectedList, query);
		
		// write new radec file and update message
		File file = getFile(query);
		String filePath = file.toString();
		String message = String.format("Radec file: %s already exists", filePath);
		if (!file.exists()) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				for (String line : lines) {
					bw.append(line);
				}
				message = String.format("Saved radec file: %s", filePath);
			} catch (IOException e) {
				message = String.format("Error in writing file: %s", filePath);
			}
		}
		return message +"\n";
	}

	/*
	 * Compiles radec filename
	 * 
	 * @param query on-line query parameters
	 * @return formatted filename <objectid>.<magband>.<fov_amin>.radec.txt
	 */
	private String compileFilename(CatalogQuery query) {
		String filename = String.join(".", Arrays.asList(query.getObjectId(), query.getMagBand(),
				String.format("%03d", query.getFovAmin().intValue()), "radec.txt"));
		return filename.replace(" ", "_");
	}

	/*
	 * Compiles file object to radec file path
	 * 
	 * @param query on-line query parameters
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
	 * Compiles a single data line in astroimagej radec format from FieldObject fo
	 * 
	 * @param fo current target or reference field object
	 * @return single data record ordered: RA, Dec, RefStar, Centroid, Mag 
	 */
	private String getFieldLine(FieldObject fo) {
		String line = AstroCoords.raHr_To_raHms(fo.getRaHr()) + ", ";
		line += AstroCoords.decDeg_To_decDms(fo.getDecDeg()) + ", ";
		if (isDataBlock) {
			line += fo.isTarget() ? "0, 1, 99.99" : String.format("1, 1, %.3f", fo.getMag());
			isDataBlock = false;
		} else {
			line += String.format("1, 1, %.3f", fo.getMag());
		}
		
		return line;
	}
	
	
	/*
	 * Compiles list of strings to write to radec file. List comprises 3 blocks separated by single char '#'. 
	 * Comment lines start with '#'
	 * 
	 * <p>Block 1: data lines in astroimagej radec format</p>
	 * <p>Block 2: comment line consisting of data line + aperture id, object id and mag error</p>
	 * <p>Block 3: comment line consisting of query data</p>
	 * 
	 * @param sortedFilteredList list of FieldObjects to convert to write to radec file
	 * @param query catalog query data for this data set
	 * @return data and comment line string array  
	 */
	private List<String> compileRaDecList(List<FieldObject> sortedFilteredList, CatalogQuery query) {
		List<String> lines = new ArrayList<>();

		// data block
		lines.add("#RA, Dec, RefStar, Centroid, Mag\n");
		for (FieldObject fo : sortedFilteredList) {
			String line = getFieldLine(fo)+ "\n";
			lines.add(line);
		}		
		lines.add("#\n#Ap, Auid, RA, Dec, RefStar, Centroid, Mag, MagErr\n");
		
		// comment block
		for (FieldObject fo : sortedFilteredList) {
			String line = String.format("#%s, %s, " , fo.getApertureId(), fo.getObjectId());
			line += getFieldLine(fo);
			line += String.format(", %.3f\n", fo.getMagErr());
			lines.add(line);
		}
		
		// query block
		lines.add(query.toFormattedString());
		return lines;
	}
	

	public static void main(String[] args) {
		// compile result object from file
		ApassFileReader fr = new ApassFileReader();
		CatalogQuery query = new CatalogQuery();
		QueryResult result = fr.runQueryFromFile(query);
		double targetMag = 12.345;

		RaDecFileWriter fw = new RaDecFileWriter();

		System.out.println(fw.compileFilename(query));
		
		System.out.println(fw.getFile(query).toString());
		
		List<FieldObject> sortedFilteredList = result.getFieldObjects();
		
		List<String> lines = fw.compileRaDecList(sortedFilteredList, query);
		
		lines.stream().forEach(System.out::println);
		
		System.out.println(fw.writeRaDecFile(sortedFilteredList, query));
		
		

	}

}
