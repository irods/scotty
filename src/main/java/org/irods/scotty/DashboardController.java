package org.irods.scotty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.Resource;
import org.irods.jargon.core.pub.domain.User;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;
import org.irods.scotty.graphviz.DotGridGraphic;
import org.irods.scotty.graphviz.GraphViz;
import org.irods.scotty.metrics.GuinanMetrics;
import org.irods.scotty.metrics.IrodsServerMetrics;
import org.irods.scotty.metrics.TimeSeriesMetric;
import org.irods.scotty.utils.GenQueryUtils;
import org.primefaces.model.StreamedContent;

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
	//private Integer numberOfResourcesInZone;
	//private Integer numberOfDataObjectsInZone;
	private Integer numberOfDataObjectsInZoneIncludingTrash;
	private Integer numberOfCollectionsInZoneIncludingTrash;
	private String numberOfCollectionsWithObjectsInZoneIncludingTrash;
	private Long sizeOfDataObjectsInZoneIncludingTrash;
	private Integer numberOfDataObjectsInZoneInTrash;
	private Long sizeOfDataObjectsInZoneInTrash;
	private Integer numberOfCollectionsInZoneInTrash;
	private String numberOfCollectionsWithObjectsInZoneInTrash;
	private String zonePath;
	private List<User> users;
	private List<User> usersInZone;
	private String selectedUser;
	private Integer numberOfDataObjectsInZoneForUserIncludingTrash;
	private Long sizeOfDataObjectsInZoneForUserIncludingTrash;
	private Integer numberOfCollectionsInZoneForUserIncludingTrash;
	private String numberOfCollectionsWithObjectsInZoneForUserIncludingTrash;
	private Integer numberOfDataObjectsInZoneForUserInTrash;
	private Long sizeOfDataObjectsInZoneForUserInTrash;
	private Integer numberOfCollectionsInZoneForUserInTrash;
	private String numberOfCollectionsWithObjectsInZoneForUserInTrash;
	private List<Resource> resources;
	//private List<Resource> resourcesInZone;
	private String selectedResource;
	private Integer numberOfDataObjectsInZoneForResourceIncludingTrash;
	private Long sizeOfDataObjectsInZoneForResourceIncludingTrash;
	//private Integer numberOfCollectionsInZoneForResourceIncludingTrash;
	//private Integer numberOfCollectionsWithObjectsInZoneForResourceIncludingTrash;
	private Integer numberOfDataObjectsInZoneForResourceInTrash;
	private Long sizeOfDataObjectsInZoneForResourceInTrash;
	//private Integer numberOfCollectionsInZoneForResourceInTrash;
	//private Integer numberOfCollectionsWithObjectsInZoneForResourceInTrash;
	private Integer numberOfDataObjectsInZoneForUserForResourceIncludingTrash;
	private Long sizeOfDataObjectsInZoneForUserForResourceIncludingTrash;
	private Integer numberOfDataObjectsInZoneForUserForResourceInTrash;
	private Long sizeOfDataObjectsInZoneForUserForResourceInTrash;
	private String serverAvailabilityPercentage;
	private StreamedContent gridGraphic;

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
	
	public String getZonePath() {
		if (zonePath == null) {
			setZonePath("/" + loginInfo.getZone());
		}
		return zonePath;
	}

	public void setZonePath(String zonePath) {
		this.zonePath = zonePath;
	}
	
	public List<User> getUsers() {
		if (this.users == null) {
			setAllUsers();
		}
		
		return this.usersInZone;
	}
	
	public List<User> getUsersInZone() {
		if (this.usersInZone == null) {
			List<User> ulist = new ArrayList<User>(); 
			if (this.users == null) {
				setAllUsers();
			}
			String zone = loginInfo.getZone();
			for (User user: this.users) {
				if (zone.equals(user.getZone())) {
					ulist.add(user);
				}
			}
			this.usersInZone = ulist;
		}
		
		return this.usersInZone;
	}
	
	public String getSelectedUser() {
		return this.selectedUser;
	}
	
	public void setSelectedUser(String user) {
		this.selectedUser = user;
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
    	
    	// now set the currently selected user to logged in user
    	this.selectedUser=loginInfo.getName();
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
	
	public String getSelectedResource() {
		return this.selectedResource;
	}
	
	public void setSelectedResource(String resource) {
		this.selectedResource = resource;
	}
	
	public List<Resource> getResources() {
		
		if (this.resources == null) {
			IRODSAccessObjectFactory accessObjectFactory;
			
			// get iRODS access info from LoginController bean and use to get
			// Jargon Resource Access Object
			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
	    	try {
	    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
				//List<Resource> resources = resourceAO.findAll();
				List<Resource> resources = resourceAO.listResourcesInZone(loginInfo.getZone());
				
				// now get rid of bundleResc
				int idx = 0;
				for (Resource r: resources) {
					if (r.getName().equals("bundleResc")) {
						resources.remove(idx);
						break;
					}
					idx++;
				}
				
				setResources(resources); 
	    	} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		if (this.resources.size() > 0) {
			setSelectedResource(this.resources.get(0).getName());
		}
    	
    	return this.resources;
	}
	
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	/** 
	 * retrieve a count of the total number of resources associated with this grid
	 * 
	 * @return <code>Integer</code> representing total count of resources
	 */
	public Integer getNumberOfResources() {

		if (this.resources == null) {
			getResources();
		}
		
		int number = this.resources.size();
		setNumberOfResources(number);
		return number;
	}
	
	public void setNumberOfResources(Integer numberOfResources) {
		this.numberOfResources = numberOfResources;
	}
	
//	public List<Resource> getResourcesInZone() {
//		
//		if (this.resourcesInZone == null) {
//			IRODSAccessObjectFactory accessObjectFactory;
//			
//			// get iRODS access info from LoginController bean and use to get
//			// Jargon Resource Access Object
//			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
//			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
//	    	try {
//	    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
//				ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
//				setResourcesInZone(resourceAO.listResourcesInZone(loginInfo.getZone())); 
//	    	} catch (JargonException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				irodsFileSystem.closeAndEatExceptions();
//			}
//		}
//		if (this.resourcesInZone.size() > 0) {
//			setSelectedResource(this.resourcesInZone.get(0).getName());
//		}
//    	
//    	return this.resourcesInZone;
//	}
//	
//	public void setResourcesInZone(List<Resource> resources) {
//		this.resourcesInZone = resources;
//	}
	
	/** 
	 * retrieve a count of the total number of resources associated with this grid
	 * specific to this zone
	 * 
	 * @return <code>Integer</code> representing total count of resources for this zone
	 */
//	public Integer getNumberOfResourcesInZone() {
//		
//		if (this.resourcesInZone == null) {
//			getResourcesInZone();
//		}
//		
//		int number = this.resourcesInZone.size();
//		setNumberOfResourcesInZone(number);
//		return number;
//	}
//	
//	public void setNumberOfResourcesInZone(Integer numberOfResourcesInZone) {
//		this.numberOfResourcesInZone = numberOfResourcesInZone;
//	}
	
	public Long getSizeOfDataObjectsInZoneIncludingTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(false, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), null, null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
//	public Integer getNumberOfDataObjectsInZone() {
//
//		Integer count = 0;
//		try {
//			count = GenQueryUtils.countTotalDataObjectsInZone(loginInfo.getIRODSAccount(),
//				loginInfo.getIRODSFileSystem());
//		} catch (JargonException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return count;
//	}
	
//	public void setNumberOfDatObjectsInZone(Integer numberOfDatObjectsInZone) {
//		this.numberOfDataObjectsInZone = numberOfDatObjectsInZone;
//	}
	
	public Integer getNumberOfDataObjectsInZoneIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null, null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Integer getNumberOfCollectionsInZoneIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public String getNumberOfCollectionsWithObjectsInZoneIncludingTrash() {
		String scount = "N/A";
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsWithObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null);
			if (count >= 0) {
				scount = Integer.toString(count);
			}
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return scount;
	}
	
	public Integer getNumberOfDataObjectsInZoneInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null, null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneInTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(true, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), null, null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfCollectionsInZoneInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	//public Integer getNumberOfCollectionsWithObjectsInZoneInTrash() {
	public String getNumberOfCollectionsWithObjectsInZoneInTrash() {
		String scount = "N/A";
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsWithObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null);
			if (count >= 0) {
				scount = Integer.toString(count);
			}
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return count;
		return scount;
	}
	
	public Integer getNumberOfDataObjectsInZoneForUserIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForUserIncludingTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(false, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), getSelectedUser(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfCollectionsInZoneForUserIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	//public Integer getNumberOfCollectionsWithObjectsInZoneForUserIncludingTrash() {
	public String getNumberOfCollectionsWithObjectsInZoneForUserIncludingTrash() {
		String scount = "N/A";
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsWithObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser());
			if (count >= 0) {
				scount = Integer.toString(count);
			}
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return count;
		return scount;
	}
	
	public Integer getNumberOfDataObjectsInZoneForUserInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForUserInTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(true, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), getSelectedUser(), null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfCollectionsInZoneForUserInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	//public Integer getNumberOfCollectionsWithObjectsInZoneForUserInTrash() {
	public String getNumberOfCollectionsWithObjectsInZoneForUserInTrash() {
		String scount = "N/A";
		Integer count = 0;
		try {
			count = GenQueryUtils.countCollectionsWithObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser());
			if (count >= 0) {
				scount = Integer.toString(count);
			}
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return count;
		return scount;
	}
	
	public Integer getNumberOfDataObjectsInZoneForResourceIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null, getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForResourceIncludingTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(false, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), null, getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfDataObjectsInZoneForResourceInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), null, getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForResourceInTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(true, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), null, getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfDataObjectsInZoneForUserForResourceIncludingTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(false, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser(), getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForUserForResourceIncludingTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(false, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), getSelectedUser(), getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public Integer getNumberOfDataObjectsInZoneForUserForResourceInTrash() {
		Integer count = 0;
		try {
			count = GenQueryUtils.countDataObjectsInZone(true, loginInfo.getIRODSAccount(),
					loginInfo.getIRODSFileSystem(), getSelectedUser(), getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	public Long getSizeOfDataObjectsInZoneForUserForResourceInTrash() {
		Long size = (long) 0;
		try {
			size = GenQueryUtils.sumTotalSizeDataObjectsInZone(true, loginInfo.getIRODSAccount(),
				loginInfo.getIRODSFileSystem(), getSelectedUser(), getSelectedResource());
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return size;
	}
	
	public String getServerAvailabilityPercentage() {
		String percentage = "N/A";
		
		if (this.serverAvailabilityPercentage == null) {
			// get iRODS access info from LoginController bean and use to get
			// Jargon Resource Access Object
			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();

			GuinanMetrics guinanMetrics = new GuinanMetrics(irodsAccount, irodsFileSystem);
			TimeSeriesMetric timeSeriesMetric = guinanMetrics.getNamedMetric("IrodsStatus");
			if (timeSeriesMetric != null) {
				IrodsServerMetrics ism = new IrodsServerMetrics(timeSeriesMetric);
				double rate = ism.getServerUpTimePercentage();
				percentage = String.valueOf(rate) + " %";
			}
		}
		
		this.serverAvailabilityPercentage = percentage;
		return this.serverAvailabilityPercentage;
	}
	
	public StreamedContent getGridGraphic() {
		
		if (this.gridGraphic == null) {
			String type = "gif";
			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
        
			GraphViz gv = new GraphViz();
			DotGridGraphic dotGridGraphic = new DotGridGraphic(irodsAccount, irodsFileSystem);
			String dotSrc = dotGridGraphic.getGridDotSource();
			StreamedContent graphic = gv.getStreamedContent(dotSrc, type);
			this.gridGraphic = graphic;
		}
	    
	    return this.gridGraphic;
	}

	
//	public Integer getNumberOfCollectionsInZoneForResourceIncludingTrash() {
//	Integer count = 0;
//	try {
//		count = GenQueryUtils.countCollectionsInZone(false, loginInfo.getIRODSAccount(),
//				loginInfo.getIRODSFileSystem(), null);
//	} catch (JargonException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return count;
//}
//
//public Integer getNumberOfCollectionsWithObjectsInZoneForResourceIncludingTrash() {
//	Integer count = 0;
//	try {
//		count = GenQueryUtils.countCollectionsWithObjectsInZone(false, loginInfo.getIRODSAccount(),
//				loginInfo.getIRODSFileSystem(), null, getSelectedResource());
//	} catch (JargonException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return count;
//}
//
//	public Integer getNumberOfCollectionsInZoneForResourceInTrash() {
//	Integer count = 0;
//	try {
//		count = GenQueryUtils.countCollectionsInZone(true, loginInfo.getIRODSAccount(),
//				loginInfo.getIRODSFileSystem(), null, getSelectedResource());
//	} catch (JargonException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return count;
//}
//
//public Integer getNumberOfCollectionsWithObjectsInZoneForResourceInTrash() {
//	Integer count = 0;
//	try {
//		count = GenQueryUtils.countCollectionsWithObjectsInZone(true, loginInfo.getIRODSAccount(),
//				loginInfo.getIRODSFileSystem(), null, getSelectedResource());
//	} catch (JargonException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return count;
//}	
	
	
	
	
	// possibly very slow (depending on how big grid is) recursive method
	// to retrieve count of all iRODS data objects under a certain path
	// don't count trash
	// in this case: "/zonename" Yikes!
	// Note: need to return int[] in order to make it mutable
	// TODO: check into how this can be paged or collected by the monitoring process
//	public int[] retrieveFullCountOfDataObjectUnderPath(String path, int[] runningCount) {
//		List<CollectionAndDataObjectListingEntry> children = null;
//		
//		// get iRODS access info from LoginController bean and use to get
//		// Jargon Resource Access Object
//		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
//		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
//    	try {
//    		IRODSAccessObjectFactory accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
//    		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO
//            	= accessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount);
//    		// save number of DataObjects here
//    		runningCount[0] += collectionAndDataObjectListAndSearchAO.listDataObjectsUnderPath(path, 0).size();
//    		
//    		// collect children of this Collection
//    		children = collectionAndDataObjectListAndSearchAO.listCollectionsUnderPath(path, 0);		
//    	} catch (JargonException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			irodsFileSystem.closeAndEatExceptions();
//		}
//    	
//    	// now start looking under children, if any
//    	for (CollectionAndDataObjectListingEntry child : children) {
//    		retrieveFullCountOfDataObjectUnderPath(child.getPathOrName(), runningCount);
//    	}
//		
//		return runningCount;
//	}
	
}
