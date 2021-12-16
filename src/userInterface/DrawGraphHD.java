package userInterface;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static execution.Constants.INFO_PATH;
import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGraphHD extends JPanel {
	private static final Dimension GRAPH_DIMENSION = new Dimension(720, 720);
	private static final int BORDER_GAP = 20;
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static final int X_HATCH_CNT = 20;
	private static final int Y_HATCH_CNT = 20;
	private List<LineOfValues> allLines;


	public DrawGraphHD(List<LineOfValues> allLines) {
		this.allLines = allLines;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graph = (Graphics2D)g;
		graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		// create x and y axes 
		graph.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		graph.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

		// create hatch marks for y axis. 
		for (int i = 0; i <= Y_HATCH_CNT; i++) {
			int x0 = BORDER_GAP;
			int x1 = 0;
			if(i%2!=0){
				x1 = (int) (x0 + BORDER_GAP*0.75);
			} else {
				x1 = (int) (x0 + BORDER_GAP*0.5);
			}
			int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
			int y1 = y0;
			graph.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i <= X_HATCH_CNT; i++) {
			int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (X_HATCH_CNT) + BORDER_GAP;
			int x1 = x0;
			int y0 = getHeight() - BORDER_GAP;
			int y1 = 0;
			if(i%2==0){
				y1 = (int) (y0 - BORDER_GAP*0.5);
			} else {
				y1 = (int) (y0 - BORDER_GAP*0.75);
			}
			graph.drawLine(x0, y0, x1, y1);
		}
		
		
		// find max value
		double maxValue = 0.0;
		for(LineOfValues singleLine:this.allLines) {
			if(singleLine.isCountForMaxValue()) {
				maxValue = Math.max(maxValue, Collections.max(singleLine.getValues()));
			}
		}
		
		// add values and lines
		
		for(LineOfValues singleLine:this.allLines) {
			List<Point> graphPoints = new ArrayList<Point>();
			for (int i = 0; i < singleLine.size(); i++) {
				double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (singleLine.size() - 1);
				int x1 = (int) (i * xScale + BORDER_GAP);
				double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (maxValue);
				int y1 = (int) ((maxValue - singleLine.getValues().get(i)) * yScale + BORDER_GAP);
				graphPoints.add(new Point(x1, y1));
			}		
			
			graph.setColor(singleLine.getColor());
			graph.setStroke(GRAPH_STROKE);
			for (int i = 0; i < graphPoints.size() - 1; i++) {
				int x1 = graphPoints.get(i).x;
				int y1 = graphPoints.get(i).y;
				int x2 = graphPoints.get(i + 1).x;
				int y2 = graphPoints.get(i + 1).y;
				graph.drawLine(x1, y1, x2, y2);         
			}
			
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return GRAPH_DIMENSION;
	}

	public static void createAndShowGui(List<Double> deltaX, List<Double> aggregateDeltaX, List<Double> correctiveArray, String name) {

		BufferedImage myPicture;
		JLabel picLabel = null;
		try {
			myPicture = ImageIO.read(new File(INFO_PATH));
			picLabel = new JLabel(new ImageIcon(myPicture));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<LineOfValues> lov = new ArrayList<LineOfValues>();
		LineOfValues deltaXLV = new LineOfValues(Color.red, deltaX, false);
		lov.add(deltaXLV);
		LineOfValues aggDeltaXLV = new LineOfValues(Color.blue, aggregateDeltaX, true);
		lov.add(aggDeltaXLV);
		LineOfValues lutXLV = new LineOfValues(Color.green, correctiveArray, true);
		lov.add(lutXLV);
		
		DrawGraphHD graphPanel = new DrawGraphHD(lov);
		
		JPanel main = new JPanel();
		main.setPreferredSize(GRAPH_DIMENSION);
		main.setLayout(null);
		
		main.add(picLabel);
		main.add(graphPanel);
		
		Insets insets = main.getInsets();
		
		Dimension graphSize = graphPanel.getPreferredSize();
		graphPanel.setBounds(0 + insets.left, 0 + insets.top,
				graphSize.width, graphSize.height);
		
		Dimension infoSize = picLabel.getPreferredSize();
		picLabel.setBounds(0 + insets.left, 0 + insets.top,
				infoSize.width, infoSize.height);

		JFrame frame = new JFrame(name);
		frame.getContentPane().add(main);
		frame.pack();
		frame.setLocationByPlatform(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setVisible(true);
	}
}