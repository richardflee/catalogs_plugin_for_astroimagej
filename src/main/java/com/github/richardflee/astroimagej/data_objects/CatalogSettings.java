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

	public CatalogSettings() {
		setDefaultSettings(null);
	}
	
	public void setDefaultSettings(Double targetMag) {
		setDefaultSettings();
		if (targetMag != null) {
			targetMagSpinnerValue = targetMag;
		}
		
	}

	private void setDefaultSettings() {
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
