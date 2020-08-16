package execution;

import java.util.ArrayList;

public class Corrector {

	public static ArrayList<Double> adjust(ArrayList<Double> values, String name) {

		ArrayList<Double> output = new ArrayList<Double>();
		
		int minorErrorCounter = 0;
		int spikeCounter = 0;
		
		output.add(values.get(0));

		for(int i = 1; i<=values.size()-2; i++) {

			double valueI0 = values.get(i);
			double valueIp1 = values.get(i+1);
			double valueIm1 = values.get(i-1);
			double deltaI = ((valueIp1-valueI0)+(valueI0-valueIm1))/2.0;
			double valueIp2 = valueIp1 + deltaI;
			double valueIp3 = valueIp2 + deltaI;
			double valueIp4 = valueIp3 + deltaI;
			if(i <= values.size()-3) valueIp2 = values.get(i+2);
			if(i <= values.size()-4) valueIp3 = values.get(i+3);
			if(i <= values.size()-5) valueIp4 = values.get(i+4);
			double valueIm2 = valueIm1 - deltaI;
			double valueIm3 = valueIm2 - deltaI;
			double valueIm4 = valueIm3 - deltaI;
			if(i >= 2) valueIm2 = values.get(i-2);
			if(i >= 3) valueIm3 = values.get(i-3);
			if(i >= 4) valueIm4 = values.get(i-4);
			
			int wrongMeasureCounter = 0;
			
			if(valueI0 < valueIm1 || valueI0 > valueIp1) {
				wrongMeasureCounter++;
			}
			if(valueI0 < valueIm2 || valueI0 > valueIp2) {
				wrongMeasureCounter++;
			}
			if(valueI0 < valueIm3 || valueI0 > valueIp3) {
				wrongMeasureCounter++;
			}
			if(valueI0 < valueIm4 || valueI0 > valueIp4) {
				wrongMeasureCounter++;
			}
			
			if(wrongMeasureCounter==2) {
				// minor error: ignored
				// aggregation will fix this value
				minorErrorCounter++;
			} else if (wrongMeasureCounter>=3) {
				// error not acceptable - spike detected
				valueI0 = (valueIm1 + valueIp1)/2.0;
				spikeCounter++;
			}
			
			output.add(valueI0);
		}
		
		output.add(values.get(values.size()-1));

		System.out.println("[" + name + "] adjusted wrong values: ");
		System.out.println(" - minor errors: " + minorErrorCounter);
		System.out.println(" - spikes corrected: " + spikeCounter);

		return output;

	}

}
