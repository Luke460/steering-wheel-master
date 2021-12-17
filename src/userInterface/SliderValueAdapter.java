package userInterface;

import execution.Utility;

import javax.swing.*;
import java.util.Hashtable;

public class SliderValueAdapter {

    public static int getGainSliderValue(double actualValue){
        double sliderValueMax = 50d;
        double actualValueMin = 0.5d;
        double actualValueMax = 1d;
        return getSliderValue(actualValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    public static double getGainActualValue(double sliderValue){
        double sliderValueMax = 50d;
        double actualValueMin = 0.5d;
        double actualValueMax = 1d;
        return getActualValue(sliderValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    public static int getGammaSliderValue(double actualValue){
        double sliderValueMax = 16d;
        double actualValueMin = -1d;
        double actualValueMax = 1d;
        return getSliderValue(actualValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    public static double getGammaActualValue(double sliderValue){
        double sliderValueMax = 16d;
        double actualValueMin = -1d;
        double actualValueMax = 1d;
        return getActualValue(sliderValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    public static int getDeadZoneSliderValue(double actualValue){
        double sliderValueMax = 80d;
        double actualValueMin = 0d;
        double actualValueMax = 0.2d;
        return getSliderValue(actualValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    public static double getDeadZoneActualValue(double sliderValue){
        double sliderValueMax = 80d;
        double actualValueMin = 0d;
        double actualValueMax = 0.2d;
        return getActualValue(sliderValue, actualValueMax, actualValueMin, sliderValueMax);
    }

    private static int getSliderValue(double actualValue, double actualValueMax, double actualValueMin, double sliderValueMax){
        double value = ((actualValue-actualValueMin)*sliderValueMax)/(actualValueMax-actualValueMin);
        return (int) (Math.round(value));
    }

    private static double getActualValue(double sliderValue, double actualValueMax, double actualValueMin, double sliderValueMax){
        double value = ((sliderValue*(actualValueMax-actualValueMin))/sliderValueMax)+actualValueMin;
        return Utility.round(value,4);
    }

    /*
    Hashtable<Integer, JLabel> gammaPosition = new Hashtable<Integer, JLabel>();
		gammaPosition.put(0, new JLabel("-1"));
     */

    public static Hashtable<Integer, JLabel> getTicksLabel(double minLabelValue, double maxLabelValue, int labelDecimals, int sliderTicksNumber, int sliderLabelTicksInterval){
        Hashtable<Integer, JLabel> labelMap = new Hashtable<>();
        double deltaLabelTick = (maxLabelValue-minLabelValue)/(sliderTicksNumber*1d);
        for(int i=0;i<=sliderTicksNumber;i+=sliderLabelTicksInterval){
            double labelValue = Utility.round((minLabelValue + deltaLabelTick*i),labelDecimals);
            if(labelValue % 1 == 0) {
                labelMap.put(i,new JLabel(Math.round(labelValue)+""));
            } else {
                labelMap.put(i, new JLabel(labelValue + ""));
            }
        }
        return labelMap;
    }

}
