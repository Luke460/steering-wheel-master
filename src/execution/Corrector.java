package execution;

import java.util.ArrayList;

public class Corrector {

	public static ArrayList<Double> adjust(ArrayList<Double> values, String name) {

		int correctionCounter = 0;

		for(int i = 1; i<=values.size()-2; i++) {

			double valueI = values.get(i);
			double valueIp1 = values.get(i+1);
			double valueIp2 = valueIp1;
			double valueIp3 = valueIp2;
			if(i <= values.size()-3) valueIp2 = values.get(i+2);
			if(i <= values.size()-4) valueIp3 = values.get(i+3);
			double valueIm1 = values.get(i-1);
			double valueIm2 = valueIm1;
			double valueIm3 = valueIm2;
			if(i >= 2) valueIm2 = values.get(i-2);
			if(i >= 3) valueIm3 = values.get(i-3);

			if(	(valueI < valueIm1 && valueI < valueIm2 && valueI < valueIm3) ||
					(valueI > valueIp1 && valueI > valueIp2 && valueI > valueIp3) ) {
				// error not acceptable - spike detected
				valueI = (valueIm1 + valueIp1)/2;
				values.set(i,valueI);
				correctionCounter++;
			}

		}

		System.out.println("[" + name + "] adjusted wrong values: " + correctionCounter);

		return values;

	}

}
