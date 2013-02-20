package org.irods.scotty;

import java.io.Serializable;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.domain.Resource;


/**
 * ResourcesController is the backing bean for the iRODS resources list page (resources.xhtml) 
 * note that the getters/setters are used in the resources.xhtml file for display and retrieval
 * of data
 * 
 * @author Lisa Stillwell - RENCI - (www.renci.org)
 *
 */
public class ResourcesController implements Serializable {

	private LoginController loginInfo;
	private List<Resource> allResourcesList;
	
	public ResourcesController() {
		
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
	 * Get full list of resources associated with this grid
	 * 
	 * @return <code>List</code> of {@link Resource} representing all of the resources
	 * 		for this grid
	 */
	public List<Resource> getAllResourcesList() {
		List <Resource> resources = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// first check to see if resources have already been retrieved
		if (this.allResourcesList == null) {
		
			// get iRODS access info from LoginController bean and use to get
			// Jargon Resource Access Object
			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
				// set resource list
				//this.allResourcesList = resourceAO.findAll();
				resources = resourceAO.listResourcesInZone(loginInfo.getZone());
				
				// now get rid of bundleResc
				int idx = 0;
				for (Resource r: resources) {
					if (r.getName().equals("bundleResc")) {
						resources.remove(idx);
					}
					idx++;
				}
				this.allResourcesList = resources;
			} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		
		return this.allResourcesList;
	}
	
	public void setAllResources(List<Resource> resources) {
		this.allResourcesList = resources;
	}
}
