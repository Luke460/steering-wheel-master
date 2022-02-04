package utility;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CsvFileFilter extends FileFilter{

	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fName = file.getName().toLowerCase();
		return fName.endsWith("csv");
	}

	public String getDescription() {
		return "csv file";
	}

}
