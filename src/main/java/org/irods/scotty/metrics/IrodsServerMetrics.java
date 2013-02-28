package org.irods.scotty.metrics;

import java.util.Date;
import java.util.List;

// TODO: probably should just make IrodsServerMetric extend TimeSeriesMetric
public class IrodsServerMetrics {
	
	private TimeSeriesMetric serverMetrics;
	
	public IrodsServerMetrics(TimeSeriesMetric metrics) {
		this.serverMetrics = metrics;
	}
	
	// return the iRODS server up time as a percentage in the time span given
	public double getServerUpTimePercentage(Date startTime, Date endTime) {
		double rate = -1.0;
		int countTotal = 0;
		int countUpTimes = 0;
		if (this.serverMetrics != null) { 
			List<Metric> metrics = this.serverMetrics.getMetricList();
			for (Metric metric: metrics) {
				if ((metric.getTimestampDate().equals(startTime) || metric.getTimestampDate().after(startTime)) &&
				    (metric.getTimestampDate().equals(endTime) || metric.getTimestampDate().before(endTime))) {
					countTotal++;
					countUpTimes+=((Boolean)metric.getFirstMetricValueKeyValue()) ? 1 : 0;
				}
			}
			if (countTotal > 0) {
				rate = (countUpTimes * 100) / countTotal;
			}
		}
		
		return rate;
	}
	
	// return the iRODS server up time as a percentage for the time span of the
	// entire this.serverMetrics
	public double getServerUpTimePercentage() {
		double rate = -1.0;
		if (this.serverMetrics != null) { 
			// first collect start and end dates from this set of metrics
			Date metricsStartDate = this.serverMetrics.getMetricsStartDate();
			Date metricsEndDate = this.serverMetrics.getMetricsEndDate();
			rate = getServerUpTimePercentage(metricsStartDate, metricsEndDate);	
		}
		
		return rate;
	}
	
	public int getServerUpDaysSince(Date startTime) {
		int numberOfDays = -1;
		
		return numberOfDays;
	}
	
	public int getServerUpDays() {
		int numberOfDays = -1;
		
		return numberOfDays;
	}

}
