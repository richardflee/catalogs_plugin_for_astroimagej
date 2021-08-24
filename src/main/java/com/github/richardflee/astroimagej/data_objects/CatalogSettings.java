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
	//private double upperLabelValue;
	//private double lowerLabelValue;
	
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
		//upperLabelValue = 0.0;
		//lowerLabelValue = 0.0;
	}
	
	/**
	 * Updates catalogui info label settings with current record numbers and reference mag limits
	 * <p>Displays #N/A if user sets mag limit value < 0.01 => that limit is disabled</p> 
	 * 
	 * @param nTotalRecords number or reference records returned in on-line query (excludes target)
	 * @param nFilteredRecords number of records after nobs & mag filters applied (excludes target)
	 */
//	public void updateLabelValues(int nTotalRecords, int nFilteredRecords) {
//		// update settings record numbers, clip negative values
//		nTotalRecords = (nTotalRecords >=0) ? nTotalRecords : 0;
//		nFilteredRecords = (nFilteredRecords >= 0) ? nFilteredRecords: 0;
//		this.setTotalLabelValue(nTotalRecords);
//		this.setFilteredLabelValue(nFilteredRecords);
//	}

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

	public String getTotalLabelValue() {
		return Integer.toString(totalLabelValue);
	}

	public void setTotalLabelValue(int nTotalRecords) {		
		this.totalLabelValue =  (nTotalRecords >=0) ? nTotalRecords : 0;
	}

	public String getFilteredLabelValue() {
		return Integer.toString(filteredLabelValue);
	}

	public void setFilteredLabelValue(int nFilteredRecords) {
		this.filteredLabelValue = (nFilteredRecords >= 0) ? nFilteredRecords: 0;
	}
	
	// Upper mag limit label: sum target + upperlimit if limit > 0.01, N/A otherwise
	/**
	 * 
	 */
	
	/**
	 * Upper limit magnitude band; upper limit < 0.01 disables this limit
	 * <p>Note: upper limit value is positive</p>
	 * 
	 * @return limit > 0.01 => sum of target and upper limit values, N/A otherwise
	 */
	public String getUpperLabelValue() {
		double limit = this.upperLimitSpinnerValue;
		double targetMag = this.targetMagSpinnerValue;
		String labelStr = (Math.abs(limit) < 0.01) ? "N/A" : String.format("%.1f", limit + targetMag);
		return labelStr;
	}

	/**
	 * Lower limit magnitude band; lower limit < 0.01 disables this limit
	 * <p>Note: lower limit value is negative</p>
	 * 
	 * @return |limit| > 0.01 => sum of target and lower limit values, N/A otherwise
	 */
	public String getLowerLabelValue() {
		double limit = this.lowerLimitSpinnerValue;
		double targetMag = this.targetMagSpinnerValue;
		String labelStr = (Math.abs(limit) < 0.01) ? "N/A" : String.format("%.1f", limit + targetMag);
		return labelStr;
	}

	
	@Override
	public String toString() {
		return "CatalogSettings [upperLimitSpinnerValue=" + upperLimitSpinnerValue + ", targetMagSpinnerValue="
				+ targetMagSpinnerValue + ", lowerLimitSpinnerValue=" + lowerLimitSpinnerValue
				+ ", isMagLimitsCheckBoxValue=" + isMagLimitsCheckBoxValue + ", distanceRadioButtonValue="
				+ distanceRadioButtonValue + ", deltaMagRadioButtonValue=" + deltaMagRadioButtonValue
				+ ", nObsSpinnerValue=" + nObsSpinnerValue + ", totalLabelValue=" + totalLabelValue
				+ ", filteredLabelValue=" + filteredLabelValue + "]";
	}
	
	
	public static void main(String[] args) {
		
		double targetMag = 10.0;
		CatalogSettings settings = new CatalogSettings(targetMag);
		
		settings.setUpperLimitSpinnerValue(1.2);
		settings.setLowerLimitSpinnerValue(0.00);
		
		System.out.println(String.format("Target value %.3f", targetMag));
		System.out.println(String.format("Upper limit spinner = 1.2, upper label: %s", settings.getUpperLabelValue()));
		System.out.println(String.format("Lower limit spinner = 0.00, lower label: %s", settings.getLowerLabelValue()));
		
		
	}

}
