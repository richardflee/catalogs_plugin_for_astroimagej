package com.github.richardflee.astroimagej.query_objects;

/**
 * This class encapsulates catalog ui sort and filter controls data
 */
public class CatalogSettings {
	// target mag value
	private double targetMagSpinnerValue;

	// mag limits
	private double upperLimitSpinnerValue;
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

	// flag enable / disable catalog ui buttons
	private boolean enableButtons;

	// Default, parameter and copy constructors
	public CatalogSettings() {
		resetDefaultSettings(null);
	}
	
	public CatalogSettings(double targetMag) {
		resetDefaultSettings(targetMag);
	}

	/**
	 * Copy constructor
	 * 
	 * @param settings source object to copy
	 */
	public CatalogSettings(CatalogSettings settings) {
		// target mag value
		this.targetMagSpinnerValue = settings.getTargetMagSpinnerValue();

		// mag limits
		this.upperLimitSpinnerValue = settings.getUpperLimitSpinnerValue();
		this.lowerLimitSpinnerValue = settings.getLowerLimitSpinnerValue();
		this.isMagLimitsCheckBoxValue = settings.isMagLimitsCheckBoxValue();

		// sort option
		this.distanceRadioButtonValue = settings.isDistanceRadioButtonValue();
		this.deltaMagRadioButtonValue = settings.isDeltaMagRadioButtonValue();

		// number observations / APASS
		this.nObsSpinnerValue = settings.getnObsSpinnerValue();

		// record totals
		this.totalLabelValue = Integer.valueOf(settings.getTotalLabelValue());
		this.filteredLabelValue = Integer.valueOf(settings.getFilteredLabelValue());

		// no table data
		this.enableButtons = false;
	}

	private void resetDefaultSettings(Double targetMag) {
		resetDefaultSettings();

		// update targetmagspinner setting if targetMag not null
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
		// upperLabelValue = 0.0;
		// lowerLabelValue = 0.0;
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

	public String getTotalLabelValue() {
		return Integer.toString(totalLabelValue);
	}

	public void setTotalLabelValue(int nTotalRecords) {
		this.totalLabelValue = (nTotalRecords >= 0) ? nTotalRecords : 0;
	}

	public String getFilteredLabelValue() {
		return Integer.toString(filteredLabelValue);
	}

	public void setFilteredLabelValue(int nFilteredRecords) {
		this.filteredLabelValue = (nFilteredRecords >= 0) ? nFilteredRecords : 0;
	}


	public boolean isEnableButtons() {
		return enableButtons;
	}

	public void setEnableButtons(boolean enableButtons) {
		this.enableButtons = enableButtons;
	}

	/**
	 * Upper limit magnitude band; upper limit < 0.01 disables this limit <p> Note:
	 * upper limit value is positive </p>
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
	 * Lower limit magnitude band; lower limit < 0.01 disables this limit <p> Note:
	 * lower limit value is negative </p>
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
		return "CatalogSettings [targetMagSpinnerValue=" + targetMagSpinnerValue + ", upperLimitSpinnerValue="
				+ upperLimitSpinnerValue + ", lowerLimitSpinnerValue=" + lowerLimitSpinnerValue
				+ ", isMagLimitsCheckBoxValue=" + isMagLimitsCheckBoxValue + ", distanceRadioButtonValue="
				+ distanceRadioButtonValue + ", deltaMagRadioButtonValue=" + deltaMagRadioButtonValue
				+ ", nObsSpinnerValue=" + nObsSpinnerValue + ", totalLabelValue=" + totalLabelValue
				+ ", filteredLabelValue=" + filteredLabelValue + ", enableButtons=" + enableButtons + "]";
	}

	public static void main(String[] args) {

		double targetMag = 12.3;
		CatalogSettings set0 = new CatalogSettings(targetMag);

		set0.setUpperLimitSpinnerValue(1.2);
		System.out.println(String.format("Target value %.3f", targetMag));

		System.out.println("Spinner value:");
		set0.setLowerLimitSpinnerValue(0.00);
		System.out.println(String.format("Upper limit spinner = 1.2, upper label: %s", set0.getUpperLabelValue()));
		System.out.println(String.format("Lower limit spinner = 0.00, lower label: %s", set0.getLowerLabelValue()));

		System.out.println("\nCopy constructor:");
		CatalogSettings set1 = new CatalogSettings(set0);
		set1.setUpperLimitSpinnerValue(0.00);
		set1.setLowerLimitSpinnerValue(-1.3);
		System.out.println(String.format("Upper limit spinner = 0.0, upper label: %s", set1.getUpperLabelValue()));
		System.out.println(String.format("Lower limit spinner = -1.3, lower label: %s", set1.getLowerLabelValue()));
		System.out.println(String.format("Confirm equal target values = 12.3 %.1f, %b", set1.getTargetMagSpinnerValue(),
				set0.getTargetMagSpinnerValue() == set1.getTargetMagSpinnerValue()));

	}

}
