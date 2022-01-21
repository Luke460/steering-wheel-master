package userInterface;

public interface TooltipsText {
	
	//// Execution settings
	
	String FILE_BROWSER_DESCRIPTION = "Choose your input calibration file. Refer to the documentation for more details.";
	String AGGREGATION_ORDER_DESCRIPTION = "Lower values of aggregation order makes your force feedback correction more precise, while higher values makes your force feedback smoother.";
	String AUTO_DESCRIPTION = "Basic settings for your wheel. Refer to the documentation page to find the best settings for your steering wheel.";
	String GAIN_REDUCTION_DESCRIPTION = "Increase to reduce the maximum amount of force sent to your steering wheel in a progressive manner.";
	String POWER_ENHANCEMENT_DESCRIPTION = "Increase to boost your wheel FFB strength. This option increases the low and medium values of FFB in a progressive manner by introducing soft clipping.";
	String DZ_ENHANCEMENT_DESCRIPTION = "Increase if you have vibrations in the central area of the steering wheel, decrease if you still have a FFB dead zone with the generated lut.";
	String LINEARIZE_NEAR_ZERO_DESCRIPTION = "Experimental feature: improve fidelity of low ffb values. Enabling this option may require you to slightly increase the dead zone enhancement value.";
	String LUT_METHOD_DESCRIPTION = "Advanced lut generation uses all the data provided by the csv file to attempt to correct the steering wheel reactions, while linear lut generation uses the csv file only to correct the dead zone.";
	String DONATION_DESCRIPTION = "Donations are not required, but always accepted with pleasure. Thanks for your support!";
	String CSV_SETTINGS_DESCRIPTION = "Configure how to read the input force feedback calibration file.";
	String PREVIEW_DESCRIPTION = "Displays force feedback and applied correction values on a chart.";
	String GENERATE_LUT_DESCRIPTION = "Generate a lut file with the current settings.";
}
