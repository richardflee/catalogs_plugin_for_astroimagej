package com.github.richardflee.astroimagej.data_objects;

/**
 * This class encapsulates catalog ui control data:
 * <p>
 * target mag values
 * </p>
 * <p>
 * sort option
 * </p>
 * <p>
 * number of observations
 * </p>
 * <p>
 * table record numbers
 * </p>
 */
public class CatalogSettings {
	// mag limits
	private double upperLimitSpinnerValue;
	private double targetMagSpinnerValue;
	private double lowerLimitSpinnerValue;
	private boolean isMagLimitsCheckBoxValue;

	// sort option
	private boolean distanceRadioButtonValue;
	private boolean deltaMagRadioButtonValue;

	// number observations / APASS
	private int nObsSpinnerValue;

	// record totals
	private int totalLabelValue;
	private int filteredLabelValue;
	
	// upper & lower mag limits
	private String upperLabelValue;
	private String lowerLabelValue;
	
	public CatalogSettings() {
		resetDefaultSettings(null);
	}

	public CatalogSettings(Double targetMag) {
		resetDefaultSettings(targetMag);
	}
	
	public void resetDefaultSettings(Double targetMag) {
		resetDefaultSettings();
		if (targetMag != null) {
			targetMagSpinnerValue = targetMag;
		}
		
	}

	private void resetDefaultSettings() {
		upperLimitSpinnerValue = 0.0;
		targetMagSpinnerValue = 12.0;
		lowerLimitSpinnerValue = 0.0;
		isMagLimitsCheckBoxValue = true;

		// sort option
		distanceRadioButtonValue = true;
		deltaMagRadioButtonValue = false;

		// number observations / APASS
		nObsSpinnerValue = 1;

		// record totals
		totalLabelValue = 0;
		filteredLabelValue = 0;
		
		// upper & lower mag limits
		upperLabelValue ="N/A";
		lowerLabelValue = "N/A";
	}
	
	/**
	 * Updates catalogui info label settings with current record numbers and reference mag limits
	 * <p>Displays #N/A if user sets mag limit value < 0.01 => that limit is disabled</p> 
	 * 
	 * @param nTotalRecords number or reference records returned in on-line query (excludes target)
	 * @param nFilteredRecords number of records after nobs & mag filters applied (excludes target)
	 */
	public void updateLabelValues(int nTotalRecords, int nFilteredRecords) {
		// update settings record numbers, clip negative values
		nTotalRecords = (nTotalRecords >=0) ? nTotalRecords : 0;
		nFilteredRecords = (nFilteredRecords >= 1) ? nFilteredRecords : 0;
		this.setTotalLabelValue(nTotalRecords);
		this.setFilteredLabelValue(nFilteredRecords);
		
		// upper & lower mag limit labels, N/A => disable
		double targetMag = this.getTargetMagSpinnerValue();
		double limitVal = this.getUpperLimitSpinnerValue();
		String limitStr = (Math.abs(limitVal) < 0.01) ? "N/A" : String.format("%.1f", limitVal + targetMag);
		this.setUpperLabelValue(limitStr);

		limitVal = this.getLowerLimitSpinnerValue();
		limitStr = (Math.abs(limitVal) < 0.01) ? "N/A" : String.format("%.1f", limitVal + targetMag);
		this.setLowerLabelValue(limitStr);
	}


	// auto getter - setters
	public double getUpperLimitSpinnerValue() {
		return upperLimitSpinnerValue;
	}

	public void setUpperLimitSpinnerValue(double upperLimitSpinnerValue) {
		this.upperLimitSpinnerValue = upperLimitSpinnerValue;
	}

	public double getTargetMagSpinnerValue() {
		return targetMagSpinnerValue;
	}

	public void setTargetMagSpinnerValue(double targetMagSpinnerValue) {
		this.targetMagSpinnerValue = targetMagSpinnerValue;
	}

	public double getLowerLimitSpinnerValue() {
		return lowerLimitSpinnerValue;
	}

	public void setLowerLimitSpinnerValue(double lowerLimitSpinnerValue) {
		this.lowerLimitSpinnerValue = lowerLimitSpinnerValue;
	}

	public boolean isMagLimitsCheckBoxValue() {
		return isMagLimitsCheckBoxValue;
	}

	public void setMagLimitsCheckBoxValue(boolean isMagLimitsCheckBoxValue) {
		this.isMagLimitsCheckBoxValue = isMagLimitsCheckBoxValue;
	}

	public boolean isDistanceRadioButtonValue() {
		return distanceRadioButtonValue;
	}

	public void setDistanceRadioButtonValue(boolean distanceRadioButtonValue) {
		this.distanceRadioButtonValue = distanceRadioButtonValue;
	}

	public boolean isDeltaMagRadioButtonValue() {
		return deltaMagRadioButtonValue;
	}

	public void setDeltaMagRadioButtonValue(boolean deltaMagRadioButtonValue) {
		this.deltaMagRadioButtonValue = deltaMagRadioButtonValue;
	}

	public int getnObsSpinnerValue() {
		return nObsSpinnerValue;
	}

	public void setnObsSpinnerValue(int nObsSpinnerValue) {
		this.nObsSpinnerValue = nObsSpinnerValue;
	}

	public int getTotalLabelValue() {
		return totalLabelValue;
	}

	public void setTotalLabelValue(int totalLabelValue) {
		this.totalLabelValue = totalLabelValue;
	}

	public int getFilteredLabelValue() {
		return filteredLabelValue;
	}

	public void setFilteredLabelValue(int filteredLabelValue) {
		this.filteredLabelValue = filteredLabelValue;
	}
	
	public String getUpperLabelValue() {
		return upperLabelValue;
	}

	public void setUpperLabelValue(String upperLabelValue) {
		this.upperLabelValue = upperLabelValue;
	}

	public String getLowerLabelValue() {
		return lowerLabelValue;
	}

	public void setLowerLabelValue(String lowerLabelValue) {
		this.lowerLabelValue = lowerLabelValue;
	}

	
	@Override
	public String toString() {
		return "CatalogSettings [upperLimitSpinnerValue=" + upperLimitSpinnerValue + ", targetMagSpinnerValue="
				+ targetMagSpinnerValue + ", lowerLimitSpinnerValue=" + lowerLimitSpinnerValue
				+ ", isMagLimitsCheckBoxValue=" + isMagLimitsCheckBoxValue + ", distanceRadioButtonValue="
				+ distanceRadioButtonValue + ", deltaMagRadioButtonValue=" + deltaMagRadioButtonValue
				+ ", nObsSpinnerValue=" + nObsSpinnerValue + ", totalLabelValue=" + totalLabelValue
				+ ", filteredLabelValue=" + filteredLabelValue + ", upperLabelValue=" + upperLabelValue
				+ ", lowerLabelValue=" + lowerLabelValue + "]";
	}

}
