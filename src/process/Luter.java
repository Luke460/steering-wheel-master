package process;

import java.util.ArrayList;
import java.util.Collections;

import execution.SimpleLogger;
import execution.Utility;

public class Luter {

	public static final double DEAD_ZONE_MULTIPLIER = 0.005;
	public static final double DEAD_ZONE_VALUE_PRECISION_INCREMENT = 0.005;

	public static ArrayList<Double> generateCorrectiveArray(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble) {

		ArrayList<Double> corrections = new ArrayList<>();
		double maxDeltaX = Collections.max(aggregateDeltaXdouble);
		double maxForce = Collections.max(force) + 0.0;

		for(double i = 0; i<= 1; i+=0.001) {
			i = Utility.round(i,3);
			double targetDeltaX = i*maxDeltaX;
			int x = findIndexOfLowerValue(aggregateDeltaXdouble, targetDeltaX);
			
			double correctForce = force.get(x);

			correctForce = (correctForce/maxForce);
			
			if(correctForce>maxForce) {
				correctForce = maxForce;
			}
			
			corrections.add(correctForce);
		}
		
		corrections.set(corrections.size()-1, 1.0);
		return correctLutArray(corrections);

	}
	
	public static ArrayList<Double> correctLutArray(ArrayList<Double> input){
		ArrayList<Double> output = new ArrayList<>();
		for(int i=0; i<input.size(); i++) {
			double currentValue = input.get(i);
			int indexStart = findIndexOfLowerValue(input, currentValue)+1;
			int indexPlus = findIndexOfHigherValue(input, currentValue);
			double plusValue = input.get(indexPlus);
			double newValue;
			double deltaI = indexPlus - indexStart;
			double deltaValue = plusValue - currentValue;
			if(deltaI!=0) {
				newValue = (i-indexStart)*(deltaValue/deltaI) + currentValue;
			} else {
				newValue = currentValue;
			}
			if(newValue>1.0) {
				newValue=1.0;
			}
			output.add(newValue);
		}
		output.set(0, 0.0);
		output.set(input.size()-1, 1.0);
		return output;
	}

	public static ArrayList<Double> enhanceDeadZone(ArrayList<Double> input, double inputDeadZoneEnhancement) {
		// remove
		double deadZoneEnhancement = Math.min(inputDeadZoneEnhancement, calculateSuggestedDeadZoneEnhancementValue(input));
		double totalDelta = deadZoneEnhancement*DEAD_ZONE_MULTIPLIER; //*1.0;
		ArrayList<Double> output = new ArrayList<>(input);
		for(int i = 0; i<input.size()-1; i++) {
			double delta = (totalDelta/input.size())*(input.size()-1-i);
			double value = input.get(i) - delta;
			if(value<0) {
				value=0.0;
			}
			output.set(i, value);
		}
		return output;
	}

	public static double calculateSuggestedDeadZoneEnhancementValue(ArrayList<Double> input) {
		double enhancementValue = 0;
		while(enhancementValue<10){
			double totalDelta = enhancementValue*DEAD_ZONE_MULTIPLIER;
			double delta = (totalDelta/input.size())*(input.size()-1-1);
			double value = input.get(1) - delta;
			if(value<=0) return enhancementValue;
			enhancementValue += DEAD_ZONE_VALUE_PRECISION_INCREMENT;
		}
		return 10.0;
	}
	
	public static ArrayList<Double> deadZoneCorrectionOnly(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble){
		int x = findIndexOfLowerValue(aggregateDeltaXdouble, Collections.max(aggregateDeltaXdouble)*0.02);
		double firstLutValue = (force.get(x)*1.0)/(Collections.max(force)*1.0);
		SimpleLogger.infoLog("firstLutValue: " + firstLutValue);
		return generateLinearizedLut(firstLutValue);
	}

	public static ArrayList<Double> reduceForcePeaks(ArrayList<Double> input, int peakForceReduction) {
		double c = 0.025;
		ArrayList<Double> output = new ArrayList<>(input);
		for(int i = input.size()-1; i>0; i--) {
			double specificPeakReduction = peakForceReduction * c * i/(input.size()-1.0);
			double value = input.get(i)*(1.0-specificPeakReduction);
			output.set(i, value);
		}
		return output;
	}

	public static ArrayList<Double> enableFullPower(ArrayList<Double> input) {
		ArrayList<Double> output = new ArrayList<>();
		double maxValue = input.get(input.size()-1);
		output.add(0.0);
		double totalDelta = 1.0 - maxValue;
		for(int i = 1; i<=input.size()-1; i++) {
			double localDelta = (i*totalDelta)/(1.0*input.size()-1);
			double newValue = input.get(i) + localDelta;
			if(newValue>1.0) newValue = 1.0;
			output.add(newValue);
		}
		return output;
	}

	public static ArrayList<Double> consistencyCheck(ArrayList<Double> input) {
		ArrayList<Double> output = new ArrayList<>();
		int consistencyCorrections = 0;
		if(input.get(0)!=0.0){
			consistencyCorrections++;
			SimpleLogger.warningLog("First lut value was not 0");
		}
		output.add(0.0);
		double lastValue = 0.0;
		for(int i = 1; i<=input.size()-1; i++) {
			double currentValue = input.get(i);
			if(currentValue>1.0){
				currentValue = 1.0;
				consistencyCorrections++;
				SimpleLogger.warningLog("Found clipping in the lut");
			}
			if(currentValue<lastValue){
				currentValue = lastValue;
				consistencyCorrections++;
				SimpleLogger.warningLog("Found decreasing value in the lut");
			}
			output.add(currentValue);
			lastValue = currentValue;
		}
		SimpleLogger.infoLog("Consistency corrections: " + consistencyCorrections);
		return output;
	}
	
	// private methods

	private static ArrayList<Double> generateLinearizedLut(double firstLutValue) {
		ArrayList<Double> output = new ArrayList<>();
		output.add(0.0);
		output.add(firstLutValue);
		double delta = (1-firstLutValue)/(1000 -1.0);
		for(int i = 2; i< 1000; i++) {
			output.add(output.get(i-1)+delta);
		}
		output.add(1.0);
		return output;
	}
	
	private static int findIndexOfLowerValue(ArrayList<Double> input, double targetValue) {
		
		if(targetValue==0) return 0;
		// from last value
		for(int i=input.size()-1; i>0;i--) {
			if(input.get(i)<=targetValue){
				return i;
			}
		}

		return 0;
	}
	
	private static int findIndexOfHigherValue(ArrayList<Double> input, double targetValue) {
		
		if(targetValue==0) return 0;
		
		for(int i=input.size()-1; i>0;i--) {
			double prevValue = input.get(i-1);
			double value = input.get(i);
			if(targetValue>=prevValue && targetValue<value){
				return i;
			}
		}

		return input.size()-1;
	}

}
