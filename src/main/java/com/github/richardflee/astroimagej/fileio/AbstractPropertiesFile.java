package com.github.richardflee.astroimagej.fileio;

import java.nio.file.Paths;


public class AbstractPropertiesFile {
	
	protected static String propertiesFilepath = null;
	
	public AbstractPropertiesFile() {
		String homePath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
		AbstractPropertiesFile.propertiesFilepath = 
				Paths.get(homePath, ".astroimagej", "catalogs_plug.properties").toString();
	}
}
