package userInterface;

import javax.swing.*;

import execution.Utility;
import model.ExecutionConfiguration;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static execution.Constants.JSON_CONFIG_PATH;

public class CsvSettingsMenu extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private static final Dimension MENU_DIMENSION = new Dimension(332, 200);
	private static final String FORCE_COLUMN_INDEX = "force_column_index";
	private static final String DELTA_COLUMN_INDEX = "delta_column_index";
	
	Menu menu;
	JButton confirmButton;
	JButton cancelButton;
	JTextField forceColumnIndexField;
	JTextField deltaColumnIndexField;
	ExecutionConfiguration inputCSVconfig;
	ExecutionConfiguration oldCSVconfig;


	public void showCSVoption(ExecutionConfiguration inputConfig){
		this.inputCSVconfig = inputConfig;
		this.oldCSVconfig = new ExecutionConfiguration(inputConfig);
		// Create frame
		JFrame frame= new JFrame(); 
		frame.setTitle("Input CSV settings");
		frame.setMinimumSize(MENU_DIMENSION);

		// Panel to define the layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel headingPanel = new JPanel();
		JLabel headingLabel = new JLabel("CSV configuration:");
		headingPanel.add(headingLabel);

		// Panel to define the layout
		JPanel layoutPanel = new JPanel(new GridBagLayout());
		// Constraints for the layout
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets = new Insets(8, 8, 8, 8);     
		constr.anchor = GridBagConstraints.WEST;

		// Declare Text fields
		JLabel forceColumnIndexLabel = new JLabel("Force column index:");
		forceColumnIndexField = new JTextField();
		forceColumnIndexField.setPreferredSize(new Dimension(42, 22));
		forceColumnIndexField.setText(""+inputCSVconfig.getForceColumnIndex());
		
		// Declare Text fields
		JLabel deltaColumnIndexLabel = new JLabel("Delta column index:");
		deltaColumnIndexField = new JTextField();
		deltaColumnIndexField.setPreferredSize(new Dimension(42, 22));
		deltaColumnIndexField.setText(""+inputCSVconfig.getDeltaColumnIndex());

		// create event listener for the buttons
		PerformListener performListener = new PerformListener();

		Dimension buttonDimension = new Dimension(120, 24);
		
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(performListener);
		confirmButton.setPreferredSize(buttonDimension);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(performListener);
		cancelButton.setPreferredSize(buttonDimension);

		// UI SETUP

		// FIRST FOW
		constr.gridx=0;
		constr.gridy=0;
		layoutPanel.add(forceColumnIndexLabel, constr);

		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(forceColumnIndexField, constr);
		constr.anchor = GridBagConstraints.WEST;

		// SECOND ROW
		constr.gridy++;

		constr.gridx=0;
		layoutPanel.add(deltaColumnIndexLabel, constr);

		constr.gridx=1;
		constr.anchor = GridBagConstraints.CENTER;
		layoutPanel.add(deltaColumnIndexField, constr);
		constr.anchor = GridBagConstraints.WEST;

		// THIRD ROW
		constr.gridy++;

		constr.gridx=0; 
		layoutPanel.add(cancelButton, constr);
		constr.gridx=1;
		layoutPanel.add(confirmButton, constr);
		
		// Add main blocks

		mainPanel.add(headingPanel);
		mainPanel.add(layoutPanel);

		// Add panel to frame
		frame.add(mainPanel);
		frame.pack();
		frame.setSize(MENU_DIMENSION);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
	}

	public boolean updateConfig() {
		try {
			int forceColumnIndex = Integer.parseInt(forceColumnIndexField.getText());
			if(forceColumnIndex<1) throw new NumberFormatException();
			this.inputCSVconfig.setForceColumnIndex(forceColumnIndex);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid index value for force column!");
			return false;
		}
		try {
			int deltaColumnIndex = Integer.parseInt(deltaColumnIndexField.getText());
			if(deltaColumnIndex<1) throw new NumberFormatException();
			this.inputCSVconfig.setDeltaColumnIndex(deltaColumnIndex);
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid index value for delta column!");
			return false;
		}
		return true;
	}
	
	public static void updateJsonConf(ExecutionConfiguration inputCSVconfig) {
		org.json.JSONObject config = Utility.readConfiguration(JSON_CONFIG_PATH);
		config.put(FORCE_COLUMN_INDEX, inputCSVconfig.getForceColumnIndex());
		config.put(DELTA_COLUMN_INDEX, inputCSVconfig.getDeltaColumnIndex());
		try {
			Files.write(Paths.get(JSON_CONFIG_PATH), config.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class PerformListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == confirmButton){
				if(updateConfig()) {
					updateJsonConf(inputCSVconfig);
					JComponent comp = (JComponent) e.getSource();
					Window win = SwingUtilities.getWindowAncestor(comp);
					win.dispose();
				}
			} else if(src == cancelButton){
				JComponent comp = (JComponent) e.getSource();
				Window win = SwingUtilities.getWindowAncestor(comp);
				win.dispose();
			}
		}
	}
}