package execution;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
	private int maxValue;
	private static final int PREF_W = 800;
	private static final int PREF_H = 600;
	private static final int BORDER_GAP = 30;
	private static final Color GRAPH_COLOR_1 = Color.red;
	private static final Color GRAPH_COLOR_2 = Color.blue;
	private static final Color GRAPH_POINT_COLOR_1 = Color.red;
	private static final Color GRAPH_POINT_COLOR_2 = Color.blue;
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static final int GRAPH_POINT_WIDTH = 3;
	private static final int Y_HATCH_CNT = 10;
	private List<Integer> values1;
	private List<Integer> values2;

	public DrawGraph(List<Integer> values1, List<Integer> values2) {
		this.values1 = values1;
		this.values2 = values2;
		this.maxValue = Math.max(Collections.max(values1),Collections.max(values2));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (values1.size() - 1);
		double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (maxValue - 1);

		List<Point> graphPoints1 = new ArrayList<Point>();
		for (int i = 0; i < values1.size(); i++) {
			int x1 = (int) (i * xScale + BORDER_GAP);
			int y1 = (int) ((maxValue - values1.get(i)) * yScale + BORDER_GAP);
			graphPoints1.add(new Point(x1, y1));
		}

		List<Point> graphPoints2 = new ArrayList<Point>();
		for (int i = 0; i < values2.size(); i++) {
			int x1 = (int) (i * xScale + BORDER_GAP);
			int y1 = (int) ((maxValue - values2.get(i)) * yScale + BORDER_GAP);
			graphPoints2.add(new Point(x1, y1));
		}

		// create x and y axes 
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

		// create hatch marks for y axis. 
		for (int i = 0; i < Y_HATCH_CNT; i++) {
			int x0 = BORDER_GAP;
			int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
			int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
			int y1 = y0;
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < values1.size() - 1; i++) {
			int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (values1.size() - 1) + BORDER_GAP;
			int x1 = x0;
			int y0 = getHeight() - BORDER_GAP;
			int y1 = y0 - GRAPH_POINT_WIDTH;
			g2.drawLine(x0, y0, x1, y1);
		}

		Stroke oldStroke1 = g2.getStroke();
		g2.setColor(GRAPH_COLOR_1);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints1.size() - 1; i++) {
			int x1 = graphPoints1.get(i).x;
			int y1 = graphPoints1.get(i).y;
			int x2 = graphPoints1.get(i + 1).x;
			int y2 = graphPoints1.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);         
		}

		Stroke oldStroke2 = g2.getStroke();
		g2.setColor(GRAPH_COLOR_2);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints1.size() - 1; i++) {
			int x1 = graphPoints2.get(i).x;
			int y1 = graphPoints2.get(i).y;
			int x2 = graphPoints2.get(i + 1).x;
			int y2 = graphPoints2.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);         
		}

		g2.setStroke(oldStroke1);      
		g2.setColor(GRAPH_POINT_COLOR_1);
		for (int i = 0; i < graphPoints1.size(); i++) {
			int x = graphPoints1.get(i).x - GRAPH_POINT_WIDTH / 2;
			int y = graphPoints1.get(i).y - GRAPH_POINT_WIDTH / 2;;
			int ovalW = GRAPH_POINT_WIDTH;
			int ovalH = GRAPH_POINT_WIDTH;
			g2.fillOval(x, y, ovalW, ovalH);
		}

		g2.setStroke(oldStroke2);      
		g2.setColor(GRAPH_POINT_COLOR_2);
		for (int i = 0; i < graphPoints2.size(); i++) {
			int x = graphPoints2.get(i).x - GRAPH_POINT_WIDTH / 2;
			int y = graphPoints2.get(i).y - GRAPH_POINT_WIDTH / 2;;
			int ovalW = GRAPH_POINT_WIDTH;
			int ovalH = GRAPH_POINT_WIDTH;
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	public static void createAndShowGui(List<Integer> deltaX, List<Integer> aggregateDeltaX) {

		DrawGraph mainPanel = new DrawGraph(deltaX, aggregateDeltaX);

		JFrame frame = new JFrame("DrawGraph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setVisible(true);
	}
}