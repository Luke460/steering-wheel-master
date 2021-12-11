package userInterface;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import execution.Manager;
import execution.Utility;
import model.ExecutionConfiguration;

import static execution.Constants.*;
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
	private static final Dimension MENU_DIMENSION = new Dimension(700, 506);
	JButton previewButton;
	JButton donateButton;
	JButton generateLutButton;
	JButton fileBrowserButton;
	JButton autoButton;
	JButton inputCsvSettings;
	JComboBox<String> lutGenerationMethod;
	JCheckBox linearizeNearZero;
	JTextField inputFileText;
	JSlider aggregationSlider;
	JSlider peakReductionSlider;
	JSlider ffbPowerEnhancementSlider;
	JSlider deadZoneEnhancementSlider;
	JSONObject config;
	JLabel documentationLink;
	JLabel updatesLink;


	public void showMenu(org.json.JSONObject inputConfig){
		this.config = inputConfig;
		
		JFrame frame= new JFrame(); 
		frame.setTitle("Steering Wheel Master");
		frame.setMinimumSize(MENU_DIMENSION);
		
		Dimension headingSize = new Dimension(100, 24);
		Dimension sideComponentSize = new Dimension(180, 42);
		Dimension textFieldSize = new Dimension(230, 26);
		Dimension sliderSize = new Dimension(244, 44);
		Dimension bigButtonDimension = new Dimension(180, 32);
		Dimension smallButtonDimension = new Dimension(72, 28);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JLabel headingLabel = new JLabel("Settings:");
		headingLabel.setPreferredSize(headingSize);
		headingLabel.setFont(new Font(headingLabel.getFont().getFontName(), 2, 18));

		JPanel layoutPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets = new Insets(8, 8, 8, 8);     
		constr.anchor = GridBagConstraints.WEST;
		
		JLabel inputFileLabel = new JLabel("Input calibration file:");
		inputFileLabel.setPreferredSize(sideComponentSize);
		inputFileText = new JTextField();
		inputFileText.setPreferredSize(textFieldSize);
		inputFileText.setText(config.getString(INPUT_FILE));
	
		final String updatesLabel = "      Check for updates";
		updatesLink = new JLabel(updatesLabel);
		updatesLink.setPreferredSize(sideComponentSize);
		updatesLink.setFont(new Font(updatesLink.getFont().getFontName(), 2, 13));
		updatesLink.setForeground(Color.BLUE);
		updatesLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		final String linkLabel = "    Open documentation";
		documentationLink = new JLabel(linkLabel);
		documentationLink.setPreferredSize(sideComponentSize);
		documentationLink.setFont(new Font(documentationLink.getFont().getFontName(), 2, 13));
		documentationLink.setForeground(Color.BLUE);
		documentationLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// create event listener for the buttons
		PerformListener performListener = new PerformListener();

		fileBrowserButton = new JButton("File browser");

		previewButton = new JButton("Preview");
		previewButton.addActionListener(performListener);

		donateButton = new JButton("Donation page");
		donateButton.addActionListener(performListener);

		generateLutButton = new JButton("Generate lut");
		generateLutButton.addActionListener(performListener);

		// Lut generation section
		JLabel lutGenerationMethodLabel = new JLabel("Lut generation method:");
		lutGenerationMethodLabel.setPreferredSize(sideComponentSize);
		String lutGenerationMethodList[] = {ADVANCED_LUT_GENERATION, LINEAR_LUT_GENERATION}; 
		lutGenerationMethod = new JComboBox<String>(lutGenerationMethodList);
		lutGenerationMethod.setPreferredSize(textFieldSize);
		DefaultListCellRenderer listRenderer;
		listRenderer = new DefaultListCellRenderer();
	    listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
	    lutGenerationMethod.setRenderer(listRenderer);
	    lutGenerationMethod.setBackground(Color.WHITE);
		lutGenerationMethod.setSelectedItem(inputConfig.getString(LUT_GENERATION_METHOD));

		// linearize near zero
		linearizeNearZero = new JCheckBox();
		linearizeNearZero.setPreferredSize(sideComponentSize);
		linearizeNearZero.setText(" Linearize near zero");
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
		aggregationLabel.setPreferredSize(sideComponentSize);
		aggregationSlider = new JSlider(0, 10, config.getInt(AGGREGATION_ORDER));
		aggregationSlider.setPreferredSize(sliderSize);
		aggregationSlider.setMajorTickSpacing(5);
		aggregationSlider.setMinorTickSpacing(1);
		aggregationSlider.setPaintTicks(true);
		aggregationSlider.setPaintLabels(true);   		         
		aggregationSlider.setLabelTable(position1); 

		JLabel peakReductionLabel = new JLabel("FFB peak reduction:");
		peakReductionLabel.setPreferredSize(sideComponentSize);
		peakReductionSlider = new JSlider(0, 10, config.getInt(PEAK_REDUCTION));
		peakReductionSlider.setPreferredSize(sliderSize);
		peakReductionSlider.setMajorTickSpacing(5);
		peakReductionSlider.setMinorTickSpacing(1);
		peakReductionSlider.setPaintTicks(true);
		peakReductionSlider.setPaintLabels(true);
		peakReductionSlider.setPaintTrack(true);
		peakReductionSlider.setLabelTable(position1);
		
		JLabel ffbPowerEnhacementLabel = new JLabel("FFB power enhancement:");
		ffbPowerEnhacementLabel.setPreferredSize(sideComponentSize);
		ffbPowerEnhancementSlider = new JSlider(0, 10, config.getInt(FFB_POWER_ENHANCEMENT));
		ffbPowerEnhancementSlider.setPreferredSize(sliderSize);
		ffbPowerEnhancementSlider.setMajorTickSpacing(5);
		ffbPowerEnhancementSlider.setMinorTickSpacing(1);
		ffbPowerEnhancementSlider.setPaintTicks(true);
		ffbPowerEnhancementSlider.setPaintLabels(true);
		ffbPowerEnhancementSlider.setPaintTrack(true);
		ffbPowerEnhancementSlider.setLabelTable(position1);
		
		JLabel deadZoneEnhancementLabel = new JLabel("Dead zone enhancement:");
		deadZoneEnhancementLabel.setPreferredSize(sideComponentSize);
		deadZoneEnhancementSlider = new JSlider(0, 20, (int)(config.getDouble(DEADZONE_ENHANCEMENT)*2));
		deadZoneEnhancementSlider.setPreferredSize(sliderSize);
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
		ffbPowerEnhancementSlider.setToolTipText(htmlBegin + POWER_ENHANCEMENT_DESCRIPTION + htmlEnd);
		deadZoneEnhancementSlider.setToolTipText(htmlBegin + DZ_ENHANCEMENT_DESCRIPTION + htmlEnd);
		linearizeNearZero.setToolTipText(htmlBegin + LINEARIZE_NEAR_ZERO_DESCRIPTION + htmlEnd);
		lutGenerationMethod.setToolTipText(htmlBegin + LUT_METHOD_DESCRIPTION + htmlEnd);
		donateButton.setToolTipText(htmlBegin + DONATION_DESCRIPTION + htmlEnd);
		inputCsvSettings.setToolTipText(htmlBegin + CSV_SETTINGS_DESCRIPTION + htmlEnd);
		previewButton.setToolTipText(htmlBegin + PREVIEW_DESCRIPTION + htmlEnd);
		generateLutButton.setToolTipText(htmlBegin + GENERATE_LUT_DESCRIPTION + htmlEnd);
		
		// BOTTON SETUP
		fileBrowserButton.setPreferredSize(bigButtonDimension);
		autoButton.setPreferredSize(smallButtonDimension);
		inputCsvSettings.setPreferredSize(bigButtonDimension);
		previewButton.setPreferredSize(bigButtonDimension);
		donateButton.setPreferredSize(bigButtonDimension);
		generateLutButton.setPreferredSize(bigButtonDimension);
		
		// UI SETUP
		constr.anchor = GridBagConstraints.WEST;
		
		// HEADING ROW
		constr.gridy=0;
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(headingLabel, constr);
		constr.anchor = GridBagConstraints.WEST;
		
		// INPUT FILE ROW
		constr.gridy++;
		constr.gridx=0;
		layoutPanel.add(inputFileLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(inputFileText, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(fileBrowserButton, constr);
		fileBrowserButton.addActionListener(performListener);

		// AGGREGATON METHOD ROW
		constr.gridy++;
		constr.gridx=0; 
		layoutPanel.add(lutGenerationMethodLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(lutGenerationMethod, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(autoButton, constr);
		
		
		// AGGREGATON ORDER ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(aggregationLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(aggregationSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(inputCsvSettings, constr);

		// PEAK REDUCTION ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(peakReductionLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(peakReductionSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(linearizeNearZero, constr);
		
		// POWER ENHANCEMENT ROW
		constr.gridy++;
		
		constr.gridx=0; 
		layoutPanel.add(ffbPowerEnhacementLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(ffbPowerEnhancementSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(updatesLink, constr);
		
		// DEADZONE ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(deadZoneEnhancementLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(deadZoneEnhancementSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(documentationLink, constr);

		// LAST ROW
		constr.gridy++;
		constr.gridx=0;
		layoutPanel.add(previewButton, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(donateButton, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(generateLutButton, constr);

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
					Desktop.getDesktop().browse(new URI("https://github.com/Luke460/steering-wheel-master"));  
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
					Desktop.getDesktop().browse(new URI("https://github.com/Luke460/steering-wheel-master/releases"));  
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		lutGenerationMethod.addItemListener(new ItemListener() {

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
		
		ffbPowerEnhancementSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateComponentsStatus();
			}
		});
		
	}
	
	private void updateComponentsStatus() {

		//aggregationSlider
		if(lutGenerationMethod.getSelectedItem().equals(ADVANCED_LUT_GENERATION)) {
			aggregationSlider.setEnabled(true);
		} else {
			aggregationSlider.setEnabled(false);
		}
		
		//linearization
		if(lutGenerationMethod.getSelectedItem().equals(LINEAR_LUT_GENERATION)) {
			linearizeNearZero.setEnabled(false);
		} else {
			linearizeNearZero.setEnabled(true);
		}
		
		//compensation
		if(peakReductionSlider.getValue()==0) {
			ffbPowerEnhancementSlider.setEnabled(true);
		} else {
			ffbPowerEnhancementSlider.setEnabled(false);
		}
		
		if(ffbPowerEnhancementSlider.getValue()==0) {
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
		config.put(LUT_GENERATION_METHOD, lutGenerationMethod.getSelectedItem());
		config.put(PEAK_REDUCTION, peakReductionSlider.getValue());
		config.put(LINEARIZE_NEAR_ZERO, linearizeNearZero.isSelected());
		config.put(FFB_POWER_ENHANCEMENT, ffbPowerEnhancementSlider.getValue());
		
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
				if(lutGenerationMethod.getSelectedItem().equals(ADVANCED_LUT_GENERATION)) {
					exConf.setAutoCalcAggregationOder(true);
					exConf = Manager.execute(exConf);
					aggregationSlider.setValue(exConf.getAggregationOrder());
					deadZoneEnhancementSlider.setValue((int) (exConf.getDeadZoneEnhancement() * 2));
					peakReductionSlider.setValue(exConf.getPeakReduction());
					lutGenerationMethod.setSelectedItem(exConf.getLutGeneration_method());
					linearizeNearZero.setSelected(exConf.isLinearizeNearZero());
					ffbPowerEnhancementSlider.setValue(exConf.getFfbPowerEnhacement());
				} else {
					//exConf.setPeakReduction(0);
					peakReductionSlider.setValue(0);
					//exConf.setFfbPowerEnhancement(0);
					ffbPowerEnhancementSlider.setValue(0);
					//exConf.setDeadZoneEnhancement(10);
					deadZoneEnhancementSlider.setValue(10);
				}
				updateComponentsStatus();
			} else if(src == inputCsvSettings){
				transferJsonConfigIntoExConf(jsonConfig,exConf);
				CsvSettingsMenu csvSettings = new CsvSettingsMenu();
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
			exConf.setLutGeneration_method(configJson.getString(LUT_GENERATION_METHOD));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + LUT_GENERATION_METHOD + "' property in '" + JSON_CONFIG_PATH + "'.");
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
		try {
			exConf.setSkipFirstRow(configJson.getBoolean(SKIP_FIRST_ROW));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + SKIP_FIRST_ROW + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
	}
	
}