package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import process.Aggregator;
import process.Corrector;
import process.Luter;
import userInterface.DrawGraph;

public class Manager {

	public static void execute(org.json.JSONObject config, boolean saveCSV, boolean saveLUT) {

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
			process(inputCsvPath, aggregationOrder, saveCSV, saveLUT);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error during aggregation process.");
			return;
		}

	}

	public static void process(String csvFile, int aggregationOrder, boolean saveCSV,  boolean saveLUT) {

		System.out.println("setting up...");

		String line = "";
		String cvsSplitBy = ",";
		boolean firstLine = true;

		java.util.ArrayList<Integer> force = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> startX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> endX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> deltaX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Double> deltaXDeg = new java.util.ArrayList<Double>();

		java.util.ArrayList<Integer> aggregateDeltaX = null;
		java.util.ArrayList<Double> aggregateDeltaXdouble = null;
		java.util.ArrayList<Double> aggregatedeltaXDeg = null;

		String title = null;

		System.out.println("reading input file...");

		// BEGIN READ

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			while ((line = br.readLine()) != null) {

				if(!firstLine) {

					line = line.replaceAll(" ", "");

					if(line!=null && !line.equals("")) {
						String[] row = line.split(cvsSplitBy);
						force.add(Integer.parseInt(row[0]));
						startX.add(Integer.parseInt(row[1]));
						endX.add(Integer.parseInt(row[2]));
						deltaX.add(Integer.parseInt(row[3]));
						deltaXDeg.add(Double.parseDouble(row[4]));
					}

				} else {
					firstLine = false;
					title = line;
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot find '" + csvFile + "' file.");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot read " + csvFile + "' file.");
			return;
		} catch ( NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: invalid input file '" + csvFile + "'.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unexpected error while reading " + csvFile + "' file.");
			return;
		}

		// END READ


		// for more precision
		java.util.ArrayList<Double> deltaXdouble = new java.util.ArrayList<Double>(); 
		deltaXdouble = Utility.integerListToDoubleList(deltaX);

		// BEGIN ERROR CORRECTION

		deltaXDeg = Corrector.adjust(deltaXDeg, "deltaXDeg");
		deltaXdouble = Corrector.adjust(deltaXdouble, "deltaX");	

		// END ERROR CORRECTION	

		// BEGIN AGGREGATION

		System.out.println("aggregation...");

		aggregatedeltaXDeg = Aggregator.aggregate(deltaXDeg, aggregationOrder);
		aggregateDeltaXdouble = Aggregator.aggregate(deltaXdouble, aggregationOrder);

		// END AGGREGATION

		aggregateDeltaX = Utility.doubleListToIntegerList(aggregateDeltaXdouble);

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = Luter.generateCorrectiveArray(force, aggregateDeltaXdouble);
		// END LUT GENERATION

		// print results
		
		try {
			DrawGraph.createAndShowGui(deltaXdouble, aggregateDeltaXdouble, correctiveMap, csvFile);
		} catch(Exception e) {
			e.printStackTrace();
		}

		// write results

		if(saveCSV) {
			String newCsvFileName = "output-AG-" + aggregationOrder + "-T-" + System.currentTimeMillis() + ".csv";
			System.out.println("generating new csv file '" + newCsvFileName + "'...");

			for(int i = -1; i < force.size(); i++) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newCsvFileName, true))) {
					if(i == -1) {
						bw.write(title);
						bw.newLine();
						bw.flush();
					} else {
						int adjustedEndX = startX.get(i) + aggregateDeltaX.get(i);
						String s = force.get(i) + ", " + 
								startX.get(i) + ", " +
								adjustedEndX + ", " + 
								aggregateDeltaX.get(i) + ", " +
								aggregatedeltaXDeg.get(i);   
						bw.write(s);
						bw.newLine();
						bw.flush();
					}
				} catch(IOException e) { 
					e.printStackTrace();
				}
			} 

			System.out.println("CSV DONE!");
			JOptionPane.showMessageDialog(null, "Process completed! Output file: '" + newCsvFileName + "'.");

		}
		
		if(saveLUT) {
			String newLutFileName = "LUT-AG-" + aggregationOrder + "-T-" + System.currentTimeMillis() + ".lut";
			System.out.println("generating new lut file '" + newLutFileName + "'...");
			double index = 0.0;
			for(Double value: correctiveMap) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newLutFileName, true))) {
						String s = index + "|" + value; 
						index = index + 0.01;
						index = Utility.roundBy2(index);
						bw.write(s);
						bw.newLine();
						bw.flush();
					
				} catch(IOException e) { 
					e.printStackTrace();
				}
			} 

			System.out.println("LUT DONE!");
			JOptionPane.showMessageDialog(null, "Process completed! Output file: '" + newLutFileName + "'.");

		}

	}

}