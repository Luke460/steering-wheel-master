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

import static execution.Constants.*;
import javax.swing.JOptionPane;
import model.ExecutionConfiguration;
import process.Aggregator;
import process.Corrector;
import process.Luter;
import userInterface.DrawGraphHD;

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

		String line;
		String cvsSplitBy = ",";
		boolean firstLine = true;

		java.util.ArrayList<Integer> inputForce = new java.util.ArrayList<>();
		java.util.ArrayList<Double> inputDeltaX = new java.util.ArrayList<>();
		java.util.ArrayList<Double> aggregateDeltaXdouble = null;

		SimpleLogger.infoLog("reading input file...");

		// BEGIN READ
		try (BufferedReader br = new BufferedReader(new FileReader(exConf.getInputCsvPath()))) {

			while ((line = br.readLine()) != null) {

				if(!firstLine || !exConf.isSkipFirstRow()) {

					line = line.replaceAll(" ", "");

					if(!line.equals("")) {
						String[] row = line.split(cvsSplitBy);
						inputForce.add(Integer.parseInt(row[exConf.getForceColumnIndex()-1]));
						inputDeltaX.add(Double.parseDouble(row[exConf.getDeltaColumnIndex()-1]));
					}

				} else {
					firstLine = false;
				}

			}
		
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
		} catch ( NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: invalid input file '" + exConf.getInputCsvPath() + "' or wrong CSV settings.");
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
				aggregateDeltaXdouble = Aggregator.performExperimentalAggregation(correctedDeltaX, exConf.getAggregationOrder());
			} else {
				aggregateDeltaXdouble = Aggregator.performAggregation(correctedDeltaX, exConf.getAggregationOrder());
			}
		}
		// END AGGREGATION
		
		// BEGIN INTERPOLATION
		inputForce = Utility.performLinearInterpolationForInt(inputForce);
		if (exConf.getLutGeneration_method().equals(ADVANCED_LUT_GENERATION)) {
			aggregateDeltaXdouble = Utility.performLinearInterpolationForDouble(aggregateDeltaXdouble);
		}
		// END INTERPOLATION

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = null;
		if(exConf.getLutGeneration_method().equals(ADVANCED_LUT_GENERATION)) {
			correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXdouble);
		} else if(exConf.getLutGeneration_method().equals(LINEAR_LUT_GENERATION)) {
			// in this case data are not aggregated, so we need to perform an aggregation first
			aggregateDeltaXdouble = Aggregator.performAggregation(correctedDeltaX, Aggregator.suggestedAggregationValue(correctedDeltaX));
			aggregateDeltaXdouble = Utility.performLinearInterpolationForDouble(aggregateDeltaXdouble);
			correctiveMap = Luter.deadZoneCorrectionOnly(inputForce, aggregateDeltaXdouble);
		}
		// END LUT GENERATION

		// BEGIN DEAD_ZONE enhancement
		if(exConf.getDeadZoneEnhancement()>0) {
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
		ArrayList<Double> resultPreview = Luter.calculateLutResult(aggregateDeltaXdouble, correctiveMap);

		// print results
		if(exConf.isShowPreview()) {
			try {
				DrawGraphHD.createAndShowGui(inputDeltaX, 
						aggregateDeltaXdouble,
						Utility.correctArrayDimensionsAndValuesForVisualization(correctiveMap, Collections.max(aggregateDeltaXdouble)*correctiveMap.get(correctiveMap.size()-1)),
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
			correctiveMap = Utility.round(correctiveMap,4);
			String newLutFileName = generateFileName(exConf);
			Files.deleteIfExists(Paths.get(newLutFileName));
			SimpleLogger.infoLog("generating new lut file '" + newLutFileName + "'...");
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

			SimpleLogger.infoLog("LUT DONE!");
			JOptionPane.showMessageDialog(null, "Process completed! Output file: '" + newLutFileName + "'.");

		}

		return exConf;

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