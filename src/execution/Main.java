package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import userInterface.Menu;

public class Main {

	public static void main(String[] args) {

		SimpleLogger.infoLog("START");
		SimpleLogger.infoLog("reading '" + JSON_CONFIG_PATH + "'...");

		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);
		
		if(config == null) return;
		
		// Open Menu
		SimpleLogger.infoLog("opening Menu...");
		Menu menu = new Menu();
		menu.showMenu(config);

	}

}
