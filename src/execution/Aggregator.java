package execution;

import java.util.ArrayList;

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

}
