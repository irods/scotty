package org.irods.scotty.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeSeriesMetric {
	
	private List<Metric> metrics;

	public TimeSeriesMetric() {
		this.metrics = new ArrayList<Metric>();
	}
	
	public TimeSeriesMetric(List<Metric> metrics) {
		this.metrics = metrics;
	}
	
	public void append(Metric metric) {
		if (metric != null) {
			this.metrics.add(metric);
		}
	}
	
	public List<Metric> getMetricList() {
		return this.metrics;
	}
	
	public int getSize() {
		return this.metrics.size();
	}
	
	public Date getMetricsStartDate() {
		Date metricsStartDate = null;
		
		if ((metrics != null) && (metrics.size() > 0)) {
			metricsStartDate = metrics.get(0).getTimestampDate();
		}
		return metricsStartDate;
	}
	
	public Date getMetricsEndDate() {
		Date metricsEndDate = null;
		
		if ((metrics != null) && (metrics.size() > 0)) {
			int size = metrics.size();
			metricsEndDate = metrics.get(size-1).getTimestampDate();
		}
		return metricsEndDate;
	}

}
