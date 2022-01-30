package execution;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class LineManager {

    public static ArrayList<Point> transformIntoLine(List<Double> input){
        ArrayList<Point> output = new ArrayList<>();
        for(int i = 0; i<input.size(); i++){
            output.add(new Point(i, input.get(i)));
        }
        return output;
    }

    public static ArrayList<Double> transformIntoFixedArray(ArrayList<Point> input){
        ArrayList<Double> output = new ArrayList<>();
        double fixedLength = Constants.INTERNAL_RESOLUTION;
        double lastX = input.get(input.size()-1).getX();
        double stepX = lastX/fixedLength;
        for(double i=0; i<lastX-(stepX*0.5); i+=stepX){
            ArrayList<Point> valuesX = getCloseValues(input, i);
            Point minP = valuesX.get(0);
            Point maxP = valuesX.get(1);
            double newX = Utility.getValueBetweenPoints(minP.getX(), maxP.getX(), minP.getY(), maxP.getY(), i);
            output.add(newX);
        }
        output.add(input.get(input.size()-1).getY());
        return output;
    }

    private static ArrayList<Point> getCloseValues(ArrayList<Point> input, double target) {
        ArrayList<Point> output = new ArrayList<>();
        for(int i = 0; i< input.size()-1; i++){
            double x = input.get(i).getX();
            double xp1 = input.get(i+1).getX();
            if(target>=x && target<= xp1){
                Point p1 = new Point(x, input.get(i).getY());
                Point p2 = new Point(xp1, input.get(i+1).getY());
                output.add(p1);
                output.add(p2);
                return output;
            }
        }
        SimpleLogger.errorLog("getCloseValues: point not found");
        return output;
    }

}
