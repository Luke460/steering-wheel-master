package model;

public class ExecutionConfiguration {
	
	boolean saveLUT; 
	boolean showPreview;
	boolean autoCalcAggregationOder;
	boolean generateLinearLut;
	boolean linearizeNearZero;
	String inputCsvPath;
	int aggregationOrder;
	double deadZoneEnhancement;
	int peakReduction;
	int ffbPowerEnhacement;
	int forceColumnIndex;
	int deltaColumnIndex;

	public ExecutionConfiguration() {
		this.saveLUT = false;
		this.showPreview = false;
		this.autoCalcAggregationOder = false;
		this.generateLinearLut = false;
		this.linearizeNearZero = false;
	};
	
	public ExecutionConfiguration(ExecutionConfiguration conf) {
		this.saveLUT = conf.isSaveLUT();
		this.showPreview = conf.isShowPreview();
		this.autoCalcAggregationOder = conf.isAutoCalcAggregationOder();
		this.generateLinearLut = conf.isGenerateLinearLut();
		this.linearizeNearZero = conf.isLinearizeNearZero();
		this.inputCsvPath = conf.getInputCsvPath();
		this.aggregationOrder = conf.getAggregationOrder();
		this.deadZoneEnhancement = conf.getDeadZoneEnhancement();
		this.peakReduction = conf.getPeakReduction();
		this.ffbPowerEnhacement = conf.getFfbPowerEnhacement();
		this.forceColumnIndex = conf.getForceColumnIndex();
		this.deltaColumnIndex = conf.getDeltaColumnIndex();
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

	public boolean isGenerateLinearLut() {
		return generateLinearLut;
	}

	public void setGenerateLinearLut(boolean generateLinearLut) {
		this.generateLinearLut = generateLinearLut;
	}

	public boolean isLinearizeNearZero() {
		return linearizeNearZero;
	}

	public void setLinearizeNearZero(boolean linearizeNearZero) {
		this.linearizeNearZero = linearizeNearZero;
	}

	public int getPeakReduction() {
		return peakReduction;
	}

	public void setPeakReduction(int peakReduction) {
		this.peakReduction = peakReduction;
	}
	
	public int getFfbPowerEnhacement() {
		return ffbPowerEnhacement;
	}

	public void setFfbPowerEnhacement(int ffbPowerEnhacement) {
		this.ffbPowerEnhacement = ffbPowerEnhacement;
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

}
