package process;

import execution.Utility;
import java.util.ArrayList;

public class Calculator {

    public static ArrayList<Double> customizeLut(ArrayList<Double> correctiveMap, double deadZone, double gain, double gamma){
        ArrayList<Double> customLut = new ArrayList<Double>();
        int l = correctiveMap.size(); // points number
        double d = deadZone; // dead zone percentage value
        double s = gain; // or saturation
        double g = gamma; // shape of the series
        for(int x=0; x<l; x++){
            double y = x+(d*((l-x)/l)) - (l-s)*(x/l) + (((Math.pow((l/2d)-x,2))/l)-(l/4d))*((g*100d)/l)*((s/100d)-(d/100d));
            //         ^x      ^dead zone     ^saturation                         ^gamma
            customLut.add(Utility.round(y*(1/(l*1d)),4));
        }
        return customLut;
    }

}
