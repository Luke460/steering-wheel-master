package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {

		System.out.println("BEGIN PROCEDURE");
		System.out.println("reading '" + JSON_CONFIG_PATH + "'...");


		org.json.JSONObject config = null;
		try {
			config = new org.json.JSONObject(new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH))));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + JSON_CONFIG_PATH + "' file.");
			return;
		}


		System.out.println("setup...");

		String inputCsvPath = null;
		int aggregationOrder = 0;
		try {
			inputCsvPath = config.getString("input_file");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'input_file' property in '" + JSON_CONFIG_PATH + "'.");
			return;
		}
		try {
			aggregationOrder = config.getInt("aggregation_order");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'aggregation_order' property in '" + JSON_CONFIG_PATH + "'.");
			return;
		}


		System.out.println("starting generate csv procedure...");

		try {
			CsvManager.generateCsv(inputCsvPath, aggregationOrder);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error during aggregation process.");
			return;
		}

	}

}
