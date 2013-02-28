package org.irods.scotty.graphviz;

import java.util.List;

import org.irods.jargon.core.pub.domain.Zone;

public class DotRemoteZone {
	private String shape = "box";
	private String style = "filled";
	private String nodeStyle = "filled";
	private String nodeFillColor = "white";
	private String fillColor = "gray80";
	private String host;
	private String zone;
	private int port;
	private List<Zone> remoteZones;
	
	public DotRemoteZone(String host, int port, String zone, List<Zone> zones) {
		this.host = host;
		this.zone = zone;
		this.port = port;
		this.remoteZones = zones;
	}
	
	public String getICATDotSource() {
		StringBuilder dotSource = new StringBuilder();
		
		dotSource.append("subgraph cluster_fed_grid {");
		dotSource.append("\n");
		dotSource.append("style=");
		dotSource.append(style);
		dotSource.append(";");
		dotSource.append("\n");
		dotSource.append("fillcolor=");
		dotSource.append(fillColor);
		dotSource.append(";");
		dotSource.append("\n");
		
		// add label
		dotSource.append("label=\"Federated Zones\";");
		dotSource.append("\n");
		
		// setup node styles
		dotSource.append("node [shape=");
		dotSource.append(shape);
		dotSource.append(", ");
		dotSource.append("style=");
		dotSource.append(nodeStyle);
		dotSource.append(", ");
		dotSource.append("fillcolor=");
		dotSource.append(nodeFillColor);
		dotSource.append("];");
		dotSource.append("\n");
		
		for (Zone zone: remoteZones) {
			
			String zoneName = zone.getZoneName();
			String zoneConn = zone.getZoneConnection();		
			
			// zone name for node
			dotSource.append("\"");
			dotSource.append(zoneName);
			dotSource.append("\"");
			dotSource.append(";");
			dotSource.append("\n");
			
			
			
			// now add this edge
			dotSource.append("iCAT -> ");
			dotSource.append("\"");
			dotSource.append(zoneName);
			dotSource.append("\"");
			dotSource.append(" [label=");
			dotSource.append("\"");
			dotSource.append(zoneConn);
			dotSource.append("\"");
			dotSource.append("];");
			dotSource.append("\n");	
		}
		// end subgraph
		dotSource.append("}");
		dotSource.append("\n");
		
		return dotSource.toString();
	}
}
