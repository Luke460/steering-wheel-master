package execution;

import model.Point;

import java.util.ArrayList;
import java.util.List;

public class LineManager {

    private final static int INTERNAL_LINE_RESOLUTION = 100000;

    public static ArrayList<Point> transformIntoLine(List<Double> input){
        ArrayList<Point> output = new ArrayList<>();
        for(int i = 0; i<input.size(); i++){
            output.add(new Point(i*INTERNAL_LINE_RESOLUTION, input.get(i)));
        }
        return output;
    }

    public static ArrayList<Double> transformIntoArray(ArrayList<Point> input){
        ArrayList<Double> output = new ArrayList<>();
        int lastValue = 0;
        for(Point point: input){
            int x = (int) Math.round(point.getX() / INTERNAL_LINE_RESOLUTION);
            while (x>=lastValue) {
                lastValue++;
                output.add(point.getY());
            }
        }
        return output;
    }

}
