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
	JSlider gainSlider;
	JSlider gammaSlider;
	JSlider deadZoneSlider;
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

		Hashtable<Integer, JLabel> labelPosition;

		JLabel aggregationLabel = new JLabel("Aggregation order:");
		aggregationLabel.setPreferredSize(sideComponentSize);
		aggregationSlider = new JSlider(0, 10, config.getInt(AGGREGATION_ORDER));
		aggregationSlider.setPreferredSize(sliderSize);
		aggregationSlider.setMajorTickSpacing(5);
		aggregationSlider.setMinorTickSpacing(1);
		aggregationSlider.setPaintTicks(true);
		aggregationSlider.setPaintLabels(true);
		labelPosition = SliderValueAdapter.getTicksLabel(0, 10, 0, 10, 1);
		aggregationSlider.setLabelTable(labelPosition);


		JLabel gainLabel = new JLabel("FFB gain percentage:");
		gainLabel.setPreferredSize(sideComponentSize);
		gainSlider = new JSlider(0, 50, SliderValueAdapter.getGainSliderValue(config.getDouble(GAIN_PERCENTAGE)));
		gainSlider.setPreferredSize(sliderSize);
		gainSlider.setMajorTickSpacing(10);
		gainSlider.setMinorTickSpacing(5);
		gainSlider.setPaintTicks(true);
		gainSlider.setPaintLabels(true);
		gainSlider.setPaintTrack(true);
		labelPosition = SliderValueAdapter.getTicksLabel(50, 100, 0, 50, 10);
		gainSlider.setLabelTable(labelPosition);

		JLabel ffbGammaLabel = new JLabel("FFB Gamma:");
		ffbGammaLabel.setPreferredSize(sideComponentSize);
		gammaSlider = new JSlider(0, 16, SliderValueAdapter.getGammaSliderValue(config.getDouble(GAMMA)));
		gammaSlider.setPreferredSize(sliderSize);
		gammaSlider.setMajorTickSpacing(4);
		gammaSlider.setMinorTickSpacing(1);
		gammaSlider.setPaintTicks(true);
		gammaSlider.setPaintLabels(true);
		gammaSlider.setPaintTrack(true);
		labelPosition = SliderValueAdapter.getTicksLabel(-1, 1, 1, 16, 4);
		gammaSlider.setLabelTable(labelPosition);
		
		JLabel deadZoneLabel = new JLabel("FFB Dead zone:");
		deadZoneLabel.setPreferredSize(sideComponentSize);
		deadZoneSlider = new JSlider(0, 80, SliderValueAdapter.getDeadZoneSliderValue(config.getDouble(DEAD_ZONE_PERCENTAGE)));
		deadZoneSlider.setPreferredSize(sliderSize);
		deadZoneSlider.setMajorTickSpacing(4);
		deadZoneSlider.setMinorTickSpacing(2);
		deadZoneSlider.setPaintTicks(true);
		deadZoneSlider.setPaintLabels(true);
		deadZoneSlider.setPaintTrack(true);
		labelPosition = SliderValueAdapter.getTicksLabel(0, 20, 0, 80, 8);
		deadZoneSlider.setLabelTable(labelPosition);
		
		autoButton = new JButton("Auto");
		autoButton.addActionListener(performListener);
		
		inputCsvSettings = new JButton("CSV settings");
		inputCsvSettings.addActionListener(performListener);
		
		// TOOLTIPS SETUP
		String htmlBegin = "<html><p width=\""+END_LINE_LIMIT+"\">";
		String htmlEnd = "</p></html>";
		
		fileBrowserButton.setToolTipText(htmlBegin + FILE_BROWSER_DESCRIPTION + htmlEnd);
		aggregationSlider.setToolTipText(htmlBegin + AGGREGATION_ORDER_DESCRIPTION + htmlEnd);
		autoButton.setToolTipText(htmlBegin + AUTO_DESCRIPTION + htmlEnd);
		gainSlider.setToolTipText(htmlBegin + PEAK_REDUCTION_DESCRIPTION + htmlEnd);
		gammaSlider.setToolTipText(htmlBegin + POWER_ENHANCEMENT_DESCRIPTION + htmlEnd);
		deadZoneSlider.setToolTipText(htmlBegin + DZ_ENHANCEMENT_DESCRIPTION + htmlEnd);
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
		layoutPanel.add(gainLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(gainSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(documentationLink, constr);
		
		// POWER ENHANCEMENT ROW
		constr.gridy++;
		
		constr.gridx=0; 
		layoutPanel.add(ffbGammaLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(gammaSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(updatesLink, constr);
		
		// DEADZONE ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(deadZoneLabel, constr);
		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(deadZoneSlider, constr);
		constr.anchor = GridBagConstraints.WEST;
		constr.gridx=2;
		layoutPanel.add(linearizeNearZero, constr);

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
		
		deadZoneSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateComponentsStatus();
			}
		});
		
		gainSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateComponentsStatus();
			}
		});
		
		gammaSlider.addChangeListener(new ChangeListener() {
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

	}

	public org.json.JSONObject updateConfig() {
		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);
		config.put(AGGREGATION_ORDER, aggregationSlider.getValue());
		config.put(INPUT_FILE, inputFileText.getText());
		config.put(DEAD_ZONE_PERCENTAGE, SliderValueAdapter.getDeadZoneActualValue(deadZoneSlider.getValue()));
		config.put(LUT_GENERATION_METHOD, lutGenerationMethod.getSelectedItem());
		config.put(GAIN_PERCENTAGE, SliderValueAdapter.getGainActualValue(gainSlider.getValue()));
		config.put(LINEARIZE_NEAR_ZERO, linearizeNearZero.isSelected());
		config.put(GAMMA, SliderValueAdapter.getGammaActualValue(gammaSlider.getValue()));
		
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
					deadZoneSlider.setValue(SliderValueAdapter.getDeadZoneSliderValue(0));
					gainSlider.setValue(SliderValueAdapter.getGainSliderValue(100));
					gammaSlider.setValue(SliderValueAdapter.getGammaSliderValue(0));
					linearizeNearZero.setSelected(false);
				} else { //LINEAR
					exConf.setAutoCalcAggregationOder(true);
					exConf = Manager.execute(exConf);
					deadZoneSlider.setValue(SliderValueAdapter.getDeadZoneSliderValue(exConf.getDeadZonePercentage()));
					gainSlider.setValue(SliderValueAdapter.getGainSliderValue(100));
					gammaSlider.setValue(SliderValueAdapter.getGammaSliderValue(0));
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
			exConf.setDeadZonePercentage(configJson.getDouble(DEAD_ZONE_PERCENTAGE));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + DEAD_ZONE_PERCENTAGE + "' property in '" + JSON_CONFIG_PATH + "'.");
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
			exConf.setGainPercentage(configJson.getInt(GAIN_PERCENTAGE));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + GAIN_PERCENTAGE + "' property in '" + JSON_CONFIG_PATH + "'.");
		}
		try {
			exConf.setGamma(configJson.getInt(GAMMA));
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: unable to read '" + GAMMA + "' property in '" + JSON_CONFIG_PATH + "'.");
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