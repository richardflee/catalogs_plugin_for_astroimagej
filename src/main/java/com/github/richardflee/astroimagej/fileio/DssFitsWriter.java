package com.github.richardflee.astroimagej.fileio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.github.richardflee.astroimagej.enums.CatalogsEnum;
import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.utils.CatalogUrls;

/**
 * Queries the SkyView server to download a Digitized Sky Survey (DSS) fits file
 * for the user-specified coordinates and field of view. <p> Fit (and radec)
 * files are saved in the ..astroimagej/radec folder, with format:
 * [objectid].[magband].[fov_amin].fits </p> <p> The user opens the DSS fits
 * image in AstroImageJ and imports apertures defined in the radec file.
 * AstroImageJ draws photometry apertures over images of the target and listed
 * comparison stars. </p> <p> SkyView Ref:
 * https://skyview.gsfc.nasa.gov/current/docs/batchpage.html </p>
 */
public class DssFitsWriter {

	/**
	 * Compile fits filename based on catalog query data. If this is a new file in
	 * destination folder then runs a SkyView server query on DSS catalog to
	 * download 1000x1000 fits image for the specified sky region.
	 * @param query
	 *     sky coordinate and field-of-view data
	 * @return message whether successful in writing DSS fits file
	 */
	public static String downloadDssFits(CatalogQuery query) {
		// compile DSS url for query parameters
		query.setCatalogType(CatalogsEnum.DSS);
		String skyUrl = CatalogUrls.urlBuilder(query);

		// connect a File variable with the compiled fits filename & get file path
		File file = getFile(query);
		// String filePath = file.toString();
		String filename = file.getName();
		// System.out.println(filePath);
		System.out.println(filename);

		// if fits file does not already exist
		// attempts to download & save a new dss fits file
		String message = String.format("Fit file: %s already exists", filename);
		if (!file.exists()) {
			try {
				InputStream in = new URL(skyUrl).openStream();
				Files.copy(in, Paths.get(file.toString()));
				message = String.format("Saved fits file: %s", filename);
			} catch (IOException e) {
				message = String.format("Error in writing file: %s", filename);
			}
		}
		return message;
	}

	/*
	 * Compiles file object to dss file path, ./astroimagej/dss. <p> Creates new
	 * folders as necessary</p>
	 * @param query on-line query parameters
	 * @return file object with path: ./astroimagej/radec/<filename>
	 */
	private static File getFile(CatalogQuery query) {
		// path to radec file, create new folder if necessary
		File dir = new File(System.getProperty("user.dir"), "dss");
		File file = new File(dir, compileFilename(query));
		return file;
	}
	
	/*
	 * Compiles radec filename pattern <object_id>.<fov_amin>.fits
	 * @param query on-line query parameters
	 * @return formatted filename <object_id>.<fov_amin>.fits
	 */
	private static String compileFilename(CatalogQuery query) {
		String filename = String.join(".", Arrays.asList(query.getObjectId(), 
				String.format("%03d", query.getFovAmin().intValue()), "fits"));
		return filename.replace(" ", "_");
	}

}
