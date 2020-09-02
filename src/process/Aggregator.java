package process;

import java.util.ArrayList;

import execution.Utility;

public class Aggregator {
	
	public static ArrayList<Double> aggregate(ArrayList<Double> values, int aggregationOrder) {
		for(int state=0; state<aggregationOrder; state++) {
			values = aggregateInternal(values);
		}
		return values;
	}

	public static ArrayList<Double> aggregateInternal(ArrayList<Double> values) {
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
		ArrayList<Double> aggregateInput = new ArrayList<Double>();
		aggregateInput.addAll(input);
		int plus = 0;
		if(input.size()<=51) {
			plus = 1; 
		} else if (input.size()<=101) {
			plus = 2; 
		} else if (input.size()<=201) {
			plus = 3; 
		} else {
			plus = 4; 
		}
		// This is possible because in vertical aggregation: 
		// Ag_1(Ag_1(x)) = Ag_2(x)
		int i;
		for (i = 0; i<=5; i++) {
			if(Utility.isGrowing(aggregateInput)) {
				return i + plus; //i min value 0, i max value 5
			} else {
				aggregateInput = aggregate(aggregateInput, 1);
			}
		}
		return i + plus;
	}

}
