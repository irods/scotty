package org.irods.scotty.graphviz;

import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.ZoneAO;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.domain.Zone;

public class DotGridGraphic {
	
	private IRODSAccount irodsAccount;
	private IRODSFileSystem irodsFileSystem;
	private String host;
	private String zone;
	private int port;
	private String fillColor = "gray80";
	private String style = "filled";
	
	public DotGridGraphic(IRODSAccount irodsAccount, IRODSFileSystem irodsFileSystem) {
		this.irodsAccount = irodsAccount;
		this.irodsFileSystem = irodsFileSystem;
		this.host = irodsAccount.getHost();
		this.zone = irodsAccount.getZone();
		this.port = irodsAccount.getPort();
	}
	
	// this is the main method to create the entire dot source to describe this zone
	public String getGridDotSource() {
		// first create grid directed graph and main cluster
		StringBuilder dotSource = new StringBuilder();
		dotSource.append("digraph irods {");
		dotSource.append("\n");
		
		dotSource.append("size=\"10,20\";"); // HOW FORMAT THIS BETTER
		dotSource.append("\n");
//		dotSource.append("ratio=3;");
//		dotSource.append("\n");
		
		dotSource.append("subgraph cluster_all {");
		dotSource.append("\n");
		dotSource.append("fillcolor=");
		dotSource.append(fillColor);
		dotSource.append(";");
		dotSource.append("\n");
		dotSource.append("style=");
		dotSource.append(style);
		dotSource.append(";");
		dotSource.append("\n");
		
		DotIRODSGrid irodsGrid = new DotIRODSGrid(host, port, zone, getLocalResources());
		dotSource.append(irodsGrid.getICATDotSource());
		
		// close cluster_all
		dotSource.append("}");
		dotSource.append("\n");
		
		// add any federated zones
		DotRemoteZone remoteZones = new DotRemoteZone(host, port, zone, getRemoteZones());
		dotSource.append(remoteZones.getICATDotSource());
		
		// end - close digraph
		dotSource.append("}");
		dotSource.append("\n");
		
		return dotSource.toString();
	}
	
	// returns null if none are found
	private List<Zone> getRemoteZones() {
		List<Zone> zones = new ArrayList<Zone>();
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ZoneAO zoneAO = accessObjectFactory.getZoneAO(irodsAccount);
			List<Zone> tmpZones = zoneAO.listZones();
			for (Zone zone: tmpZones) {
				if (zone.getZoneType().equals("remote")) {
					zones.add(zone);
				}
			}
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
			return zones;
		}
	}
	
	// returns null if none are found
	private List<Resource> getRemoteResources() {
		List<Resource> resources = new ArrayList<Resource>();
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
			List<Resource> tmpResources = resourceAO.findAll();
			for (Resource resource: tmpResources) {
				// check to see if this resource is on this Grid's host
				if (! resource.getLocation().equals(this.host)) {
					resources.add(resource);
				}
			}
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
			return resources;
		}
	}
	
	// returns null if none are found
	private List<Resource> getLocalResources() {
		List<Resource> resources = new ArrayList<Resource>();
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
			List<Resource> tmpResources = resourceAO.findAll();
			for (Resource resource: tmpResources) {
				// check to see if this resource is on this Grid's host
				if ((resource.getLocation().equals(this.host)) || 
					(resource.getLocation().equals("localhost"))) {
					resources.add(resource);
				}
			}
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
			return resources;
		}
	}	

}
