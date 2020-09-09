package userInterface;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class LineOfValues {

	private Color color;
	private List<Double> values;
	
	public LineOfValues(Color color, List<Double> values) {
		super();
		this.color = color;
		this.values = values;
	}
	
	public double maxValue() {
		return Collections.max(this.values);
	}
	
	public int size() {
		return this.values.size();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}
	
}
