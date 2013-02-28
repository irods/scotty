package org.irods.scotty.metrics;

import java.util.Set;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Metric {
	
	private String metricName;
	private Double timestamp;
	private JSONObject metricValues;
	private JSONObject metadataValues;
	private String displayName;
	private String displayDescription;
	
	public Metric() {		
	}
	
	public Metric(String metricName) {		
		this.metricName = metricName;
	}
	
	public Metric(String metricName, Double timestamp, JSONObject metricValues) {
		this.metricName = metricName;
		this.timestamp = timestamp;
		this.metricValues = metricValues;
	}
	
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public Double getTimestamp() {
		return timestamp;
	}
	public Date getTimestampDate() {
		Date ts = new Date(this.timestamp.longValue()*1000);
		return ts;
	}
	public void setTimestamp(Double timestamp) {
		this.timestamp = timestamp;
	}
	public JSONObject getMetricValues() {
		return metricValues;
	}
	public void setMetricValues(JSONObject metricValues) {
		this.metricValues = metricValues;
	}
	
	public int getMetricValuesSize() {
		return metricValues.size();
	}
	
	public void setMetadataValues(JSONObject metadataValues) {
		this.metadataValues = metadataValues;
	}
	
	public int getMetadataValuesSize() {
		return metadataValues.size();
	}
	
	public String getFirstMetricValueKeyName() {
		String firstKey = null;
		
		Set keySet = metricValues.keySet();
		firstKey = keySet.iterator().next().toString();

		return firstKey;
	}
	
	public Object getFirstMetricValueKeyValue() {
		Object value = null;
		
		value = metricValues.values().iterator().next();
		
		return value;
	}
	
	public String getDisplayName() {
		String displayName = null;
		
		if (this.displayName == null) {	
			if (this.metadataValues != null) {
				displayName = (String)metadataValues.get("DisplayName");
			}
		}
		
		this.displayName = displayName;
		return this.displayName;
	}
	
	public String getDisplayDescription() {
		String displayDescription = null;
		
		if (this.displayDescription == null) {	
			if (this.metadataValues != null) {
				displayDescription = (String)metadataValues.get("Description");
			}
		}
		
		this.displayDescription = displayDescription;
		return this.displayDescription;
	}
	
}
