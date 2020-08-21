package userInterface;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CsvFileFilter extends FileFilter{

	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
		return fname.endsWith("csv");
	}

	public String getDescription() {
		return "csv file";
	}

}
