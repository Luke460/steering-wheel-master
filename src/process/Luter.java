package process;

import java.util.ArrayList;
import java.util.Collections;

import execution.Utility;

public class Luter {

	public static ArrayList<Double> generateCorrectiveArray(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble) {

		ArrayList<Double> corrections = new ArrayList<Double>();
		Double maxDeltaX = Collections.max(aggregateDeltaXdouble);
		Double maxforce = Collections.max(force) + 0.0;

		for(double i = 0; i<= 1; i+=0.001) {
			i = Utility.round(i,3);
			double targetDeltaX = i*maxDeltaX;
			int x = findIndexOfLowerValue(aggregateDeltaXdouble, targetDeltaX);
			
			double correctForce = force.get(x);

			correctForce = (correctForce/maxforce);
			
			if(correctForce>maxforce) {
				correctForce = maxforce;
			}
			
			corrections.add(correctForce);
		}
		
		corrections.set(corrections.size()-1, 1.0);

		return correctLutArray(corrections);

	}
	
	public static ArrayList<Double> correctLutArray(ArrayList<Double> input){
		ArrayList<Double> output = new ArrayList<Double>();
		for(int i=0; i<input.size(); i++) {
			double currentValue = input.get(i);
			int indexStart = findIndexOfLowerValue(input, currentValue)+1;
			int indexPlus = findIndexOfHigherValue(input, currentValue);
			double plusValue = input.get(indexPlus);
			double newValue = 0;
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
	
	public static ArrayList<Double> enhanceDeadZone(ArrayList<Double> input, int deadZoneEnhancement){
		// dead zone enhancement: 0% -> 5% [+0.5%]
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		ArrayList<Double> percentages = new ArrayList<Double>();
		for(double i=1; i<=deadZoneEnhancement*5; i++) {
			percentages.add(Math.max(0, Utility.round(i*0.01, 2)));
		}
		for(int i=1; i<=percentages.size(); i++) {
			double percentage = percentages.get(percentages.size()-i);
			output.set(i, input.get(i)*(1.0-percentage));
		}
		return output;
	}
	
	public static ArrayList<Double> deadZoneCorrectionOnly(ArrayList<Double> input){
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		for(int i=0; i<input.size()-1; i++) {
			Double targetDelta = findTargetDelta(input, i);
			Double currentDelta = input.get(i+1) - input.get(i); 
			if(targetDelta>=currentDelta) {
				return linearizeAll(output,i,targetDelta);
			}
		}
		return output;
	}

	private static ArrayList<Double> linearizeAll(ArrayList<Double> input, int x, double delta) {
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		for(int i = x; i<input.size()-1; i++) {
			Double value = output.get(i-1) + delta;
			output.set(i, value);
		}
		return output;
	}

	private static Double findTargetDelta(ArrayList<Double> input, int i) {
		Double totalDelta = input.get(input.size()-1) - input.get(i);
		return (totalDelta/(double)(input.size()-1-i));
	}

	private static int findIndexOfLowerValue(ArrayList<Double> input, double targetValue) {
		
		if(targetValue==0) return 0;
		
		for(int i=0; i<input.size();i++) {
			if(i!=0 && targetValue>input.get(i-1) && targetValue<=input.get(i)){
				return i-1;
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
