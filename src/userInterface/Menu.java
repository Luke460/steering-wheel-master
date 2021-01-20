package userInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import execution.Manager;
import execution.Utility;
import model.ExecutionConfiguration;

import static execution.Constants.JSON_CONFIG_PATH;
import static userInterface.TooltipsText.*;

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
	private static final String FFB_POWER_ENHANCEMENT = "ffb_power_enhancement";
	private static final String LINEARIZE_NEAR_ZERO = "linearize_near_zero";
	private static final String FORCE_COLUMN_INDEX = "force_column_index";
	private static final String DELTA_COLUMN_INDEX = "delta_column_index";
	private static final Dimension MENU_DIMENSION = new Dimension(648, 444);
	JButton previewButton;
	JButton donateButton;
	JButton generateLutButton;
	JButton fileBrowserButton;
	JButton autoButton;
	JButton inputCsvSettings;
	JCheckBox generateLinearLut;
	JCheckBox linearizeNearZero;
	JTextField inputFileText;
	JSlider aggregationSlider;
	JSlider peakReductionSlider;
	JSlider ffbPowerEnhacementSlider;
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
		JLabel inputFileLabel = new JLabel("Input calibration file:");
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

		donateButton = new JButton("Donation page");
		donateButton.addActionListener(performListener);

		generateLutButton = new JButton("Generate lut");
		generateLutButton.addActionListener(performListener);

		generateLinearLut = new JCheckBox();
		generateLinearLut.setText("Generate linear lut");
		generateLinearLut.setSelected(inputConfig.getBoolean(GENERATE_LINEAR_LUT));

		linearizeNearZero = new JCheckBox();
		linearizeNearZero.setText("Linearize near zero");
		linearizeNearZero.setSelected(inputConfig.getBoolean(LINEARIZE_NEAR_ZERO));

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
		
		JLabel ffbPowerEnhacementLabel = new JLabel("FFB power enhancement:");
		ffbPowerEnhacementSlider = new JSlider(0, 10, config.getInt(FFB_POWER_ENHANCEMENT));
		ffbPowerEnhacementSlider.setPreferredSize(new Dimension(244, 44));
		ffbPowerEnhacementSlider.setMajorTickSpacing(5);
		ffbPowerEnhacementSlider.setMinorTickSpacing(1);
		ffbPowerEnhacementSlider.setPaintTicks(true);
		ffbPowerEnhacementSlider.setPaintLabels(true);
		ffbPowerEnhacementSlider.setPaintTrack(true);
		ffbPowerEnhacementSlider.setLabelTable(position1);
		
		JLabel deadZoneEnhancementLabel = new JLabel("Dead zone enhancement:");
		deadZoneEnhancementSlider = new JSlider(0, 20, (int)(config.getDouble(DEADZONE_ENHANCEMENT)*2));
		deadZoneEnhancementSlider.setPreferredSize(new Dimension(244, 44));
		deadZoneEnhancementSlider.setMajorTickSpacing(2);
		deadZoneEnhancementSlider.setMinorTickSpacing(1);
		deadZoneEnhancementSlider.setPaintTicks(true);
		deadZoneEnhancementSlider.setPaintLabels(true);
		deadZoneEnhancementSlider.setPaintTrack(true);
		deadZoneEnhancementSlider.setLabelTable(positionHighPrecision); 
		
		autoButton = new JButton("Auto");
		autoButton.addActionListener(performListener);
		
		inputCsvSettings = new JButton("CSV settings");
		inputCsvSettings.addActionListener(performListener);
		
		// TOOLTIPS SETUP
		String htmlBegin = "<html><p width=\"360\">";
		String htmlEnd = "</p></html>";
		
		fileBrowserButton.setToolTipText(htmlBegin + FILE_BROWSER_DESCRIPTION + htmlEnd);
		aggregationSlider.setToolTipText(htmlBegin + AGGREGATION_ORDER_DESCRIPTION + htmlEnd);
		autoButton.setToolTipText(htmlBegin + AUTO_DESCRIPTION + htmlEnd);
		peakReductionSlider.setToolTipText(htmlBegin + PEAK_REDUCTION_DESCRIPTION + htmlEnd);
		ffbPowerEnhacementSlider.setToolTipText(htmlBegin + POWER_ENHANCEMENT_DESCRIPTION + htmlEnd);
		deadZoneEnhancementSlider.setToolTipText(htmlBegin + DZ_ENHANCEMENT_DESCRIPTION + htmlEnd);
		linearizeNearZero.setToolTipText(htmlBegin + LINEARIZE_NEAR_ZERO_DESCRIPTION + htmlEnd);
		generateLinearLut.setToolTipText(htmlBegin + GENERATE_LINEAR_LUT_DESCRIPTION + htmlEnd);
		donateButton.setToolTipText(htmlBegin + DONATION_DESCRIPTION + htmlEnd);
		inputCsvSettings.setToolTipText(htmlBegin + CSV_SETTINGS_DESCRIPTION + htmlEnd);
		previewButton.setToolTipText(htmlBegin + PREVIEW_DESCRIPTION + htmlEnd);
		generateLutButton.setToolTipText(htmlBegin + GENERATE_LUT_DESCRIPTION + htmlEnd);
		
		// BOTTON SIZE
		Dimension buttonDimension = new Dimension(160, 30);
		fileBrowserButton.setPreferredSize(buttonDimension);
		autoButton.setPreferredSize(buttonDimension);
		inputCsvSettings.setPreferredSize(buttonDimension);
		previewButton.setPreferredSize(buttonDimension);
		donateButton.setPreferredSize(buttonDimension);
		generateLutButton.setPreferredSize(buttonDimension);
		
		// UI SETUP

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

		constr.gridx=2;
		layoutPanel.add(autoButton, constr);

		// THIRD ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(peakReductionLabel, constr);
		constr.gridx=1;
		layoutPanel.add(peakReductionSlider, constr);
		
		constr.gridx=2;
		layoutPanel.add(inputCsvSettings, constr);
		
		//FOURTH ROW
		constr.gridy++;
		
		constr.gridx=0; 
		layoutPanel.add(ffbPowerEnhacementLabel, constr);
		constr.gridx=1;
		layoutPanel.add(ffbPowerEnhacementSlider, constr);
		
		//FIFTH ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(deadZoneEnhancementLabel, constr);
		constr.gridx=1;
		layoutPanel.add(deadZoneEnhancementSlider, constr);

		constr.gridx=2;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(updatesLink, constr);
		constr.anchor = GridBagConstraints.WEST;

		// SIXTH ROW
		constr.gridy++;

		constr.gridx=0;
		layoutPanel.add(linearizeNearZero, constr);

		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(generateLinearLut, constr);

		constr.gridx=2;
		layoutPanel.add(documentationLink, constr);
		constr.anchor = GridBagConstraints.WEST;

		//SEVENTH ROW
		constr.gridx=0; constr.gridy++;
		constr.gridwidth = 3;
		constr.anchor = GridBagConstraints.WEST;
		layoutPanel.add(previewButton, constr);
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(donateButton, constr);
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
		
		donateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {            
					Desktop.getDesktop().browse(new URI("https://www.paypal.com/donate?hosted_button_id=WVSY5VX8TA4ZE"));  
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
		
		linearizeNearZero.addItemListener(new ItemListener() {

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
		
		ffbPowerEnhacementSlider.addChangeListener(new ChangeListener() {
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
		
		//linearization
		if(generateLinearLut.isSelected()) {
			linearizeNearZero.setEnabled(false);
		} else {
			linearizeNearZero.setEnabled(true);
		}
		
		//compensation
		if(peakReductionSlider.getValue()==0) {
			ffbPowerEnhacementSlider.setEnabled(true);
		} else {
			ffbPowerEnhacementSlider.setEnabled(false);
		}
		
		if(ffbPowerEnhacementSlider.getValue()==0) {
			peakReductionSlider.setEnabled(true);
		} else {
			peakReductionSlider.setEnabled(false);
		}
	}

	public org.json.JSONObject updateConfig() {
		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);
		config.put(AGGREGATION_ORDER, aggregationSlider.getValue());
		config.put(INPUT_FILE, inputFileText.getText());
		config.put(DEADZONE_ENHANCEMENT, deadZoneEnhancementSlider.getValue()/2.0);
		config.put(GENERATE_LINEAR_LUT, generateLinearLut.isSelected());
		config.put(PEAK_REDUCTION, peakReductionSlider.getValue());
		config.put(LINEARIZE_NEAR_ZERO, linearizeNearZero.isSelected());
		config.put(FFB_POWER_ENHANCEMENT, ffbPowerEnhacementSlider.getValue());
		try {
			Files.write(Paths.get(JSON_CONFIG_PATH), config.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	class PerformListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			org.json.JSONObject jsonConfig = updateConfig();
			ExecutionConfiguration exConf = new ExecutionConfiguration();
			transferJsonConfigIntoExConf(jsonConfig, exConf);
			Object src = e.getSource();
			if (src == previewButton){
				exConf.setShowPreview(true);
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
				linearizeNearZero.setSelected(exConf.isLinearizeNearZero());
				ffbPowerEnhacementSlider.setValue(exConf.getFfbPowerEnhacement());
				updateComponentsStatus();
			} else if(src == inputCsvSettings){
				transferJsonConfigIntoExConf(jsonConfig,exConf);
				CsvSettings csvSettings = new CsvSettings();
				csvSettings.showCSVoption(exConf);
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

	public void transferJsonConfigIntoExConf(JSONObject configJson, ExecutionConfiguration exConf) {

		try {
			exConf.setInputCsvPath(configJson.getString(INPUT_FILE));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + INPUT_FILE + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setAggregationOrder(configJson.getInt(AGGREGATION_ORDER));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + AGGREGATION_ORDER + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setDeadZoneEnhancement(configJson.getDouble(DEADZONE_ENHANCEMENT));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + DEADZONE_ENHANCEMENT + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setGenerateLinearLut(configJson.getBoolean(GENERATE_LINEAR_LUT));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + GENERATE_LINEAR_LUT + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setLinearizeNearZero(configJson.getBoolean(LINEARIZE_NEAR_ZERO));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + LINEARIZE_NEAR_ZERO + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setPeakReduction(configJson.getInt(PEAK_REDUCTION));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + PEAK_REDUCTION + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setFfbPowerEnhacement(configJson.getInt(FFB_POWER_ENHANCEMENT));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + FFB_POWER_ENHANCEMENT + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setForceColumnIndex(configJson.getInt(FORCE_COLUMN_INDEX));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + FORCE_COLUMN_INDEX + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setDeltaColumnIndex(configJson.getInt(DELTA_COLUMN_INDEX));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + DELTA_COLUMN_INDEX + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
	}
	
}