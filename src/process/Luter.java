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

	public static ArrayList<Double> enhanceDeadZone(ArrayList<Double> input, double deadZoneEnhancement) {
		// remove 
		double totalDelta = deadZoneEnhancement*0.005; //*1.0;
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		for(int i = 0; i<input.size()-1; i++) {
			Double delta = (totalDelta/input.size())*(input.size()-1-i);
			Double value = input.get(i) - delta;
			if(value<0) {
				value=0.0;
			}
			output.set(i, value);
		}
		return output;
	}
	
	public static ArrayList<Double> deadZoneCorrectionOnly(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble){
		int x = findIndexOfLowerValue(aggregateDeltaXdouble, Collections.max(aggregateDeltaXdouble)*0.01);
		double firstLutValue = (force.get(x)*1.0)/(Collections.max(force)*1.0);
		return generateLinearizedLut(1000, firstLutValue);

	}
	
	public static ArrayList<Double> reduceForcePeaks(ArrayList<Double> input, int peakForceReduction) {
		//peakForceReduction = 1,2,3,...,8,9,10
		// 1  -> (100-1*5)*0.1 = 0.95
		// 10 -> (100-10*5)*0.1 = 0.50
		int startingPoint = (int) Math.round(input.size()*0.5);
		double startingValue = input.get(startingPoint);
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		double c = 0.0;
		double x = (peakForceReduction*0.05)/(input.size()-startingPoint);
		for(int i = startingPoint; i<input.size(); i++) {
			c += x;
			double value = startingValue*(c) + input.get(i)*(1.0-c);
			if(value<output.get(i-1)) {
				value = output.get(i-1);
			}
			output.set(i, value);
		}
		return output;
	}
	
	public static ArrayList<Double> enableFullPower(ArrayList<Double> input) {
		ArrayList<Double> output = new ArrayList<Double>();
		double maxValue = Collections.max(input);
		double len = (double) input.size();
		output.add(input.get(0));
		double maxM = 1/maxValue;
		for(int i = 1; i<=input.size()-2; i++) {
			double multiplier = ((maxM-1)/len)*((double)i) + 1.0;
			      	//^ deve dare 1 quando i è 0, oppure 1/maxValue quando i è = len
			// System.out.println("multiplier: " + multiplier);
			double newValue = input.get(i) * multiplier;
			// System.out.println("newValue: " + newValue);
			output.add(newValue);
		}
		output.add(1.0);
		return output;
	}
	
	// private methods

	private static ArrayList<Double> generateLinearizedLut(int size, double firstLutValue) {
		ArrayList<Double> output = new ArrayList<Double>();
		output.add(0.0);
		output.add(firstLutValue);
		double delta = (1-firstLutValue)/(size-1.0);
		for(int i = 2; i<size; i++) {
			output.add(output.get(i-1)+delta);
		}
		output.add(1.0);
		return output;
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
