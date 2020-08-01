package execution;

import java.util.ArrayList;

public class Aggregator {
	
	public static ArrayList<Double> aggregate(ArrayList<Double> deltaXDeg, int aggregationOrder) {
		ArrayList<Double> output = deltaXDeg;
		for(int state=0; state<aggregationOrder; state++) {
			output = aggregateInternal(output);
		}
		return output;
	}

	public static ArrayList<Double> aggregateInternal(ArrayList<Double> deltaXDeg) {
		ArrayList<Double> aggregateOutput = new ArrayList<Double>();
		aggregateOutput.add(deltaXDeg.get(0)); // first value

		for(int i = 1; i<deltaXDeg.size()-1; i++) {
			double aggregateValue = (deltaXDeg.get(i-1) + deltaXDeg.get(i) + deltaXDeg.get(i+1))/3.0; 
			aggregateOutput.add(aggregateValue);
		}

		aggregateOutput.add(deltaXDeg.get(deltaXDeg.size()-1)); // last value

		return aggregateOutput;
	}

}
