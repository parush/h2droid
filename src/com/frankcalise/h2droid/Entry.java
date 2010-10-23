package com.frankcalise.h2droid;

import java.util.Date;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class Entry {
	private Date date;
	private double nonMetricAmount;
	private double metricAmount;
	
	private static final double ouncePerMililiter = 0.0338140227;
	
	public Date getDate() { return date; }
	public double getNonMetricAmount() { return nonMetricAmount; }
	public double getMetricAmount() { return metricAmount; }
	
	/** Constructor for Entry
	 * 
	 * @param _date The date of the entry
	 * @param _amount The amount of water consumed at this date
	 * @param _isNonMetric True/false if the amount was in non-Metric units
	 * 
	 * Depending on _isNonMetric, units will be converted to get 
	 * the other amount for second double field
	 */
	public Entry(Date _date, double _amount, boolean _isNonMetric) {
		date = _date;
		
		if (_isNonMetric) {
			nonMetricAmount = _amount;
			metricAmount = convertToMetric(_amount);
		} else {
			metricAmount = _amount;
			nonMetricAmount = convertToNonMetric(_amount);
		}
	}
	
	/** Constructor for Entry, use now as date
	 * 
	 * @param _amount The amount of water consumed 
	 * @param _isNonMetric
	 */
	public Entry(double _amount, boolean _isNonMetric) {
		this(new Date(), _amount, _isNonMetric);
	}
	
	/** Received amount in non-Metric units, convert to Metric */
	private double convertToMetric(double _amount) {
		return round((_amount / ouncePerMililiter), 2, BigDecimal.ROUND_UP);
	}
	
	/** Received amount in Metric units, convert to non-Metric */
	private double convertToNonMetric(double _amount) {
		return round((_amount * ouncePerMililiter), 2, BigDecimal.ROUND_UP);
	}
	
	/** Entry toString */
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String dateString = sdf.format(date);
		
		return (dateString + "- metric: " + metricAmount + " ml - nonmetric: "
			   + nonMetricAmount + " fl oz"); 
	}
	
	/** Helper function for rounding double values */
	private static double round(double unrounded, int precision, int roundingMode) {
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    
	    return rounded.doubleValue();
	}
}
