package execution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import static execution.Constants.MAX_RESOLUTION;
import javax.swing.JOptionPane;
import process.Aggregator;
import process.Corrector;
import process.Luter;
import userInterface.DrawGraphHD;

public class Manager {
	
	public enum FileType {
		csv, lut
	}

	public static ExecutionConfiguration execute(ExecutionConfiguration exConf) {

		try {
			return process(exConf);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error during aggregation process.");
			return exConf;
		}

	}

	public static ExecutionConfiguration process(ExecutionConfiguration exConf) throws IOException {

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

		if(exConf.isSaveCSV()) {
			inputDeltaXDeg = Corrector.adjust(inputDeltaXDeg, "deltaXDeg");
		}
		deltaXdouble = Corrector.adjust(deltaXdouble, "deltaX");	

		// END ERROR CORRECTION	
		
		if(exConf.isAutoCalcAggregationOder()) {
			exConf.setAggregationOrder(Aggregator.suggestedAggregationValue(deltaXdouble));
			exConf.setDeadZoneEnhancement(0);
			exConf.setDeadZoneCorrectionOnly(false);
			return exConf;
		}

		// BEGIN AGGREGATION

		System.out.println("aggregation...");

		if(exConf.isSaveCSV()) {
			aggregatedeltaXDeg = Aggregator.aggregate(inputDeltaXDeg, exConf.isDeadZoneCorrectionOnly()?0:exConf.getAggregationOrder());
		}
		aggregateDeltaXdouble = Aggregator.aggregate(deltaXdouble, exConf.isDeadZoneCorrectionOnly()?0:exConf.getAggregationOrder());

		// END AGGREGATION

		aggregateDeltaX = Utility.doubleListToIntegerList(aggregateDeltaXdouble);

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXdouble);

		// END LUT GENERATION
		
		// BEGIN DEAD CORRECTION ONLY
		if(exConf.isDeadZoneCorrectionOnly()) {
			correctiveMap = Luter.deadZoneCorrectionOnly(inputForce, aggregateDeltaXdouble);
		}
		// END DEAD CORRECTION ONLY

		// BEGIN DEAD_ZONE enhancement
		if(exConf.getDeadZoneEnhancement()>0) {
			correctiveMap = Luter.enhanceDeadZone(correctiveMap, exConf.getDeadZoneEnhancement());
		}
		
		// END DEAD_ZONE enhancement
		
		// print results
		if(exConf.isShowPreview()) {
			try {
				DrawGraphHD.createAndShowGui(Utility.integerListToDoubleList(inputDeltaX), 
						aggregateDeltaXdouble, 
						Utility.correctArrayDimensionsAndValuesForVisualizzation(correctiveMap, Collections.max(aggregateDeltaXdouble)), 
						"[AG=" + (exConf.isDeadZoneCorrectionOnly()?0:exConf.aggregationOrder) + ",DZ=" + exConf.getDeadZoneEnhancement() + ",DZCO=" + exConf.isDeadZoneCorrectionOnly() + "] " + exConf.inputCsvPath);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//correctiveMap = Utility.truncateArray(correctiveMap, 5); // 200 values in the output lut

		// write results

		if(exConf.isSaveCSV()) {
			String newCsvFileName = generateFileName(exConf, FileType.csv);
			System.out.println("generating new csv file '" + newCsvFileName + "'...");
			Files.deleteIfExists(Paths.get(newCsvFileName));
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
			String newLutFileName = generateFileName(exConf, FileType.lut);
			Files.deleteIfExists(Paths.get(newLutFileName));
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
	
	private static String generateFileName(ExecutionConfiguration exConf, FileType type) {
		return "AG-" + (exConf.isDeadZoneCorrectionOnly()?0:exConf.aggregationOrder) + "-DZ-" + exConf.getDeadZoneEnhancement() + (exConf.isDeadZoneCorrectionOnly()?"-DZCO":"") + (exConf.isAddTimestamp()?"-T-" + System.currentTimeMillis():"") + "." + type.name();
	}

}