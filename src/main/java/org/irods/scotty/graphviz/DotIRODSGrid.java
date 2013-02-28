package org.irods.scotty.graphviz;

import java.util.List;

import org.irods.jargon.core.pub.domain.Resource;

public class DotIRODSGrid {
	
	private String nodeShape = "box";
	private String nodeStyle = "filled";
	private String nodeFillColor = "white";
	private String style = "filled";
	private String fillColor = "#2694a5";
	private String host;
	private String zone;
	private int port;
	private List<Resource> localResources;
	
	public DotIRODSGrid(String host, int port, String zone, List<Resource> resources) {
		this.host = host;
		this.zone = zone;
		this.port = port;
		this.localResources = resources;
	}
	
	public String getICATDotSource() {
		StringBuilder dotSource = new StringBuilder();
		
		dotSource.append("subgraph cluster_irods_grid {");
		dotSource.append("\n");
		
		// setup node styles
		dotSource.append("node [shape=");
		dotSource.append(nodeShape);
		dotSource.append(", ");
		dotSource.append("style=");
		dotSource.append(nodeStyle);
		dotSource.append(", ");
		dotSource.append("fillcolor=");
		dotSource.append(nodeFillColor);
		dotSource.append("];");
		dotSource.append("\n");
		
		// setup cluster styles
		dotSource.append("style=");
		dotSource.append(style);
		dotSource.append(";");
		dotSource.append("\n");
		dotSource.append("fillcolor=");
		dotSource.append("\"");
		dotSource.append(fillColor);
		dotSource.append("\"");
		dotSource.append(";");
		dotSource.append("\n");
		
		// assemble label
		dotSource.append("label=\"iRODS Host: ");
		dotSource.append(host);
		dotSource.append(", Port: ");
		dotSource.append(port);
		dotSource.append(", Zone: ");
		dotSource.append(zone);
		dotSource.append("\";");
		dotSource.append("\n");
		
		// now setup any local resources
		for (Resource resource: localResources) {
			String rescName = resource.getName();
			dotSource.append("iCAT -> \"");
			dotSource.append(rescName);
			dotSource.append("\";");
			dotSource.append("\n");
		}
		
		// close cluster
		dotSource.append("}");
		dotSource.append("\n");
		
		return dotSource.toString();
	}

}
