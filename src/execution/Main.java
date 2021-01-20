package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import userInterface.Menu;

public class Main {

	public static void main(String[] args) {

		System.out.println("START");
		System.out.println("reading '" + JSON_CONFIG_PATH + "'...");

		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);
		
		if(config == null) return;
		
		// Open Menu
		System.out.println("opening Menu...");
		Menu menu = new Menu();
		menu.showMenu(config);

	}

}
