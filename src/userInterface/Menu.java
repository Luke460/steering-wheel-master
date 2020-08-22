package userInterface;

import javax.swing.*;

import org.json.JSONObject;

import execution.Manager;

import static execution.Constants.JSON_CONFIG_PATH;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Menu extends JPanel{

	private static final long serialVersionUID = 1L;
	private static final String INPUT_FILE = "input_file";
	private static final String AGGREGATION_ORDER = "aggregation_order";
	JButton previewButton;
	JButton generateCsvButton;
	JButton generateLutButton;
	JButton fileBrowserButton;
	JTextField inputFileText;
	JTextField aggregationText;
	JSONObject config;


	public void showMenu(org.json.JSONObject inputConfig){
		this.config = inputConfig;
		// Create frame with title Wheel Check Data Aggregator
		JFrame frame= new JFrame(); 
		frame.setTitle("Wheel Check Data Aggregator");

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
		aggregationText = new JTextField(2);
		aggregationText.setText(config.getInt(AGGREGATION_ORDER) + "");

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
		layoutPanel.add(aggregationText, constr);

		previewButton = new JButton("Preview");
		previewButton.addActionListener(performListener);

		generateCsvButton = new JButton("Generate csv");
		generateCsvButton.addActionListener(performListener);
		
		generateLutButton = new JButton("Generate lut");
		generateLutButton.addActionListener(performListener);


		// Add label and button to panel
		constr.gridx=0; constr.gridy=2;
		constr.gridwidth = 2;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(previewButton, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateCsvButton, constr);
		constr.gridx=2;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateLutButton, constr);

		mainPanel.add(headingPanel);
		mainPanel.add(layoutPanel);

		// Add panel to frame
		frame.add(mainPanel);
		frame.pack();
		frame.setSize(600, 200);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void updateConfig(org.json.JSONObject config) {
		config.put(AGGREGATION_ORDER, Integer.valueOf(aggregationText.getText()));
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
