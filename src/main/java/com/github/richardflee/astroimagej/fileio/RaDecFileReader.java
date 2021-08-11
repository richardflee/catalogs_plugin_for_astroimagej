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
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.richardflee.astroimagej.query_objects.FieldObject;

public class RaDecFileReader extends AbstractRaDecFile {

	public List<FieldObject> readRaDecFile(File file) {
		List<String> lines = new ArrayList<>();
		
		Path path = file.toPath();
		try (Stream<String> stream = Files.lines(path)) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		int idx1 = 0;
		while (!(lines.get(idx1++).equals("#"))) {
		}
		int firstIndex = idx1 + 2;

		int idx2 = lines.size() - 1;
		while (!(lines.get(idx2--).equals("#"))) {
		}
		int lastIndex = idx2;
		List<String> dataLines = lines.subList(firstIndex, lastIndex);

		List<FieldObject> fieldObjects = new ArrayList<>();		
		for (String dataLine : dataLines) {
			FieldObject fo = getFieldObject(dataLine);
			fieldObjects.add(fo);
		}
		return fieldObjects;
	}
	
	  public File radecFileDialog() {
		  
		  try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				System.err.println("Failed to initialize Windows Look-Feel");
			}
		  
		   File file = new File(System.getProperty("user.dir"), "radec");
	       JFileChooser jfc = new JFileChooser(file);
	       jfc.setDialogTitle("Select radec file");
	       jfc.setAcceptAllFileFilterUsed(false);
	       FileNameExtensionFilter filter = new FileNameExtensionFilter("RaDec files (*.txt)", "txt");
	       jfc.addChoosableFileFilter(filter);
	       
	       int returnValue = jfc.showOpenDialog(null);
	       if(returnValue == JFileChooser.APPROVE_OPTION){
	          file = jfc.getSelectedFile();
	       } else {
	    	   file = null;
	       }
		   return file;
	   }

	public static void main(String[] args) {

		RaDecFileReader fr = new RaDecFileReader();
		
		File file = fr.radecFileDialog();
		
		if (file == null) {
			System.out.println("null");
			System.exit(0);
		}
		
		List<FieldObject> fieldObjects = fr.readRaDecFile(file);
		
		
		fieldObjects.stream().forEach(System.out::println);
		
		
		System.out.println("done");

	}
}
