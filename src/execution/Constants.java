package execution;

public interface Constants {
	
	//// Execution settings
	public static final String JSON_CONFIG_PATH = "config.json";
	public static final String INFO_PATH = "graph-info.png";
	public static final int MAX_RESOLUTION = 1000;
	public static final int INTERNAL_RESOLUTION = 1000;
	
	//// Settings keys
	public static final String INPUT_FILE = "input_file";
	public static final String AGGREGATION_ORDER = "aggregation_order";
	public static final String DEAD_ZONE_PERCENTAGE = "dead_zone_percentage";
	public static final String LUT_GENERATION_METHOD = "lut_generation_method";
	public static final String GAIN_PERCENTAGE = "gain_percentage";
	public static final String GAMMA = "gamma";
	public static final String LINEARIZE_NEAR_ZERO = "linearize_near_zero";
	public static final String FORCE_COLUMN_INDEX = "force_column_index";
	public static final String DELTA_COLUMN_INDEX = "delta_column_index";
	public static final String SKIP_FIRST_ROW = "skip_first_row";
	public static final String ADVANCED_LUT_GENERATION = "advanced lut generation";
	public static final String LINEAR_LUT_GENERATION = "linear lut generation";
	
}
