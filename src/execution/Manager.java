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
import static execution.Constants.MAX_RESOLUTION;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import process.Aggregator;
import process.Corrector;
import process.Luter;
import userInterface.DrawGraph;

public class Manager {

	public static ExecutionConfiguration execute(JSONObject config, ExecutionConfiguration exConf) {

		System.out.println("setup...");

		try {
			exConf.setInputCsvPath(config.getString("input_file"));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'input_file' property in '" + JSON_CONFIG_PATH + "'.");
			return exConf;
		}
		try {
			exConf.setAggregationOrder(config.getInt("aggregation_order"));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'aggregation_order' property in '" + JSON_CONFIG_PATH + "'.");
			return exConf;
		}
		try {
			exConf.setDeadZoneEnhancement(config.getInt("deadzone_enhancement"));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read 'deadzone_enhancement' property in '" + JSON_CONFIG_PATH + "'.");
			return exConf;
		}


		System.out.println("starting generate csv procedure...");

		try {
			return process(exConf);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error during aggregation process.");
			return exConf;
		}

	}

	public static ExecutionConfiguration process(ExecutionConfiguration exConf) {

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

		try (BufferedReader br = new BufferedReader(new FileReader(exConf.getInputCsvPath()))) {

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
			JOptionPane.showMessageDialog(null, "Error: cannot find '" + exConf.getInputCsvPath() + "' file.");
			return exConf;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot read " + exConf.getInputCsvPath() + "' file.");
			return exConf;
		} catch ( NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: invalid input file '" + exConf.getInputCsvPath() + "'.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unexpected error while reading " + exConf.getInputCsvPath() + "' file.");
			return exConf;
		}
		
		if(inputDeltaX.size()>MAX_RESOLUTION) {
			JOptionPane.showMessageDialog(null, "Input file exceeds the maximum resolution: " + MAX_RESOLUTION);
			return exConf;
		}

		// END READ


		// for more precision
		java.util.ArrayList<Double> deltaXdouble = new java.util.ArrayList<Double>(); 
		deltaXdouble = Utility.integerListToDoubleList(inputDeltaX);

		// BEGIN ERROR CORRECTION

		inputDeltaXDeg = Corrector.adjust(inputDeltaXDeg, "deltaXDeg");
		deltaXdouble = Corrector.adjust(deltaXdouble, "deltaX");	

		// END ERROR CORRECTION	
		
		if(exConf.isAutoCalcAggregationOder()) {
			exConf.setAggregationOrder(Aggregator.suggestedAggregationValue(deltaXdouble));
			return exConf;
		}

		// BEGIN AGGREGATION

		System.out.println("aggregation...");

		aggregatedeltaXDeg = Aggregator.aggregate(inputDeltaXDeg, exConf.getAggregationOrder());
		aggregateDeltaXdouble = Aggregator.aggregate(deltaXdouble, exConf.getAggregationOrder());

		// END AGGREGATION

		aggregateDeltaX = Utility.doubleListToIntegerList(aggregateDeltaXdouble);

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXdouble);

		// END LUT GENERATION

		// BEGIN DEAD_ZONE enhancement
		if(exConf.getDeadZoneEnhancement()>0) {
			correctiveMap = Luter.enhanceDeadZone(correctiveMap, exConf.getDeadZoneEnhancement());
		}
		
		// END DEAD_ZONE enhancement
		
		// print results
		if(exConf.isShowPreview()) {
			try {
				DrawGraph.createAndShowGui(Utility.integerListToDoubleList(inputDeltaX), 
						aggregateDeltaXdouble, 
						Utility.correctArrayDimensionsAndValuesForVisualizzation(correctiveMap, aggregateDeltaXdouble.size(), Collections.max(aggregateDeltaXdouble)), 
						"[AG=" + exConf.aggregationOrder + "] " + exConf.inputCsvPath);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//correctiveMap = Utility.truncateArray(correctiveMap, 5); // 200 values in the output lut

		// write results

		if(exConf.isSaveCSV()) {
			String newCsvFileName = "output-AG-" + exConf.getAggregationOrder() + "-T-" + System.currentTimeMillis() + ".csv";
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

		if(exConf.isSaveLUT()) {
			correctiveMap = Utility.round(correctiveMap,4);
			String newLutFileName = "LUT-AG-" + exConf.getAggregationOrder() + "-T-" + System.currentTimeMillis() + ".lut";
			System.out.println("generating new lut file '" + newLutFileName + "'...");
			double index = 0.0;
			for(Double value: correctiveMap) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newLutFileName, true))) {
					String s = index + "|" + value; 
					index = index + (1.0/(double)correctiveMap.size());
					index = Utility.round(index,3);
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
		
		return exConf;

	}

}