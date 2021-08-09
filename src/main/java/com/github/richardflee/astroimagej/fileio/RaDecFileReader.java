package com.github.richardflee.astroimagej.fileio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class RaDecFileReader {

	private final static String fileName = 
			"C:/Users/rlee1/eclipse-workspace/astroimagej-plugin/catalogs_plugin/radec/wasp_12.V.060.radec.txt";
	
//	public String readRaDecFile() {
//		
//	}


//		// converts query data to string list to write to radec file
//
//		// write new radec file and update message
//		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//			for (String line : lines) {
//				br.append(line);
//			}
//		} catch (IOException e) {
//		}
//	}

	public static void main(String[] args) {
		
		 try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

	            stream.forEach(System.out::println);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}

	

}
