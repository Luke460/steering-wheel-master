package utility;

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

	public static List<Double> correctArrayDimensionsAndValuesForVisualization(ArrayList<Double> input, Double targetMaxValue) {
		
		ArrayList<Double> output = new ArrayList<>();
		double inputMaxValue = Collections.max(input);
		for(double value:input) {
			double newValue = (value*targetMaxValue)/inputMaxValue;
			output.add(newValue);
		}
		
		return output;
		
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

	public static double getValueBetweenPoints(double x1, double x2, double v1, double v2, double xt){
		if(xt==x1) return v1;
		if(xt==x2) return v2;
		if(xt<x1){
			SimpleLogger.errorLog("x1: " + x1 + " | x2: " + x2 + " | v1: " + v1 + " | v2: " + v2 + " | xt: " + xt);
			throw new IllegalArgumentException("xt out of bounds: xt lower than x1");
		}
		if(xt>x2) {
			SimpleLogger.errorLog("x1: " + x1 + " | x2: " + x2 + " | v1: " + v1 + " | v2: " + v2 + " | xt: " + xt);
			throw new IllegalArgumentException("xt out of bounds: xt greater than x1");
		}
		double vt = (((x1-xt)*(v2-v1))/(x1-x2))+v1;
		return vt;
	}

}
