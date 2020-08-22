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
			i = Utility.round(i,2);
			double targetDeltaX = i*maxDeltaX;
			int x = findIndexOfLowerValue(aggregateDeltaXdouble, targetDeltaX);
			
			double selectedForce = force.get(x);
			double selectedDeltaX = aggregateDeltaXdouble.get(x);
			double deltaPercentage = (targetDeltaX-selectedDeltaX) / maxDeltaX;
			double correctValue1 = (selectedForce*(1.0+deltaPercentage));
			
			double selectedForcePlus = force.get(x+1);
			double selectedDeltaXPlus = aggregateDeltaXdouble.get(x+1);
			double deltaMinusPercentage = (targetDeltaX-selectedDeltaXPlus) / maxDeltaX;
			double correctValue2 = (selectedForcePlus*(1.0+deltaMinusPercentage));
			
			double correctForce = (correctValue1 + correctValue2)/2.0;
			if(correctForce>maxforce) {
				correctForce = maxforce;
			}
			// round
			// correctValue = Utility.roundBy4(correctValue);
			if(aggregateDeltaXdouble.get(x)==0) {
				correctForce = 0;
			}
			correctForce = Utility.round((correctForce/maxforce),3);
			corrections.add(correctForce);
		}

		return corrections;

	}

	private static int findIndexOfLowerValue(ArrayList<Double> aggregateDeltaXdouble, double targetValue) {
		
		if(targetValue==0) return 0;
		
		for(int i=0; i<aggregateDeltaXdouble.size();i++) {
			if(i!=0 && targetValue>=aggregateDeltaXdouble.get(i-1) && targetValue<=aggregateDeltaXdouble.get(i)){
				return i-1;
			}
		}

		return 0;
	}

}
