package com.github.richardflee.astroimagej.visibility_plotter;

import java.time.LocalDateTime;
import java.util.Map;

import org.jfree.data.time.Minute;

import com.github.richardflee.astroimagej.query_objects.AltAzmObject;
import com.github.richardflee.astroimagej.query_objects.BaseFieldObject;
import com.github.richardflee.astroimagej.query_objects.ObservationSite;
import com.github.richardflee.astroimagej.visibility_plotter.CoordsConverter.CoordsEnum;

/**
 * Creates the next altazm object in 60s sequence 
 *
 */
public class AltAzmGenerator {

	private CoordsConverter coords = null;
	private String objectId = null;

	/**
	 * Configures object generator for target object, observer's location and starting night
	 * 
	 * @param fo target object
	 * @param site observation site
	 * @param civilDate starting night
	 */
	public AltAzmGenerator(BaseFieldObject fo, ObservationSite site) {
		this.coords = new CoordsConverter(fo, site);
		this.objectId = fo.getObjectId();
	}

	/**
	 * Returns the next alt-azm object in time sequence
	 * 
	 * @param current time in JFreeChart Minute units (60s)
	 * @return current object encapsulating alt, azm and time data
	 */
	public AltAzmObject getAltAzmObject(Minute current) {
		// convert current time Minute to utc
		LocalDateTime civilDateTime = TimesConverter.convertMinuteToCivilDateTime(current);
		LocalDateTime utcDateTime = this.coords.getTimesConverter().convertCivilDateTimeToUtc(civilDateTime);
		
		// compute object altitude and azimuth coordinates
		Map<CoordsEnum, Double> map = coords.getAltAzm(utcDateTime);
		double altDeg = map.get(CoordsEnum.ALT_DEG);
		double azmDeg = map.get(CoordsEnum.AZM_DEG);
		AltAzmObject altAzmObject = new AltAzmObject(civilDateTime, altDeg, azmDeg);
		AltAzmObject.setName(objectId);
		return altAzmObject;
	}
}
