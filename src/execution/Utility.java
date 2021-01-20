package execution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

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

	public static List<Double> correctArrayDimensionsAndValuesForVisualizzation(ArrayList<Double> input, Double targetMaxValue) {
		
		ArrayList<Double> output = new ArrayList<Double>();
		double inputMaxValue = Collections.max(input);
		for(double value:input) {
			double newValue = (value*targetMaxValue)/inputMaxValue;
			output.add(newValue);
		}
		
		return output;
		
	}
	
	public static boolean isGrowingForDoubleList(List<Double> input) {
		double prevValue = -1;
		for(Double value:input) {
			if(prevValue>value) return false;
			prevValue = value;
		}
		return true;
	}
	
	public static boolean isGrowingForIntegerList(List<Integer> input) {
		double prevValue = -1;
		for(Integer value:input) {
			if(prevValue>value) return false;
			prevValue = value;
		}
		return true;
	}
	
	public static org.json.JSONObject readConfiguration(String path){
		// Read Configuration
		org.json.JSONObject config = null;
		try {
			config = new org.json.JSONObject(new String(Files.readAllBytes(Paths.get(path))));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + path + "' file.");
			return null;
		}
		return config;
	}

}
