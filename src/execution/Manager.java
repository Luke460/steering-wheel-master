package execution;

import static execution.Constants.JSON_CONFIG_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import process.Aggregator;
import process.Corrector;
import process.Luter;
import userInterface.DrawGraph;

public class Manager {

	public static int execute(org.json.JSONObject config, boolean saveCSV, boolean saveLUT, boolean showPreview) {

		System.out.println("setup...");

		String inputCsvPath = null;
		int aggregationOrder = 0;
		try {
			inputCsvPath = config.getString("input_file");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'input_file' property in '" + JSON_CONFIG_PATH + "'.");
			return -1;
		}
		try {
			aggregationOrder = config.getInt("aggregation_order");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'aggregation_order' property in '" + JSON_CONFIG_PATH + "'.");
			return -1;
		}


		System.out.println("starting generate csv procedure...");

		try {
			return process(inputCsvPath, aggregationOrder, saveCSV, saveLUT, showPreview);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error during aggregation process.");
			return -1;
		}

	}

	public static int process(String csvFile, int aggregationOrder, boolean saveCSV,  boolean saveLUT, boolean showPreview) {

		System.out.println("setting up...");

		String line = "";
		String cvsSplitBy = ",";
		boolean firstLine = true;

		java.util.ArrayList<Integer> inputForce = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> startX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> endX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Integer> inputDeltaX = new java.util.ArrayList<Integer>();
		java.util.ArrayList<Double> inputDeltaXDeg = new java.util.ArrayList<Double>();

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
						inputForce.add(Integer.parseInt(row[0]));
						startX.add(Integer.parseInt(row[1]));
						endX.add(Integer.parseInt(row[2]));
						inputDeltaX.add(Integer.parseInt(row[3]));
						inputDeltaXDeg.add(Double.parseDouble(row[4]));
					}

				} else {
					firstLine = false;
					title = line;
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot find '" + csvFile + "' file.");
			return aggregationOrder;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot read " + csvFile + "' file.");
			return aggregationOrder;
		} catch ( NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: invalid input file '" + csvFile + "'.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unexpected error while reading " + csvFile + "' file.");
			return aggregationOrder;
		}

		// END READ


		// for more precision
		java.util.ArrayList<Double> deltaXdouble = new java.util.ArrayList<Double>(); 
		deltaXdouble = Utility.integerListToDoubleList(inputDeltaX);

		// BEGIN ERROR CORRECTION

		inputDeltaXDeg = Corrector.adjust(inputDeltaXDeg, "deltaXDeg");
		deltaXdouble = Corrector.adjust(deltaXdouble, "deltaX");	

		// END ERROR CORRECTION	
		
		if(!saveCSV && !saveLUT && !showPreview) {
			return Aggregator.suggestedAggregationValue(deltaXdouble);
		}

		// BEGIN AGGREGATION

		System.out.println("aggregation...");

		aggregatedeltaXDeg = Aggregator.aggregate(inputDeltaXDeg, aggregationOrder);
		aggregateDeltaXdouble = Aggregator.aggregate(deltaXdouble, aggregationOrder);

		// END AGGREGATION

		aggregateDeltaX = Utility.doubleListToIntegerList(aggregateDeltaXdouble);

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXdouble);
		//correctiveMap = Aggregator.aggregate(correctiveMap,20);
		correctiveMap = Utility.truncateArray(correctiveMap, 10);

		// END LUT GENERATION

		// print results
		if(showPreview) {
			try {
				DrawGraph.createAndShowGui(Utility.integerListToDoubleList(inputDeltaX), 
						aggregateDeltaXdouble, 
						Utility.correctArrayDimensionsAndValues(correctiveMap, aggregateDeltaXdouble.size(), Collections.max(aggregateDeltaXdouble)), 
						csvFile);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		// write results

		if(saveCSV) {
			String newCsvFileName = "output-AG-" + aggregationOrder + "-T-" + System.currentTimeMillis() + ".csv";
			System.out.println("generating new csv file '" + newCsvFileName + "'...");

			for(int i = -1; i < inputForce.size(); i++) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newCsvFileName, true))) {
					if(i == -1) {
						bw.write(title);
						bw.newLine();
						bw.flush();
					} else {
						int adjustedEndX = startX.get(i) + aggregateDeltaX.get(i);
						String s = inputForce.get(i) + ", " + 
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
			correctiveMap = Utility.round(correctiveMap,3);
			String newLutFileName = "LUT-AG-" + aggregationOrder + "-T-" + System.currentTimeMillis() + ".lut";
			System.out.println("generating new lut file '" + newLutFileName + "'...");
			double index = 0.0;
			for(Double value: correctiveMap) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newLutFileName, true))) {
					String s = index + "|" + value; 
					index = index + 0.01;
					index = Utility.round(index,2);
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
		
		return aggregationOrder;

	}

}