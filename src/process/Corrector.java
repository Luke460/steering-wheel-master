package process;

import utililty.SimpleLogger;

import java.util.ArrayList;
import java.util.Collections;

public class Corrector {

	public static ArrayList<Double> adjust(ArrayList<Double> inputValues, String name) {
		ArrayList<Double> outputValues = new ArrayList<>(inputValues);
		int spikeCounter = 0;

		for(int i = 1; i<=outputValues.size()-2; i++) {

			double valueI0 = outputValues.get(i);
			double valueIp1 = outputValues.get(i+1);
			double valueIm1 = outputValues.get(i-1);
			double deltaI = ((valueIp1-valueI0)+(valueI0-valueIm1))/2.0;
			double valueIp2 = valueIp1 + deltaI;
			double valueIp3 = valueIp2 + deltaI;
			double valueIp4 = valueIp3 + deltaI;
			if(i <= outputValues.size()-3) valueIp2 = outputValues.get(i+2);
			if(i <= outputValues.size()-4) valueIp3 = outputValues.get(i+3);
			if(i <= outputValues.size()-5) valueIp4 = outputValues.get(i+4);
			double valueIm2 = valueIm1 - deltaI;
			double valueIm3 = valueIm2 - deltaI;
			double valueIm4 = valueIm3 - deltaI;
			if(i >= 2) valueIm2 = outputValues.get(i-2);
			if(i >= 3) valueIm3 = outputValues.get(i-3);
			if(i >= 4) valueIm4 = outputValues.get(i-4);
			
			ArrayList<Double> sortedValues = new ArrayList<>();
			
			sortedValues.add(valueIm1); // 0
			sortedValues.add(valueIm2); // 1    - - - -
			sortedValues.add(valueIm3); // 2 <- low value limit (after sorting)
			sortedValues.add(valueIm4); // 3
			sortedValues.add(valueI0);  // 4 <- mid value (after sorting)
			sortedValues.add(valueIp1); // 5
			sortedValues.add(valueIp2); // 6 <- high value limit (after sorting)
			sortedValues.add(valueIp3); // 7    + + + +
			sortedValues.add(valueIp4); // 8
			
			Collections.sort(sortedValues);
			
			if (valueI0 < sortedValues.get(2)) {
				// error not acceptable - spike detected
				outputValues.set(i, sortedValues.get(2));
				spikeCounter++;
			} else if(valueI0 > sortedValues.get(6)) {
				// error not acceptable - spike detected
				outputValues.set(i, sortedValues.get(6));
				spikeCounter++;
			}
			
		}

		SimpleLogger.infoLog("[" + name + "] adjusted wrong values: ");
		SimpleLogger.infoLog("spikes corrected: " + spikeCounter);

		return outputValues;

	}

}
