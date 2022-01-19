package execution;

public interface Constants {
	
	//// Execution settings
	String JSON_CONFIG_PATH = "config.json";
	String INFO_PATH = "graph-info.png";
	int MAX_RESOLUTION = 1000;
	int INTERNAL_RESOLUTION = 5000;
	
	//// Settings keys
	String INPUT_FILE = "input_file";
	String AGGREGATION_ORDER = "aggregation_order";
	String DEADZONE_ENHANCEMENT = "deadzone_enhancement";
	String LUT_GENERATION_METHOD = "lut_geeration_method";
	String PEAK_REDUCTION = "peak_reduction";
	String FFB_POWER_ENHANCEMENT = "ffb_power_enhancement";
	String LINEARIZE_NEAR_ZERO = "linearize_near_zero";
	String FORCE_COLUMN_INDEX = "force_column_index";
	String DELTA_COLUMN_INDEX = "delta_column_index";
	String SKIP_FIRST_ROW = "skip_first_row";
	String ADVANCED_LUT_GENERATION = "advanced lut generation";
	String LINEAR_LUT_GENERATION = "linear lut generation";
	
}
