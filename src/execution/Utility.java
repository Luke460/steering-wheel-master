package execution;

import java.util.ArrayList;

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
	
	public static double roundBy2(double correctValue) {
		long rcv = Math.round(correctValue*100);
		correctValue = rcv/100.0;
		return correctValue;
	}
	
	public static double roundBy5(double correctValue) {
		long rcv = Math.round(correctValue*100000.0);
		correctValue = rcv/100000.0;
		return correctValue;
	}

}
