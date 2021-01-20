package userInterface;

public interface TooltipsText {
	
	//// Execution settings
	
	public static final String FILE_BROWSER_DESCRIPTION = "Choose your input calibration file. Refer to the documentation for more details.";
	public static final String AGGREGATION_ORDER_DESCRIPTION = "Lower values of aggregation order makes your force feedback correction more precise, while higher values makes your force feedback smoother.";
	public static final String AUTO_DESCRIPTION = "Basic settings for your wheel. Refer to the documentation page to find the best settings for your steering wheel.";
	public static final String PEAK_REDUCTION_DESCRIPTION = "Increase if you have clipping issues or you want to reduce the overall noise of your steering wheel.";
	public static final String POWER_ENHANCEMENT_DESCRIPTION = "Increase to boost your wheel FFB strength. This option increases the low and medium values of FFB in a progressive manner.";
	public static final String DZ_ENHANCEMENT_DESCRIPTION = "Increase if you have vibrations in the central area of the steering wheel, decrease if you still have a FFB deadzone with the generated lut.";
	public static final String LINEARIZE_NEAR_ZERO_DESCRIPTION = "Change the aggregation strategy for low values of the force feedback. It can help remove vibrations in the center of some steering wheels. Activating this option requires you to also increase the deadzone enhancement value."; 
	public static final String GENERATE_LINEAR_LUT_DESCRIPTION = "The linear lut generation uses the csv file only to correct the dead zone in order to not to alter the standard behavior of your steering wheel.";
	public static final String DONATION_DESCRIPTION = "Donations are not required, but always accepted with pleasure. Thanks for your support!";
	public static final String CSV_SETTINGS_DESCRIPTION = "Configure how to read the input csv file.";
	public static final String PREVIEW_DESCRIPTION = "Displays force feedback and applied correction values ​​on a graph.";
	public static final String GENERATE_LUT_DESCRIPTION = "Generate a lut file with the current settings.";
}
