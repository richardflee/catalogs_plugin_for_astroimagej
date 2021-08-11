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

import com.github.richardflee.astroimagej.query_objects.CatalogQuery;
import com.github.richardflee.astroimagej.query_objects.FieldObject;

public class RaDecFileReader extends AbstractRaDecFile {

	public List<String> readRaDecFile(File file) {
		List<String> lines = new ArrayList<>();

		Path path = file.toPath();
		try (Stream<String> stream = Files.lines(path)) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
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
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = jfc.getSelectedFile();
		} else {
			file = null;
		}
		return file;
	}

	public List<FieldObject> getTableData(List<String> lines) {
		int idx = 0;
		while (!(lines.get(idx).equals("#"))) {
			idx++;
		}
		int fromIndex = idx + 2;

		idx = lines.size() - 1;
		while (!(lines.get(idx).equals("#"))) {
			idx--;
		}
		int toIndex = idx;
		List<String> dataLines = lines.subList(fromIndex, toIndex);
		

		List<FieldObject> fieldObjects = new ArrayList<>();
		for (String dataLine : dataLines) {
			FieldObject fo = compileFieldObject(dataLine);
			fieldObjects.add(fo);
		}
		return fieldObjects;
	}
	
	private String getQueryLine(List<String> lines) {

		int idx = lines.size() - 1;
		while (!(lines.get(idx).equals("#"))) {
			idx--;
		}
		int dataIndex = idx + 2;
		return lines.get(dataIndex);
	}
	
	public CatalogQuery getQueryData(List<String> lines) {
		String dataLine = getQueryLine(lines);
		return CatalogQuery.fromFormattedString(dataLine);
	}

	public static void main(String[] args) {

		RaDecFileReader fr = new RaDecFileReader();

		File file = fr.radecFileDialog();

		if (file == null) {
			System.out.println("null");
			System.exit(0);
		}

		List<String> lines = fr.readRaDecFile(file);
		lines.stream().forEach(System.out::println);
		List<FieldObject> fieldObjects = fr.getTableData(lines);
		CatalogQuery query = fr.getQueryData(lines);
		
		System.out.println();
		fieldObjects.stream().forEach(p -> System.out.println(p.toString()));

		String dataLine = fr.getQueryLine(lines);
		System.out.println(query.toString());

	}
}
