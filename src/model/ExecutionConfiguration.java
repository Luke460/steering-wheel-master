package model;

public class ExecutionConfiguration {
	
	boolean saveLUT; 
	boolean showPreview;
	boolean autoCalcAggregationOder;
	String lutGeneration_method;
	boolean linearizeNearZero;
	String inputCsvPath;
	int aggregationOrder;
	double deadZoneEnhancement;
	int gainReduction;
	int ffbPowerEnhancement;
	int forceColumnIndex;
	int deltaColumnIndex;
	boolean skipFirstRow;

	public ExecutionConfiguration() {}
	
	public ExecutionConfiguration(ExecutionConfiguration conf) {
		this.saveLUT = conf.isSaveLUT();
		this.showPreview = conf.isShowPreview();
		this.autoCalcAggregationOder = conf.isAutoCalcAggregationOder();
		this.lutGeneration_method = conf.getLutGeneration_method();
		this.linearizeNearZero = conf.isLinearizeNearZero();
		this.inputCsvPath = conf.getInputCsvPath();
		this.aggregationOrder = conf.getAggregationOrder();
		this.deadZoneEnhancement = conf.getDeadZoneEnhancement();
		this.gainReduction = conf.getGainReduction();
		this.ffbPowerEnhancement = conf.getFfbPowerEnhancement();
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

	public double getDeadZoneEnhancement() {
		return deadZoneEnhancement;
	}

	public void setDeadZoneEnhancement(double deadZoneEnhancement) {
		this.deadZoneEnhancement = deadZoneEnhancement;
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

	public int getGainReduction() {
		return gainReduction;
	}

	public void setGainReduction(int gainReduction) {
		this.gainReduction = gainReduction;
	}
	
	public int getFfbPowerEnhancement() {
		return ffbPowerEnhancement;
	}

	public void setFfbPowerEnhancement(int ffbPowerEnhancement) {
		this.ffbPowerEnhancement = ffbPowerEnhancement;
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
