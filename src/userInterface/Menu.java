package userInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import execution.Manager;
import model.ExecutionConfiguration;

import static execution.Constants.JSON_CONFIG_PATH;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class Menu extends JPanel{

	private static final long serialVersionUID = 1L;
	private static final String INPUT_FILE = "input_file";
	private static final String AGGREGATION_ORDER = "aggregation_order";
	private static final String DEADZONE_ENHANCEMENT = "deadzone_enhancement";
	private static final String GENERATE_LINEAR_LUT = "generate_linear_lut";
	private static final String PEAK_REDUCTION = "peak_reduction";
	private static final String EXPERIMENTAL_AGGREGATION = "experimental_aggregation";
	private static final Dimension MENU_DIMENSION = new Dimension(648, 404);
	JButton previewButton;
	JButton generateCsvButton;
	JButton generateLutButton;
	JButton fileBrowserButton;
	JButton autoButton;
	JCheckBox generateLinearLut;
	JCheckBox experimentalAggregation;
	JTextField inputFileText;
	JSlider aggregationSlider;
	JSlider peakReductionSlider;
	JSlider deadZoneEnhancementSlider;
	JSONObject config;
	JLabel documentationLink;
	JLabel updatesLink;


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

		// Declare Text fields
		JLabel inputFileLabel = new JLabel("Input file:");
		inputFileText = new JTextField();
		inputFileText.setPreferredSize(new Dimension(236, 22));
		inputFileText.setText(config.getString(INPUT_FILE));

		// Link label	
		final String linkLabel = "Open documentation";
		documentationLink = new JLabel(linkLabel);
		documentationLink.setFont(new Font(headingLabel.getFont().getFontName(), 2, 13));
		documentationLink.setForeground(Color.BLUE);
		documentationLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Updates label	
		final String updatesLabel = "Check for updates";
		updatesLink = new JLabel(updatesLabel);
		updatesLink.setFont(new Font(headingLabel.getFont().getFontName(), 2, 13));
		updatesLink.setForeground(Color.BLUE);
		updatesLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// create event listener for the buttons
		PerformListener performListener = new PerformListener();

		fileBrowserButton = new JButton("File browser");

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

		generateLinearLut = new JCheckBox();
		generateLinearLut.setText("Generate linear lut");
		generateLinearLut.setSelected(inputConfig.getBoolean(GENERATE_LINEAR_LUT));

		experimentalAggregation = new JCheckBox();
		experimentalAggregation.setText("Linearize near zero");
		experimentalAggregation.setSelected(inputConfig.getBoolean(EXPERIMENTAL_AGGREGATION));

		// Add positions label in the slider
		Hashtable<Integer, JLabel> position1 = new Hashtable<Integer, JLabel>();
		for(int i = 0; i<=10; i++) {
			position1.put(i, new JLabel(i+""));
		}
		
		Hashtable<Integer, JLabel> positionHighPrecision = new Hashtable<Integer, JLabel>();
		for(int i = 0; i<=20; i+=2) {
			positionHighPrecision.put(i, new JLabel(i/2+""));
		}		

		JLabel aggregationLabel = new JLabel("Aggregation order:");
		aggregationSlider = new JSlider(0, 10, config.getInt(AGGREGATION_ORDER));
		aggregationSlider.setPreferredSize(new Dimension(244, 44));
		aggregationSlider.setMajorTickSpacing(5);
		aggregationSlider.setMinorTickSpacing(1);
		aggregationSlider.setPaintTicks(true);
		aggregationSlider.setPaintLabels(true);   		         
		aggregationSlider.setLabelTable(position1); 

		JLabel peakReductionLabel = new JLabel("FFB peak reduction:");
		peakReductionSlider = new JSlider(0, 10, config.getInt(PEAK_REDUCTION));
		peakReductionSlider.setPreferredSize(new Dimension(244, 44));
		peakReductionSlider.setMajorTickSpacing(5);
		peakReductionSlider.setMinorTickSpacing(1);
		peakReductionSlider.setPaintTicks(true);
		peakReductionSlider.setPaintLabels(true);
		peakReductionSlider.setPaintTrack(true);
		peakReductionSlider.setLabelTable(position1); 
		
		JLabel deadZoneEnhancementLabel = new JLabel("Dead zone enhancement:");
		deadZoneEnhancementSlider = new JSlider(0, 20, (int)(config.getDouble(DEADZONE_ENHANCEMENT)*2));
		deadZoneEnhancementSlider.setPreferredSize(new Dimension(244, 44));
		deadZoneEnhancementSlider.setMajorTickSpacing(2);
		deadZoneEnhancementSlider.setMinorTickSpacing(1);
		deadZoneEnhancementSlider.setPaintTicks(true);
		deadZoneEnhancementSlider.setPaintLabels(true);
		deadZoneEnhancementSlider.setPaintTrack(true);
		deadZoneEnhancementSlider.setLabelTable(positionHighPrecision); 

		// FIRST FOW
		constr.gridx=0;
		constr.gridy=0;
		layoutPanel.add(inputFileLabel, constr);

		constr.gridx=1;
		layoutPanel.add(inputFileText, constr);

		constr.gridx=2;
		layoutPanel.add(fileBrowserButton, constr);
		fileBrowserButton.addActionListener(performListener);

		// SECOND ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(aggregationLabel, constr);
		constr.gridx=1;
		layoutPanel.add(aggregationSlider, constr);

		autoButton = new JButton("Auto");
		autoButton.addActionListener(performListener);

		constr.gridx=2;
		layoutPanel.add(autoButton, constr);

		// THIRD ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(peakReductionLabel, constr);
		constr.gridx=1;
		layoutPanel.add(peakReductionSlider, constr);
		
		// FOURTH ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(deadZoneEnhancementLabel, constr);
		constr.gridx=1;
		layoutPanel.add(deadZoneEnhancementSlider, constr);

		constr.gridx=2;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(updatesLink, constr);
		constr.anchor = GridBagConstraints.WEST;

		// FIFTH ROW
		constr.gridy++;

		constr.gridx=0;
		layoutPanel.add(experimentalAggregation, constr);

		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateLinearLut, constr);

		constr.gridx=2;
		layoutPanel.add(documentationLink, constr);
		constr.anchor = GridBagConstraints.WEST;

		//SIXTH ROW
		constr.gridx=0; constr.gridy++;
		constr.gridwidth = 3;
		constr.anchor = GridBagConstraints.WEST;
		layoutPanel.add(previewButton, constr);
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateCsvButton, constr);
		constr.anchor = GridBagConstraints.EAST;
		layoutPanel.add(generateLutButton, constr);

		mainPanel.add(headingPanel);
		mainPanel.add(layoutPanel);
		
		addListeners();
		updateComponentsStatus();

		// Add panel to frame
		frame.add(mainPanel);
		frame.pack();
		frame.setSize(MENU_DIMENSION);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	private void addListeners() {
			
		documentationLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {            
					Desktop.getDesktop().browse(new URI("https://github.com/Luke460/wheel-check-data-aggregator"));  
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
			
		updatesLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {            
					Desktop.getDesktop().browse(new URI("https://github.com/Luke460/wheel-check-data-aggregator/releases"));  
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		generateLinearLut.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateComponentsStatus();
			}
		});
		
		experimentalAggregation.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateComponentsStatus();
			}
		});
		
		deadZoneEnhancementSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateComponentsStatus();
			}
		});
		
		peakReductionSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateComponentsStatus();
			}
		});
		
	}
	
	private void updateComponentsStatus() {

		//aggregationSlider
		if(!generateLinearLut.isSelected()) {
			aggregationSlider.setEnabled(true);
		} else {
			aggregationSlider.setEnabled(false);
		}
		
		//generateCsvButton
		if(!generateLinearLut.isSelected()&&deadZoneEnhancementSlider.getValue()==0&&peakReductionSlider.getValue()==0) {
			generateCsvButton.setEnabled(true);
		} else {
			generateCsvButton.setEnabled(false);
		}
		
		//linearization
		if(generateLinearLut.isSelected()) {
			experimentalAggregation.setEnabled(false);
		} else {
			experimentalAggregation.setEnabled(true);
		}
		
	}

	public void updateConfig(org.json.JSONObject config) {
		config.put(AGGREGATION_ORDER, aggregationSlider.getValue());
		config.put(INPUT_FILE, inputFileText.getText());
		config.put(DEADZONE_ENHANCEMENT, deadZoneEnhancementSlider.getValue()/2.0);
		config.put(GENERATE_LINEAR_LUT, generateLinearLut.isSelected());
		config.put(PEAK_REDUCTION, peakReductionSlider.getValue());
		config.put(EXPERIMENTAL_AGGREGATION, experimentalAggregation.isSelected());
		try {
			Files.write(Paths.get(JSON_CONFIG_PATH), config.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PerformListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateConfig(config);
			ExecutionConfiguration exConf = new ExecutionConfiguration();
			transferJsonConfigIntoExConf(exConf);
			Object src = e.getSource();
			if (src == previewButton){
				exConf.setShowPreview(true);
				Manager.execute(exConf);
			} else if(src == generateCsvButton){
				exConf.setShowPreview(true);
				exConf.setSaveCSV(true);
				Manager.execute(exConf);
			} else if(src == generateLutButton){
				exConf.setShowPreview(true);
				exConf.setSaveLUT(true);
				Manager.execute(exConf);
			} else if(src == autoButton) {
				exConf.setAutoCalcAggregationOder(true);
				exConf = Manager.execute(exConf);
				aggregationSlider.setValue(exConf.getAggregationOrder());
				deadZoneEnhancementSlider.setValue((int)(exConf.getDeadZoneEnhancement()*2));
				peakReductionSlider.setValue(exConf.getPeakReduction());
				generateLinearLut.setSelected(exConf.isGenerateLinearLut());
				experimentalAggregation.setSelected(false);
				updateComponentsStatus();
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

		private void transferJsonConfigIntoExConf(ExecutionConfiguration exConf) {

			try {
				exConf.setInputCsvPath(config.getString(INPUT_FILE));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + INPUT_FILE + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
			try {
				exConf.setAggregationOrder(config.getInt(AGGREGATION_ORDER));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + AGGREGATION_ORDER + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
			try {
				exConf.setDeadZoneEnhancement(config.getDouble(DEADZONE_ENHANCEMENT));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + DEADZONE_ENHANCEMENT + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
			try {
				exConf.setGenerateLinearLut(config.getBoolean(GENERATE_LINEAR_LUT));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + GENERATE_LINEAR_LUT + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
			try {
				exConf.setExperimentalAggregation(config.getBoolean(EXPERIMENTAL_AGGREGATION));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + EXPERIMENTAL_AGGREGATION + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
			try {
				exConf.setPeakReduction(config.getInt(PEAK_REDUCTION));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read '" + PEAK_REDUCTION + "' property in '" + JSON_CONFIG_PATH + "'.");
			}
		}
	}

}