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

		return correctLutArray(corrections);

	}
	
	public static ArrayList<Double> correctLutArray(ArrayList<Double> input){
		ArrayList<Double> output = new ArrayList<Double>();
		for(int i=0; i<input.size(); i++) {
			double currentValue = input.get(i);
			int indexStart = findIndexOfLowerValue(input, currentValue)+1;
			int indexPlus = findIndexOfHigherValue(input, currentValue);;
			double plusValue = input.get(indexPlus);
			double newValue = 0;
			double deltaI = indexPlus - indexStart;
			double deltaValue = plusValue - currentValue;
			if(deltaI!=0) {
				//newValue = (i*deltaValue)/deltaI + currentValue;
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
	
	public static ArrayList<Double> enhanceDeadZone(ArrayList<Double> input){
		ArrayList<Double> output = new ArrayList<Double>();
		output.addAll(input);
		// Fibonacci sequence
		double[] percentages = new double[] {0.01, 0.01, 0.02, 0.03, 0.05, 0.08, 0.13, 0.21, 0.34, 0.55, 0.89};
		for(int i=1; i<=percentages.length; i++) {
			double percentage = percentages[percentages.length-i];
			output.set(i, input.get(i)*(1.0-percentage));
		}
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

		return 0;
	}

}
