package execution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static execution.Constants.INTERNAL_RESOLUTION;

import javax.swing.JOptionPane;

public class Utility {
	
	public static ArrayList<Integer> doubleListToIntegerList(ArrayList<Double> input) {	
		java.util.ArrayList<Integer> output = new java.util.ArrayList<>();
		for(double element:input) {
			output.add((int) Math.round(element));
		}
		return output;
	}

	public static ArrayList<Double> integerListToDoubleList(ArrayList<Integer> input) {		
		java.util.ArrayList<Double> output = new java.util.ArrayList<>();
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
		java.util.ArrayList<Double> output = new java.util.ArrayList<>();
		for(double element:input) {
			output.add(round(element, places));
		}
		return output;
	}
	
	public static ArrayList<Double> truncateArray(ArrayList<Double> input, int places) {
		java.util.ArrayList<Double> output = new java.util.ArrayList<>();
		for(int i = 0; i<input.size(); i+= places) {
			output.add(input.get(i));
		}
		return output;
	}

	public static List<Double> correctArrayDimensionsAndValuesForVisualization(ArrayList<Double> input, Double targetMaxValue) {
		
		ArrayList<Double> output = new ArrayList<>();
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

	public static double calculateVariance(List<Double> input) {
		double prevValue = 0;
		double totalVariance = 0;
		double maxValue = -1;
		for(Double value:input) {
			totalVariance += Math.max(value, prevValue) - Math.min(value, prevValue);
			if(value>maxValue) maxValue = value;
			prevValue = value;
		}
		return totalVariance/(input.size()*maxValue);
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
		org.json.JSONObject config;
		try {
			config = new org.json.JSONObject(new String(Files.readAllBytes(Paths.get(path))));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Something went wrong:\nError details: unable to read '" + path + "' file.");
			return null;
		}
		return config;
	}
	
	public static ArrayList<Integer> performLinearInterpolationForInt(ArrayList<Integer> array){
		do {
			array = performSingleLinearInterpolationForInt(array);
		} while (array.size()<INTERNAL_RESOLUTION);
		return array;
	}
	
	private static ArrayList<Integer> performSingleLinearInterpolationForInt(ArrayList<Integer> array){
		ArrayList<Integer> output = new ArrayList<>();
		for(int i=0; i<array.size()-1; i++) {
			int current = array.get(i);
			int next = array.get(i+1);
			output.add(current);
			output.add((int)(Math.round((current+next)/2.0)));
		}
		output.add(array.get(array.size()-1));
		return output;
	}
	
	public static ArrayList<Double> performLinearInterpolationForDouble(ArrayList<Double> array){
		do {
			array = performSingleLinearInterpolationForDouble(array);
		} while (array.size()<INTERNAL_RESOLUTION);
		return array;
	}
	
	private static ArrayList<Double> performSingleLinearInterpolationForDouble(ArrayList<Double> array){
		ArrayList<Double> output = new ArrayList<>();
		for(int i=0; i<array.size()-1; i++) {
			double current = array.get(i);
			double next = array.get(i+1);
			output.add(current);
			output.add((current+next)/2.0);
		}
		output.add(array.get(array.size()-1));
		return output;
	}

}
