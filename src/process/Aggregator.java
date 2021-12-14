package process;

import java.util.ArrayList;

import execution.Utility;

public class Aggregator {
	
	public static ArrayList<Double> performExperimentalAggregation(ArrayList<Double> values, int aggregationOrder) {
		if(aggregationOrder<1) return values;
		values = prepareFirstValuesForAggregation(values);
		values = performAggregation(values, aggregationOrder);
		values = removeNegativeValues(values);
		return values;
		
	}
	
	public static ArrayList<Double> performAggregation(ArrayList<Double> values, int aggregationOrder) {
		for(int state=0; state<aggregationOrder; state++) {
			values = aggregateInternal(values);
		}
		return values;
		
	}

	private static ArrayList<Double> aggregateInternal(ArrayList<Double> values) {
		ArrayList<Double> aggregateOutput = new ArrayList<Double>();
		aggregateOutput.add(values.get(0)); // first value

		for(int i = 1; i<values.size()-1; i++) {
			double aggregateValue = (values.get(i-1) + values.get(i) + values.get(i+1))/3.0; 
			aggregateOutput.add(aggregateValue);
		}

		aggregateOutput.add(values.get(values.size()-1)); // last value

		return aggregateOutput;
	}
	
	public static int suggestedAggregationValue(ArrayList<Double> input) {
		// The best part is no part, the best process is no process. [Elon Musk]
		if(input.size()<=75){
			// small size: min 2 max 4
			return 3;
		} else if(input.size()>75 && input.size()<=125){
			// standard size: min 3 max 5
			return 4;
		} else {
			// big size: min 4 max 6
			return 5;
		}
	}
	
	public static ArrayList<Double> prepareFirstValuesForAggregation(ArrayList<Double> values) {
		ArrayList<Double> output = new ArrayList<Double>();
		int lastZeroPosition = 0;
		double requiredValue = values.get(values.size()-1)*0.01;
		// first value must be 0
		for(int i = 1; i<values.size(); i++) {
			if(values.get(i)>=requiredValue && values.get(i-1)>=requiredValue) {
				lastZeroPosition = i-2;
				break;
			}
		}
		lastZeroPosition = Math.max(0, lastZeroPosition);
		// System.out.print("lastZeroPosition="+lastZeroPosition);
		output.addAll(values);
		// reverse values
		for(int i = 0; i<lastZeroPosition; i++) {
			double value = values.get(2*lastZeroPosition-i);
			output.set(i, -value);
		}	
		return output;
	}
	
	public static ArrayList<Double> removeNegativeValues(ArrayList<Double> values) {
		ArrayList<Double> output = new ArrayList<Double>();
		for(double value:values) {
			if(value>0) {
				output.add(value);
			} else {
				output.add(0.0);
			}
		}
		return output;
	}

}
