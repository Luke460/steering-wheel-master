package execution;

import static execution.Constants.ADVANCED_LUT_GENERATION;
import static execution.Constants.FILE_NAME_SEPARATOR;
import static execution.Constants.INTERNAL_RESOLUTION;
import static execution.Constants.LINEAR_LUT_GENERATION;
import static execution.Constants.LUT_RESOLUTION;
import static execution.Constants.MAX_RESOLUTION;
import static execution.Constants.OUTPUT_INDEX_LUT_ROUNDING_PRECISION;
import static execution.Constants.OUTPUT_VALUE_LUT_ROUNDING_PRECISION;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExecutionConfiguration;
import process.Aggregator;
import process.Corrector;
import process.Luter;
import userinterface.DrawGraphHD;
import utility.NumberWriter;
import utility.SimpleLogger;
import utility.Utility;

public class Manager {

	public static ExecutionConfiguration execute(ExecutionConfiguration exConf) {

		try {
			return process(exConf);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unexpected error: check settings and input file.");
			return exConf;
		}

	}

	public static ExecutionConfiguration process(ExecutionConfiguration exConf) throws IOException {

		SimpleLogger.infoLog("setting up...");

		java.util.ArrayList<Integer> inputForce = new java.util.ArrayList<>();
		java.util.ArrayList<Double> inputDeltaX = new java.util.ArrayList<>();
		java.util.ArrayList<Double> aggregateDeltaXDouble = new java.util.ArrayList<>();

		SimpleLogger.infoLog("reading input file...");

		// BEGIN READ
		try {
			readInputFileList(exConf, inputForce, inputDeltaX);
			
			if(!Utility.isGrowingForIntegerList(inputForce)) {
				JOptionPane.showMessageDialog(null, "Invalid input CSV file: force column does not contain increasing values.");
				return exConf;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot find '" + exConf.getInputCsvPath() + "' file.");
			return exConf;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: cannot read " + exConf.getInputCsvPath() + "' file.");
			return exConf;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: invalid input file '" + exConf.getInputCsvPath() + "' or wrong CSV settings.");
			return exConf;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid input file list. All files must have the same number of values.");
			return exConf;
		}catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Invalid input file list. All files must have the same number of values.");
			return exConf;
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

		// BEGIN ERROR CORRECTION
		java.util.ArrayList<Double> correctedDeltaX = Corrector.adjust(inputDeltaX, "deltaX");
		// END ERROR CORRECTION

		// BEGIN AUTO FUNCTION
		if(exConf.isAutoCalcAggregationOder()) {
			exConf.setAggregationOrder(Aggregator.suggestedAggregationValue(correctedDeltaX));
			return exConf;
		}
		// END AUTO FUNCTION

		// BEGIN AGGREGATION
		SimpleLogger.infoLog("aggregation...");
		if (exConf.getLutGeneration_method().equals(ADVANCED_LUT_GENERATION)) {
			if (exConf.isLinearizeNearZero()) {
				aggregateDeltaXDouble = Aggregator.performExperimentalAggregation(correctedDeltaX, exConf.getAggregationOrder());
			} else {
				aggregateDeltaXDouble = Aggregator.performAggregation(correctedDeltaX, exConf.getAggregationOrder());
			}
		}
		// END AGGREGATION
		
		// BEGIN INTERPOLATION
		inputForce = Utility.performLinearInterpolationForInt(inputForce);
		if (exConf.getLutGeneration_method().equals(ADVANCED_LUT_GENERATION)) {
			aggregateDeltaXDouble = Utility.performLinearInterpolationForDouble(aggregateDeltaXDouble);
		}
		// END INTERPOLATION

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap;
		if(exConf.getLutGeneration_method().equals(ADVANCED_LUT_GENERATION)) {
			correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXDouble);
		} else  { // Linear lut generation
			// in this case data are not aggregated, so we need to perform an aggregation first
			aggregateDeltaXDouble = Aggregator.performAggregation(correctedDeltaX, Aggregator.suggestedAggregationValue(correctedDeltaX));
			aggregateDeltaXDouble = Utility.performLinearInterpolationForDouble(aggregateDeltaXDouble);
			correctiveMap = Luter.deadZoneCorrectionOnly(inputForce, aggregateDeltaXDouble);
		}
		// END LUT GENERATION

		// BEGIN DEAD_ZONE enhancement
		if((exConf.isLinearizeNearZero() || exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)) && exConf.getDeadZoneEnhancement()>0) {
			correctiveMap = Luter.enhanceDeadZone(correctiveMap, exConf.getDeadZoneEnhancement());
		}
		// END DEAD_ZONE enhancement

		// BEGIN GAIN_REDUCTION
		if(exConf.getGainReduction()>0) {
			correctiveMap = Luter.reduceCurve(correctiveMap, exConf.getGainReduction());
		}
		// END GAIN_REDUCTION
		
		// BEGIN FFB POWER ENHANCEMENT
		if(exConf.getFfbPowerEnhancement()>0) {
			correctiveMap = Luter.alterLutCurve(correctiveMap, exConf.getFfbPowerEnhancement());
		}
		// END FFB POWER ENHANCEMENT

		// consistency check
		correctiveMap = Luter.consistencyCheck(correctiveMap);

		// result simulation
		ArrayList<Double> resultPreview = Luter.calculateLutResult(aggregateDeltaXDouble, correctiveMap);

		// print results
		if(exConf.isShowPreview()) {
			try {
				DrawGraphHD.createAndShowGui(inputDeltaX, 
						aggregateDeltaXDouble,
						Utility.correctArrayDimensionsAndValuesForVisualization(correctiveMap, Collections.max(aggregateDeltaXDouble)*correctiveMap.get(correctiveMap.size()-1)),
						resultPreview,
						generateDescriptionName(exConf)
						);
			} catch(Exception e) {
				SimpleLogger.errorLog("Unable to show preview chart: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// write results

		if(exConf.isSaveLUT()) {
			correctiveMap = Utility.round(correctiveMap,5);
			String newLutFileName = generateFileName(exConf);
			Files.deleteIfExists(Paths.get(newLutFileName));
			SimpleLogger.infoLog("generating new lut file '" + newLutFileName + "'...");
			NumberWriter indexWriter = new NumberWriter(OUTPUT_INDEX_LUT_ROUNDING_PRECISION);
			NumberWriter valueWriter = new NumberWriter(OUTPUT_VALUE_LUT_ROUNDING_PRECISION);
			for(int i = 0; i<= LUT_RESOLUTION; i++) {
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(newLutFileName, true))) {
					String value = valueWriter.getStringValue(correctiveMap.get(i*(INTERNAL_RESOLUTION/LUT_RESOLUTION)));
					String index = indexWriter.getStringValue(i/(LUT_RESOLUTION*1.0));
					String s = index + "|" + value;
					bw.write(s);
					bw.newLine();
					bw.flush();

				} catch(IOException e) { 
					e.printStackTrace();
				}
			} 

			SimpleLogger.infoLog("LUT DONE!");
			JOptionPane.showMessageDialog(null, "Process completed! Output file: '" + newLutFileName + "'.");

		}

		return exConf;

	}

	private static void readInputFileList(ExecutionConfiguration exConf, ArrayList<Integer> inputForce, ArrayList<Double> inputDeltaX)
			throws IOException, FileNotFoundException {
		String cvsSplitBy = ",";
		String[] files = exConf.getInputCsvPath().split(FILE_NAME_SEPARATOR);
		List<String> fileList = Arrays.asList(files);
		Integer prevFileLength = null;
		int fileCounter = 0;
		for(String fileName:fileList) {
			fileCounter++;
			String line;
			boolean firstLine = true;
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				int validLineCounter = 0;
				while ((line = br.readLine()) != null) {
					if(!firstLine || !exConf.isSkipFirstRow()) {

						line = line.replaceAll(" ", "");

						if(!line.equals("")) {
							String[] row = line.split(cvsSplitBy);
							if(fileCounter==1) {
								inputForce.add(Integer.parseInt(row[exConf.getForceColumnIndex()-1]));
								inputDeltaX.add(Double.parseDouble(row[exConf.getDeltaColumnIndex()-1]));
							} else {
								int newInputForceValue = Integer.parseInt(row[exConf.getForceColumnIndex()-1]);
								int prevTotalInputForceValue = inputForce.get(validLineCounter);
								inputForce.set(validLineCounter, newInputForceValue+prevTotalInputForceValue);

								double newinputDeltaX = Double.parseDouble(row[exConf.getDeltaColumnIndex()-1]);
								double prevTotalInputDeltaX = inputDeltaX.get(validLineCounter);
								inputDeltaX.set(validLineCounter, newinputDeltaX+prevTotalInputDeltaX);
							}
						}
						validLineCounter++;
					} else {
						firstLine = false;
					}

				}
				if(prevFileLength!=null && prevFileLength!=validLineCounter) {
					throw new IllegalArgumentException("Invalid input file list. All files must have the same number of values.");
				}
			}
			
		}
		
		for(int i = 0; i<inputForce.size(); i++) {
			inputForce.set(i, inputForce.get(i)/fileCounter);
			inputDeltaX.set(i, inputDeltaX.get(i)/fileCounter);
		}
	}

	private static String generateFileName(ExecutionConfiguration exConf) {
		String deadZoneEnhancement = "" + (int)exConf.getDeadZoneEnhancement();
		if (exConf.getDeadZoneEnhancement()%1 != 0) {
			deadZoneEnhancement += "p";
		}
		return "AG" + (exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?0:exConf.getAggregationOrder()) + 
				"-GR" + exConf.getGainReduction() +
				"-PE" + exConf.getFfbPowerEnhancement() +
				"-DZ" + (deadZoneEnhancement) + 
				(exConf.isLinearizeNearZero()&&!exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?"-LNZ":"") + 
				(exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?"-LL":"") 
				+ ".lut";
	}
	
	private static String generateDescriptionName(ExecutionConfiguration exConf) {
		return "[AG=" + (exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?0:exConf.getAggregationOrder()) + 
				",GR=" + exConf.getGainReduction() +
				",PE=" + exConf.getFfbPowerEnhancement() +
				",DZ=" + exConf.getDeadZoneEnhancement() + 
				",LNZ=" + (exConf.isLinearizeNearZero()&&!exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?1:0) + 
				",LL=" + (exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)?1:0) + 
				"] " + exConf.getInputCsvPath();
	}

}