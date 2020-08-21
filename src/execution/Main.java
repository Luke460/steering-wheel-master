package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {

		System.out.println("BEGIN PROCEDURE");
		System.out.println("reading '" + JSON_CONFIG_PATH + "'...");

		// Read Configuration
		org.json.JSONObject config = null;
		try {
			config = new org.json.JSONObject(new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH))));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + JSON_CONFIG_PATH + "' file.");
			return;
		}
		
		// Open Menu
		Menu menu = new Menu();
		menu.showMenu(config);

	}

}
