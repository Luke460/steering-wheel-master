package execution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utility {
	
	public static ArrayList<Integer> doubleListToIntegerList(ArrayList<Double> input) {	
		java.util.ArrayList<Integer> output = new java.util.ArrayList<Integer>();
		for(double element:input) {
			output.add((int) Math.round(element));
		}
		return output;
	}

	public static ArrayList<Double> integerListToDoubleList(ArrayList<Integer> input) {		
		java.util.ArrayList<Double> output = new java.util.ArrayList<Double>();
		for(double element:input) {
			output.add(element);
		}
		return output;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_EVEN);
	    return bd.doubleValue();
	}
	
	public static ArrayList<Double> round(ArrayList<Double> input, int places) {		
		java.util.ArrayList<Double> output = new java.util.ArrayList<Double>();
		for(double element:input) {
			output.add(round(element, places));
		}
		return output;
	}
	
	public static ArrayList<Double> truncateArray(ArrayList<Double> input, int places) {
		java.util.ArrayList<Double> output = new java.util.ArrayList<Double>();
		for(int i = 0; i<input.size(); i+= places) {
			output.add(input.get(i));
		}
		return output;
	}

	public static List<Double> correctArrayDimensionsAndValues(ArrayList<Double> input, int targetSize, Double targetMaxValue) {
		List<Double> output = new ArrayList<Double>();
		double inputMaxValue = Collections.max(input);
		
		for(int i = 0; i<targetSize; i++) {
			int j = (int) Math.round((i*(double)input.size())/targetSize);
			double newValue = (input.get(j)*targetMaxValue)/inputMaxValue;
			output.add(newValue);
		}
		
		double diff = targetMaxValue-Collections.max(output);
		if(diff > 0) {
			for(int i=0; i<targetSize; i++) {
				double correctedValue = output.get(i) + (((double)i/(double)(targetSize-1.0))*diff);
				output.set(i, correctedValue);
			}
		}
		
		return output;
		
	}

}
