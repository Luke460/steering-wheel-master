package process;

import java.util.ArrayList;
import java.util.Collections;

import execution.Utility;

public class Luter {

	public static ArrayList<Double> generateCorrectiveArray(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble) {

		ArrayList<Double> corrections = new ArrayList<Double>();
		Double maxDeltaX = Collections.max(aggregateDeltaXdouble);
		Double maxforce = Collections.max(force) + 0.0;

		for(double i = 0; i<= 1; i+=0.01) {
			i = Utility.roundBy2(i);
			double targetValue = i*maxDeltaX;
			int x = findIndexOfLowerValue(aggregateDeltaXdouble, targetValue);
			double deltaPercentage = (targetValue-aggregateDeltaXdouble.get(x)) / maxDeltaX;
			double correctValue = (force.get(x)+(1.0+deltaPercentage))/maxforce;
			// round
			// correctValue = Utility.roundBy4(correctValue);
			if(aggregateDeltaXdouble.get(x)==0) {
				correctValue = 0;
			}
			correctValue = Utility.roundBy5(correctValue);
			corrections.add(correctValue);
		}

		return corrections;

	}

	private static int findIndexOfLowerValue(ArrayList<Double> aggregateDeltaXdouble, double targetValue) {
		
		if(targetValue==0) return 0;
		
		for(int i=0; i<aggregateDeltaXdouble.size();i++) {
			if(i!=0 && targetValue>=aggregateDeltaXdouble.get(i-1) && targetValue<=aggregateDeltaXdouble.get(i)){
				return i;
			}
		}

		return 0;
	}

}
