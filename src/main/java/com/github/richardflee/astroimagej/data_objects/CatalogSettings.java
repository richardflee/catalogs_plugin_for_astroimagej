package com.github.richardflee.astroimagej.data_objects;

/**
 * This class encapsulates catalog ui control data:
 * <p>target mag values</p>
 * <p>sort option</p>
 * <p>number of observations</p>
 * <p>table record numbers</p>
 */
public class CatalogSettings {
	// mag limits
	private double upperLimitSpinnerValue = 0.0;
	private double targetMagSpinnerValue = 12.0;
	private double lowerLimitSpinnerValue = 0.0;
	private boolean isMagLimitsCheckBoxValue = true;

	// sort option
	private boolean distanceRadioButtonValue = true;
	private boolean deltaMagRadioButtonValue = false;

	// number observations / APASS
	private int nObsSpinnerValue = 1;
	
	// record totals
	private int totalLabelValue = 0;
	private int filteredLabelValue = 0;
	
	public CatalogSettings() {
		
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

	@Override
	public String toString() {
		return "CatalogSetting [upperLimitSpinnerValue=" + upperLimitSpinnerValue + ", targetMagSpinnerValue="
				+ targetMagSpinnerValue + ", lowerLimitSpinnerValue=" + lowerLimitSpinnerValue
				+ ", isMagLimitsCheckBoxValue=" + isMagLimitsCheckBoxValue + ", distanceRadioButtonValue="
				+ distanceRadioButtonValue + ", deltaMagRadioButtonValue=" + deltaMagRadioButtonValue
				+ ", nObsSpinnerValue=" + nObsSpinnerValue + ", totalLabelValue=" + totalLabelValue
				+ ", filteredLabelValue=" + filteredLabelValue + "]";
	}

}
