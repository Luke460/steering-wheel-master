package process;

import java.util.ArrayList;

public class Aggregator {
	
	public static ArrayList<Double> performExperimentalAggregation(ArrayList<Double> inputValues, int aggregationOrder) {
		ArrayList<Double> outputValues = new ArrayList<>(inputValues);
		if(aggregationOrder<1) return outputValues;
		outputValues = linearizeFirstValues(outputValues);
		outputValues = performAggregation(outputValues, aggregationOrder);
		outputValues = removeNegativeValues(outputValues);
		return outputValues;
		
	}
	
	public static ArrayList<Double> performAggregation(ArrayList<Double> inputValues, int aggregationOrder) {
		ArrayList<Double> outputValues = new ArrayList<>(inputValues);
		for(int state=0; state<aggregationOrder; state++) {
			outputValues = aggregateInternal(outputValues);
		}
		return outputValues;
		
	}

	private static ArrayList<Double> aggregateInternal(ArrayList<Double> values) {
		ArrayList<Double> aggregateOutput = new ArrayList<>();
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
			// small size(50): 3
			return 3;
		} else if(input.size()<=125){
			// standard size(100): 4
			return 4;
		} else if(input.size()<=175){
			// big size(150): 5
			return 5;
		} else if(input.size()<=225){
			// large size(200): 6
			return 6;
		} else if(input.size()<=275){
			// huge size(250): 7
			return 7;
		} else {
			// you are definitely insane: 8
			return 8;
		}
	}
	
	public static ArrayList<Double> linearizeFirstValues(ArrayList<Double> values) {
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
		ArrayList<Double> output = new ArrayList<>(values);
		// reverse values
		for(int i = 0; i<lastZeroPosition; i++) {
			double value = values.get(2*lastZeroPosition-i);
			output.set(i, -value);
		}	
		return output;
	}
	
	private static ArrayList<Double> removeNegativeValues(ArrayList<Double> values) {
		ArrayList<Double> output = new ArrayList<>();
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
