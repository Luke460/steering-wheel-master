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

import model.ExecutionConfiguration;
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
		java.util.ArrayList<Double> aggregateDeltaXdouble = null;

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
					}

				} else {
					firstLine = false;
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

		deltaXdouble = Corrector.adjust(deltaXdouble, "deltaX");	

		// END ERROR CORRECTION	

		if(exConf.isAutoCalcAggregationOder()) {
			exConf.setAggregationOrder(Aggregator.suggestedAggregationValue(deltaXdouble));
			exConf.setDeadZoneEnhancement(0);
			exConf.setGenerateLinearLut(false);
			exConf.setPeakReduction(0);
			exConf.setExperimentalAggregation(false);
			exConf.setFfbPowerEnhacement(0);
			return exConf;
		}

		// BEGIN AGGREGATION

		System.out.println("aggregation...");

		if(exConf.isExperimentalAggregation()) {
			aggregateDeltaXdouble = Aggregator.performExperimentalAggregation(deltaXdouble, exConf.isGenerateLinearLut()?0:exConf.getAggregationOrder());
		} else {
			aggregateDeltaXdouble = Aggregator.performAggregation(deltaXdouble, exConf.isGenerateLinearLut()?0:exConf.getAggregationOrder());
		}

		// END AGGREGATION

		// BEGIN LUT GENERATION
		ArrayList<Double> correctiveMap = Luter.generateCorrectiveArray(inputForce, aggregateDeltaXdouble);

		// END LUT GENERATION

		// BEGIN DEAD CORRECTION ONLY
		if(exConf.isGenerateLinearLut()) {
			correctiveMap = Luter.deadZoneCorrectionOnly(inputForce, aggregateDeltaXdouble);
		}
		// END DEAD CORRECTION ONLY

		// BEGIN PEAK_REDUCTION
		if(exConf.getPeakReduction()>0 && exConf.getFfbPowerEnhacement()==0) {
			correctiveMap = Luter.reduceForcePeaks(correctiveMap, exConf.getPeakReduction());
		}
		// END PEAK_REDUCTION

		// BEGIN DEAD_ZONE enhancement
		if(exConf.getDeadZoneEnhancement()>0) {
			correctiveMap = Luter.enhanceDeadZone(correctiveMap, exConf.getDeadZoneEnhancement());
		}
		// END DEAD_ZONE enhancement
		
		// BEGIN FFB POWER ENHANCEMENT
		if(exConf.getFfbPowerEnhacement()>0 && exConf.getPeakReduction()==0) {
			correctiveMap = Luter.reduceForcePeaks(correctiveMap, exConf.getFfbPowerEnhacement());
			correctiveMap = Luter.enableFullPower(correctiveMap);
		}
		// END FFB POWER ENHANCEMENT

		// print results
		if(exConf.isShowPreview()) {
			try {
				DrawGraphHD.createAndShowGui(Utility.integerListToDoubleList(inputDeltaX), 
						aggregateDeltaXdouble, 
						Utility.correctArrayDimensionsAndValuesForVisualizzation(correctiveMap, Collections.max(aggregateDeltaXdouble)*correctiveMap.get(correctiveMap.size()-1)), generateDescriptionName(exConf));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		// write results

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
		String deadZoneEnhancement = "" + (int)exConf.getDeadZoneEnhancement();
		if (exConf.getDeadZoneEnhancement()%1 != 0) {
			deadZoneEnhancement += "p";
		}
		return "AG" + (exConf.isGenerateLinearLut()?0:exConf.getAggregationOrder()) + 
				"-PR" + exConf.getPeakReduction() + 
				"-PE" + exConf.getFfbPowerEnhacement() + 
				"-DZ" + (deadZoneEnhancement) + 
				(exConf.isExperimentalAggregation()&&!exConf.isGenerateLinearLut()?"-LNZ":"") + 
				(exConf.isGenerateLinearLut()?"-LL":"") 
				+ "." + type.name();
	}
	
	private static String generateDescriptionName(ExecutionConfiguration exConf) {
		return "[AG=" + (exConf.isGenerateLinearLut()?0:exConf.getAggregationOrder()) + 
				",PR=" + exConf.getPeakReduction() + 
				",PE=" + exConf.getFfbPowerEnhacement() +
				",DZ=" + exConf.getDeadZoneEnhancement() + 
				",LNZ=" + (exConf.isExperimentalAggregation()&&!exConf.isGenerateLinearLut()?1:0) + 
				",LL=" + (exConf.isGenerateLinearLut()?1:0) + 
				"] " + exConf.getInputCsvPath();
	}

}