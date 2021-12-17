package model;

public class ExecutionConfiguration {
	
	boolean saveLUT; 
	boolean showPreview;
	boolean autoCalcAggregationOder;
	String lutGeneration_method;
	boolean linearizeNearZero;
	String inputCsvPath;
	int aggregationOrder;
	double deadZonePercentage;
	int gainPercentage;
	int gamma;
	int forceColumnIndex;
	int deltaColumnIndex;
	boolean skipFirstRow;

	public ExecutionConfiguration() {};
	
	public ExecutionConfiguration(ExecutionConfiguration conf) {
		this.saveLUT = conf.isSaveLUT();
		this.showPreview = conf.isShowPreview();
		this.autoCalcAggregationOder = conf.isAutoCalcAggregationOder();
		this.lutGeneration_method = conf.getLutGeneration_method();
		this.linearizeNearZero = conf.isLinearizeNearZero();
		this.inputCsvPath = conf.getInputCsvPath();
		this.aggregationOrder = conf.getAggregationOrder();
		this.deadZonePercentage = conf.getDeadZonePercentage();
		this.gainPercentage = conf.getGainPercentage();
		this.gamma = conf.getGamma();
		this.forceColumnIndex = conf.getForceColumnIndex();
		this.deltaColumnIndex = conf.getDeltaColumnIndex();
		this.skipFirstRow = conf.isSkipFirstRow();
	}

	public boolean isSaveLUT() {
		return saveLUT;
	}

	public void setSaveLUT(boolean saveLUT) {
		this.saveLUT = saveLUT;
	}

	public boolean isShowPreview() {
		return showPreview;
	}

	public void setShowPreview(boolean showPreview) {
		this.showPreview = showPreview;
	}
	
	public boolean isAutoCalcAggregationOder() {
		return autoCalcAggregationOder;
	}

	public void setAutoCalcAggregationOder(boolean autoAggregationOder) {
		this.autoCalcAggregationOder = autoAggregationOder;
	}

	public String getInputCsvPath() {
		return inputCsvPath;
	}

	public void setInputCsvPath(String inputCsvPath) {
		this.inputCsvPath = inputCsvPath;
	}

	public int getAggregationOrder() {
		return aggregationOrder;
	}

	public void setAggregationOrder(int aggregationValue) {
		this.aggregationOrder = aggregationValue;
	}

	public double getDeadZonePercentage() {
		return deadZonePercentage;
	}

	public void setDeadZonePercentage(double deadZonePercentage) {
		this.deadZonePercentage = deadZonePercentage;
	}

	public String getLutGeneration_method() {
		return lutGeneration_method;
	}

	public void setLutGeneration_method(String lutGenerationMethod) {
		this.lutGeneration_method = lutGenerationMethod;
	}

	public boolean isLinearizeNearZero() {
		return linearizeNearZero;
	}

	public void setLinearizeNearZero(boolean linearizeNearZero) {
		this.linearizeNearZero = linearizeNearZero;
	}

	public int getGainPercentage() {
		return gainPercentage;
	}

	public void setGainPercentage(int gainPercentage) {
		this.gainPercentage = gainPercentage;
	}
	
	public int getGamma() {
		return gamma;
	}

	public void setGamma(int gamma) {
		this.gamma = gamma;
	}

	public int getForceColumnIndex() {
		return forceColumnIndex;
	}

	public void setForceColumnIndex(int forceColumnIndex) {
		this.forceColumnIndex = forceColumnIndex;
	}

	public int getDeltaColumnIndex() {
		return deltaColumnIndex;
	}

	public void setDeltaColumnIndex(int deltaColumnIndex) {
		this.deltaColumnIndex = deltaColumnIndex;
	}

	public boolean isSkipFirstRow() {
		return skipFirstRow;
	}

	public void setSkipFirstRow(boolean skipFirstRow) {
		this.skipFirstRow = skipFirstRow;
	}

}
