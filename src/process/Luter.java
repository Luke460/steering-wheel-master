package process;

import java.util.ArrayList;
import java.util.Collections;

import execution.Constants;
import execution.LineManager;
import execution.SimpleLogger;
import execution.Utility;
import model.Point;

public class Luter {

	public static ArrayList<Double> generateCorrectiveArray(ArrayList<Integer> force, ArrayList<Double> delta) {
		ArrayList<Point> output = new ArrayList<>();
		double maxForce = Collections.max(force);
		double maxDelta = Collections.max(delta);
		for(int i=0; i<=force.size()-1; i++){
			Point p = new Point(delta.get(i)/maxDelta, force.get(i)/maxForce);
			output.add(p);
		}
		return LineManager.transformIntoFixedArray(output);
	}

	public static ArrayList<Double> enhanceDeadZone(ArrayList<Double> input, double inputDeadZoneEnhancement) {
		ArrayList<Double> output = new ArrayList<>(input);
		output.set(input.size()-1, input.get(input.size()-1));
		double l = input.size()-1;
		double k = (inputDeadZoneEnhancement * 0.0125 *l)+1.0;
		for(int i = input.size()-2; i>=1; i--) {
			// increase every x in a progressive manner
			double deltaX = ((k-1.0)*(l-i))/l;
			double targetX = i-deltaX;
			int x1 = (int) targetX;
			int x2 = x1+1;
			double v1, v2;
			double value;
			if(x1>=1) {
				v1 = input.get(x1);
				v2 = input.get(x2);
				value = Utility.getValueBetweenPoints(x1, x2, v1, v2, targetX);
			} else {
				value = 0;
			}
			if(value<0) {
				value=0.0;
			}
			output.set(i, Utility.round(value,8));
		}
		output.set(0, 0.0);
		output = fixInitialValues(output);
		return output;
	}

	private static ArrayList<Double> fixInitialValuesWithRotation(ArrayList<Double> input) {
		// clean first ten values
		ArrayList<Double> array = cleanFirstXValues(input);
		// correct
		int startingIndex = -1;
		double minValue = array.get(1);
		int count = 0;
		for(int i = array.size()-1; i>0; i--){
			double value = array.get(i);
			if(startingIndex == -1 && value == 0){
				startingIndex = i+1;
				minValue = array.get(i+1);
			}
			if(value == 0) {
				count++;
				double valueToRotate = array.get(startingIndex+count);
				value = minValue - (valueToRotate - minValue);
				array.set(i, value);
			}
		}
		return array;
	}

	private static ArrayList<Double> fixInitialValues(ArrayList<Double> input) {
		// clean first ten values
		final int positions =  (int) (Constants.INTERNAL_RESOLUTION*0.025);
		double subMinDelta = 0;
		ArrayList<Double> array = cleanFirstXValues(input);
		// correct
		boolean start = true;
		for(int i = array.size()-1; i>0; i--){
			double value = array.get(i);
			if(start  && value == 0){
				start = false;
				int startingIndex = i+1;
				subMinDelta = (array.get(startingIndex+positions) - array.get(startingIndex))/(positions*1.0);
				i=i+positions;
			}
			if(!start) {
				value = array.get(i+1) - subMinDelta;
				if(value<0) {
					SimpleLogger.warningLog("forced to zero a negative lut value");
					value = 0;
				}
				array.set(i, value);
			}
		}
		return array;
	}

	private static ArrayList<Double> cleanFirstXValues(ArrayList<Double> input) {
		int valuesToIgnore = (int) (Constants.INTERNAL_RESOLUTION*0.05);
		ArrayList<Double> output = new ArrayList<>(input);
		for(int i=0; i<= input.size()-1; i++){
			double value = input.get(i);
			if(value>0 && valuesToIgnore>0){
				output.set(i, 0.0);
				valuesToIgnore--;
			}
			if(valuesToIgnore<=0) break;
		}
		return output;
	}
	
	public static ArrayList<Double> deadZoneCorrectionOnly(ArrayList<Integer> force, ArrayList<Double> aggregateDeltaXdouble){
		int x = findIndexOfLowerValue(aggregateDeltaXdouble, Collections.max(aggregateDeltaXdouble)*0.02);
		double firstLutValue = (force.get(x)*1.0)/(Collections.max(force)*1.0);
		SimpleLogger.infoLog("firstLutValue: " + firstLutValue);
		return generateLinearizedLut(firstLutValue);
	}

	public static ArrayList<Double> reduceCurve(ArrayList<Double> inputList, int alterationParameter) {
		ArrayList<Double> output = new ArrayList<>();
		double prevValue = 0.0;
		int count = 0;
		for(int i=0; i<inputList.size(); i++) {
			if(i!=0 && i%((40/alterationParameter))==0){
				double customValue = (prevValue + inputList.get(count))/2;
						output.add(customValue);
			} else {
				output.add(inputList.get(count));
				prevValue = inputList.get(count);
				count++;
			}
		}
		return output;
	}

	public static ArrayList<Double> alterLutCurve(ArrayList<Double> inputList, int alterationParameter) {
		ArrayList<Point> input = LineManager.transformIntoLine(inputList);
		double c = 0.015;
		ArrayList<Point> output = new ArrayList<>();
		double maxX = input.get(input.size()-1).getX();
		for(Point point: input) {
			double xRel = point.getX()/maxX;
			double deltaX;
			if(xRel<0.5){
				deltaX = Math.sin(xRel*Math.PI)*alterationParameter*c*(inputList.size()-1);
			} else {
				deltaX = Math.sqrt(Math.sin(xRel*Math.PI))*alterationParameter*c*(inputList.size()-1);
			}
			Point newPoint = new Point(point.getX()-deltaX, point.getY());
			output.add(newPoint);
		}
		return LineManager.transformIntoFixedArray(output);
	}

	public static ArrayList<Double> consistencyCheck(ArrayList<Double> input) {
		ArrayList<Double> output = new ArrayList<>();
		int consistencyCorrections = 0;
		int duplicatedValues = 0;
		int decreasingValues = 0;
		if(input.get(0)!=0.0){
			consistencyCorrections++;
			SimpleLogger.warningLog("First lut value was not 0");
		}
		output.add(0.0);
		double lastValue = 0.0;
		for(int i = 1; i<=input.size()-1; i++) {
			double currentValue = input.get(i);
			if(currentValue>1.0){
				currentValue = 1.0;
				consistencyCorrections++;
				SimpleLogger.warningLog("Found clipping in the lut");
			}
			if(currentValue==lastValue){
				duplicatedValues++;
				//SimpleLogger.warningLog("Found duplicated value in the lut");
			}
			if(currentValue<lastValue){
				currentValue = lastValue;
				consistencyCorrections++;
				decreasingValues++;
				//SimpleLogger.warningLog("Found decreasing value in the lut");
			}
			output.add(currentValue);
			lastValue = currentValue;
		}
		SimpleLogger.infoLog("Consistency corrections: " + consistencyCorrections);
		SimpleLogger.infoLog("Duplicated lut values count: " + duplicatedValues);
		SimpleLogger.infoLog("Decreasing lut values count: " + decreasingValues);
		return output;
	}
	
	// private methods

	private static ArrayList<Double> generateLinearizedLut(double firstLutValue) {
		ArrayList<Double> output = new ArrayList<>();
		output.add(0.0);
		output.add(firstLutValue);
		double delta = (1-firstLutValue)/(Constants.INTERNAL_RESOLUTION -1.0);
		for(int i = 2; i< Constants.INTERNAL_RESOLUTION; i++) {
			output.add(output.get(i-1)+delta);
		}
		output.add(1.0);
		return output;
	}
	
	private static int findIndexOfLowerValue(ArrayList<Double> input, double targetValue) {
		
		if(targetValue==0) return 0;
		// from last value
		for(int i=input.size()-1; i>0;i--) {
			if(input.get(i)<targetValue){
				return i;
			}
		}

		return 0;
	}

    public static ArrayList<Double> calculateLutResult(ArrayList<Double> inputDelta, ArrayList<Double> correctiveMap) {
		ArrayList<Double> output = new ArrayList<>();
		double lutResolution=Constants.INTERNAL_RESOLUTION;
		for (int i = 0; i < lutResolution; i++){
			// game force from 0.0 to 1.0
			double gameForce = i/(lutResolution-1);
			int lutIndex = (int) Math.round(gameForce*lutResolution);
			double lutResult = correctiveMap.get(lutIndex);
			// lutResult/1.0 : index:inputForce.size()-1
			int index = (int) Math.round((lutResult*(inputDelta.size()-1))/1.0);
			double simulatedReaction = inputDelta.get(index);
			output.add(simulatedReaction);
		}
		return output;
    }
}
