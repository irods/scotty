package org.irods.scotty.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFileReader;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GuinanMetrics {
	
	private IRODSAccount irodsAccount;
	private IRODSFileSystem irodsFileSystem;

	public GuinanMetrics(IRODSAccount irodsAccount, IRODSFileSystem irodsFileSystem) {
		this.irodsAccount = irodsAccount;
		this.irodsFileSystem = irodsFileSystem;
	}
	
	public List<TimeSeriesMetric> retrieveAllMetrics() {
		List<TimeSeriesMetric> metrics = new ArrayList<TimeSeriesMetric>();
		
		List<String> metricNames = getAllMetricNames();
		
		for (String metricName: metricNames) {
			TimeSeriesMetric metric = getNamedMetric(metricName);
			metrics.add(metric);
		}
		return metrics;
	}
	
	public List<String> getAllMetricNames() {
		List<String> names = new ArrayList<String>();
		List<CollectionAndDataObjectListingEntry> metricsList = null;
		
		StringBuilder metricsDir = new StringBuilder();
		metricsDir.append("/");
		metricsDir.append(irodsAccount.getZone()); // This may not always be the right zone???
		metricsDir.append("/guinan");
		
		try {
			metricsList = irodsFileSystem.getIRODSAccessObjectFactory().
					getCollectionAndDataObjectListAndSearchAO(irodsAccount).
					listDataObjectsUnderPath(metricsDir.toString(), 0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (CollectionAndDataObjectListingEntry entry: metricsList) {
			String fileName  = entry.getPathOrName();
			int idx = fileName.indexOf('.');
			String name = fileName.substring(0, idx);
			names.add(name);
		}
		
		return names;
	}
	
	public TimeSeriesMetric getNamedMetric(String metricName) {
		
		StringBuilder metricFileName = new StringBuilder();
		metricFileName.append("/");
		metricFileName.append(irodsAccount.getZone()); // This may not always be the right zone???
		metricFileName.append("/guinan/");
		metricFileName.append(metricName);
		metricFileName.append(".json");

		IRODSFileReader fr = null;
    	try {
    		fr = irodsFileSystem.getIRODSFileFactory(irodsAccount).instanceIRODSFileReader(metricFileName.toString());
			
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
    	
    	JSONParser parser = new JSONParser();
    	TimeSeriesMetric timeSeriesMetric = new TimeSeriesMetric();
    	try {
			Object obj = parser.parse(fr);
			JSONObject jsonObject = (JSONObject) obj;
			 
			JSONArray ts = (JSONArray) jsonObject.get(metricName);
			Iterator<JSONObject> iterator = ts.iterator();
			while (iterator.hasNext()) {
				Metric metric = new Metric(metricName);
				JSONObject metricObject = (JSONObject)iterator.next();
				metric.setTimestamp((Double) metricObject.get("timestamp"));
				metric.setMetricValues((JSONObject)((JSONObject)metricObject.get("metric")));
				JSONObject metadata = (JSONObject)metricObject.get("metadata");
				if (metadata != null) {
					metric.setMetadataValues(metadata);
				}
				timeSeriesMetric.append(metric);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeSeriesMetric;
	}
}
