package userInterface;

import javax.swing.*;

import org.json.JSONObject;

import execution.Manager;

import static execution.Constants.JSON_CONFIG_PATH;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class Menu extends JPanel{

	private static final long serialVersionUID = 1L;
	private static final String INPUT_FILE = "input_file";
	private static final String AGGREGATION_ORDER = "aggregation_order";
	private static final Dimension MENU_DIMENSION = new Dimension(600, 240);
	JButton previewButton;
	JButton generateCsvButton;
	JButton generateLutButton;
	JButton fileBrowserButton;
	JTextField inputFileText;
	JSlider aggregationSlider;
	JSONObject config;


	public void showMenu(org.json.JSONObject inputConfig){
		this.config = inputConfig;
		// Create frame with title Wheel Check Data Aggregator
		JFrame frame= new JFrame(); 
		frame.setTitle("Wheel Check Data Aggregator");
		frame.setMinimumSize(MENU_DIMENSION);

		// Panel to define the layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel headingPanel = new JPanel();
		JLabel headingLabel = new JLabel("Settings:");
		headingLabel.setFont(new Font(headingLabel.getFont().getFontName(), 2, 16));
		headingPanel.add(headingLabel);

		// Panel to define the layout
		JPanel layoutPanel = new JPanel(new GridBagLayout());
		// Constraints for the layout
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets = new Insets(8, 8, 8, 8);     
		constr.anchor = GridBagConstraints.WEST;


		// Declare the required Labels
		JLabel inputFileLabel = new JLabel("Input file:");
		JLabel aggregationLabel = new JLabel("Aggregation order:");

		// Declare Text fields
		inputFileText = new JTextField(20);
		inputFileText.setText(config.getString(INPUT_FILE));
		
		aggregationSlider = new JSlider(0, 10, config.getInt(AGGREGATION_ORDER));
		aggregationSlider.setPreferredSize(new Dimension(244, 48));
		aggregationSlider.setMajorTickSpacing(5);
		aggregationSlider.setMinorTickSpacing(1);
		aggregationSlider.setPaintTicks(true);
		
		// Set the labels to be painted on the slider
		aggregationSlider.setPaintLabels(true);   	
		         
		// Add positions label in the slider
		Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
		for(int i = 0; i<=10; i++) {
			position.put(i, new JLabel(i+""));
		}
		
		// Set the label to be drawn
		aggregationSlider.setLabelTable(position); 

		// create event listener for the buttons
		PerformListener performListener = new PerformListener();

		// Declare File Manager Button
		fileBrowserButton = new JButton("File browser");

		constr.gridx=0;
		constr.gridy=0;
		layoutPanel.add(inputFileLabel, constr);

		constr.gridx=1;
		layoutPanel.add(inputFileText, constr);

		constr.gridx=2;
		layoutPanel.add(fileBrowserButton, constr);
		fileBrowserButton.addActionListener(performListener);

		constr.gridx=0; constr.gridy=1;
		layoutPanel.add(aggregationLabel, constr);
		constr.gridx=1;
		layoutPanel.add(aggregationSlider, constr);

		previewButton = new JButton("Preview");
		previewButton.addActionListener(performListener);

		generateCsvButton = new JButton("Generate csv");
		generateCsvButton.addActionListener(performListener);
		
		generateLutButton = new JButton("Generate lut");
		generateLutButton.addActionListener(performListener);

		Dimension buttonDimension = new Dimension(160, 30);
		fileBrowserButton.setPreferredSize(buttonDimension);
		previewButton.setPreferredSize(buttonDimension);
		generateCsvButton.setPreferredSize(buttonDimension);
		generateLutButton.setPreferredSize(buttonDimension);
		

		// Add label and button to panel
		constr.gridx=0; constr.gridy=2;
		constr.gridwidth = 3;
		constr.anchor = GridBagConstraints.WEST;
		layoutPanel.add(previewButton, constr);
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateCsvButton, constr);
		constr.anchor = GridBagConstraints.EAST;
		layoutPanel.add(generateLutButton, constr);

		mainPanel.add(headingPanel);
		mainPanel.add(layoutPanel);

		// Add panel to frame
		frame.add(mainPanel);
		frame.pack();
		frame.setSize(MENU_DIMENSION);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void updateConfig(org.json.JSONObject config) {
		config.put(AGGREGATION_ORDER, aggregationSlider.getValue());
		config.put(INPUT_FILE, inputFileText.getText());
		try {
			Files.write(Paths.get(JSON_CONFIG_PATH), config.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PerformListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateConfig(config);
			Object src = e.getSource();
			if (src == previewButton){
				Manager.execute(config, false, false);
			} else if(src == generateCsvButton){
				Manager.execute(config, true, false);
			}	else if(src == generateLutButton){
				Manager.execute(config, false, true);
			} else if(src == fileBrowserButton){
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new CsvFileFilter());
				int n = fileChooser.showOpenDialog(Menu.this);
				if (n == JFileChooser.APPROVE_OPTION) {
					File f = fileChooser.getSelectedFile();	         
					inputFileText.setText(f.getPath());
				}
			}
		}
	}
}
