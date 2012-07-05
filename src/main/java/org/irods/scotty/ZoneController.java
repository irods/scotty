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

public class ZoneController implements Serializable {
	
	private static final long serialVersionUID = -4070325313708559439L;
	private LoginController loginInfo1;
	private List<Zone> zones;
	private List<String> userZones;
	
	public ZoneController() {
	}
	
	public LoginController getLoginInfo1() {
		return loginInfo1;
	}

	public void setLoginInfo1(LoginController login) {
		this.loginInfo1 = login;
	}

	public List<Zone> listZones() {
		List<Zone> zones = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		IRODSAccount irodsAccount = loginInfo1.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo1.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				ZoneAO zoneAO = accessObjectFactory.getZoneAO(irodsAccount);
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
	
	public List<String> getZoneNameList() {
		List<String> zoneNames = new ArrayList<String>();
		
		if (this.zones == null) {
			this.zones = listZones();
		}
		
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
