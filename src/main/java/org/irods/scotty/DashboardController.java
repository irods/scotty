package org.irods.scotty;

import java.io.Serializable;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.domain.User;

/**
 * DashboardController is the backing bean for the iRODS Admin Dashboard 
 * note that the getters/setters are used in the dashboard.xhtml file for display and retrieval
 * of data
 * 
 * @author Lisa Stillwell - RENCI - (www.renci.org)
 *
 */
public class DashboardController implements Serializable {

	private LoginController loginInfo;
	private Integer numberOfAdminUsers;
	private Integer numberOfRodsUsers;
	private Integer numberOfUsers;
	private Integer numberOfResources;
	private Integer numberOfResourcesInZone;
	private List<User> users;
	private List<Resource> resources;

	public DashboardController() {
		
	}
	
	// get access to the session scoped information in the LoginContoller bean
	// which is also defined as a managed property in WEB-INF/faces-config.xml
	public void setLoginInfo(LoginController bean) {
		this.loginInfo = bean;
	}
	public LoginController getLoginInfo() {
		return this.loginInfo;
	}
	
	/**
	 * Set a list of all of the iRODS users - including all types
	 */
	public void setAllUsers() {
		Integer number = 0;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
			this.users = userAO.findAll(); 
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
	}
	
	/**
	 * retrieve a count of the rodsadmin and rodsuser type users only
	 * 
	 * @return <code>Integer</code> representing total count of rodsadmin
	 * 		and rodsuser user types
	 */
	public Integer getNumberOfUsers() {
		Integer number = 0;
		
		if (this.users == null) {
			setAllUsers();
		}
		
		for (User user : this.users) {
			if ((user.getUserType().equals(UserTypeEnum.RODS_ADMIN))
				|| (user.getUserType().equals(UserTypeEnum.RODS_USER))) {
			// TODO: && (user.getZone().equals(getSelectedZone()))) { may want to do this or list separate zone numbers
				number++;
			}
		}
		return number;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	
	/**
	 * retrieve a count of the type rodsadmin users only
	 * 
	 * @return <code>Integer</code> representing total count of rodsadmin
	 * 		 user type
	 */
	public Integer getNumberOfAdminUsers() {
		Integer number = 0;

		if (this.users == null) {
			getNumberOfUsers();
		}
		
		for (User user : this.users) {
			if ((user.getUserType().equals(UserTypeEnum.RODS_ADMIN))) {
				number++;
			}
		}
		return number;
	}

	public void setNumberOfAdminUsers(Integer numberOfAdminUsers) {
		this.numberOfAdminUsers = numberOfAdminUsers;
	}

	/**
	 * retrieve a count of the type rodsusers users only
	 * 
	 * @return <code>Integer</code> representing total count of rodsusers
	 * 		 user type
	 */
	public Integer getNumberOfRodsUsers() {
		Integer number = 0;
		
		if (this.users == null) {
			getNumberOfUsers();
		}
		
		for (User user : this.users) {
			if ((user.getUserType().equals(UserTypeEnum.RODS_USER))) {
				number++;
			}
		}
		return number;
	}

	public void setNumberOfRodsUsers(Integer numberOfRodsUsers) {
		this.numberOfRodsUsers = numberOfRodsUsers;
	}
	
	/** 
	 * retrieve a count of the total number of resources associated with this grid
	 * 
	 * @return <code>Integer</code> representing total count of resources
	 */
	public Integer getNumberOfResources() {
		Integer number = 0;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon Resource Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
			number = resourceAO.findAll().size(); 
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
		
		return number;
	}
	
	public void setNumberOfResources(Integer numberOfResources) {
		this.numberOfResources = numberOfResources;
	}
	
	/** 
	 * retrieve a count of the total number of resources associated with this grid
	 * specific to this zone
	 * 
	 * @return <code>Integer</code> representing total count of resources for this zone
	 */
	public Integer getNumberOfResourcesInZone() {
		Integer number = 0;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon Resource Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
			number = resourceAO.listResourcesInZone(loginInfo.getZone()).size();
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
		
		return number;
	}
	
	public void setNumberOfResourcesInZone(Integer numberOfResourcesInZone) {
		this.numberOfResourcesInZone = numberOfResourcesInZone;
	}
}
