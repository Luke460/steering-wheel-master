package execution;

public class ExecutionConfiguration {
	
	boolean saveCSV; 
	boolean saveLUT; 
	boolean showPreview;
	boolean autoCalcAggregationOder;
	boolean deadZoneCorrectionOnly;
	boolean addTimestamp;
	String inputCsvPath;
	int aggregationOrder;
	int deadZoneEnhancement;
	
	public ExecutionConfiguration() {
		this.saveCSV = false;
		this.saveLUT = false;
		this.showPreview = false;
		this.autoCalcAggregationOder = false;
		this.deadZoneCorrectionOnly = false;
		this.addTimestamp = true;
	};

	public boolean isSaveCSV() {
		return saveCSV;
	}

	public void setSaveCSV(boolean saveCSV) {
		this.saveCSV = saveCSV;
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

	public int getDeadZoneEnhancement() {
		return deadZoneEnhancement;
	}

	public void setDeadZoneEnhancement(int deadZoneEnhancement) {
		this.deadZoneEnhancement = deadZoneEnhancement;
	}

	public boolean isDeadZoneCorrectionOnly() {
		return deadZoneCorrectionOnly;
	}

	public void setDeadZoneCorrectionOnly(boolean deadZoneCorrectionOnly) {
		this.deadZoneCorrectionOnly = deadZoneCorrectionOnly;
	}

	public boolean isAddTimestamp() {
		return addTimestamp;
	}

	public void setAddTimestamp(boolean addTimestamp) {
		this.addTimestamp = addTimestamp;
	}

}
