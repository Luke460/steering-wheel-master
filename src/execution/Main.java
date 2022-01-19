package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import userInterface.Menu;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {

		SimpleLogger.infoLog("START");
		SimpleLogger.infoLog("reading '" + JSON_CONFIG_PATH + "'...");

		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);

		if(config == null) return;

		// Open Menu
		SimpleLogger.infoLog("opening Menu...");

		try {
			Menu menu = new Menu();
			menu.showMenu(config);
		} catch (org.json.JSONException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong:\nUnable to load the configuration file '" + JSON_CONFIG_PATH + "'.\nError details: " + e.getMessage());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Something went wrong:\nError details: " + e.getMessage());
		}

	}

}
