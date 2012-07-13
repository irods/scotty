package org.irods.scotty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.ZoneAO;
import org.irods.jargon.core.pub.domain.User;
import org.irods.jargon.core.pub.domain.Zone;

/**
* ZoneController provides access to iRODS zone information
* note that the getters/setters are used in the .xhtml files for display
* and retrieval of data
* 
* @author Lisa Stillwell - RENCI - (www.renci.org)
*
*/
public class ZoneController implements Serializable {
	
	private static final long serialVersionUID = -4070325313708559439L;
	private LoginController loginInfo1;
	private List<Zone> zones;
	private List<String> userZones;
	
	public ZoneController() {
	}
	
	// get access to the session scoped information in the LoginContoller bean
		// which is also defined as a managed property in WEB-INF/faces-config.xml
	public LoginController getLoginInfo1() {
		return loginInfo1;
	}

	public void setLoginInfo1(LoginController login) {
		this.loginInfo1 = login;
	}

	/**
	 * Get a complete list of iRODS zones
	 * 
	 * @return <code>List</code> {@link Zone}  contains complete list of iRODS zones for a grid
	 */
	public List<Zone> listZones() {
		List<Zone> zones = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon Zone Access Object
		IRODSAccount irodsAccount = loginInfo1.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo1.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				ZoneAO zoneAO = accessObjectFactory.getZoneAO(irodsAccount);
				// get list of zones
				zones = zoneAO.listZones();
        	} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		return zones;
	}
	
	/**
	 * Get a list of iRODS zone names for this grid
	 * 
	 * @return <code>List</code> of <code>String</code> contains full list of iRODS zone names for a grid
	 */
	public List<String> getZoneNameList() {
		List<String> zoneNames = new ArrayList<String>();
		
		// if the list of zones have NOT already been retrieved - get them
		if (this.zones == null) {
			this.zones = listZones();
		}
		
		// now build List of zone names
		for (Zone zone : this.zones) {
			zoneNames.add(zone.getZoneName());
		}
		return zoneNames;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}

	public List<String> getUserZones() {
		return getZoneNameList();
	}

	public void setUserZones(List<String> userZones) {
		this.userZones = userZones;
	}	
	
}
