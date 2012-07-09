package org.irods.scotty;

import java.io.Serializable;
import java.util.List;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.ResourceAO;
import org.irods.jargon.core.pub.domain.Resource;

public class ResourcesController implements Serializable {

	private LoginController loginInfo;
	private List<Resource> allResourcesList;
	
	public ResourcesController() {
		
	}
	public void setLoginInfo(LoginController bean) {
		this.loginInfo = bean;
	}
	public LoginController getLoginInfo() {
		return this.loginInfo;
	}
	
	public List<Resource> getAllResourcesList() {
		List <Resource> resources = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
		try {
			accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			ResourceAO resourceAO = accessObjectFactory.getResourceAO(irodsAccount);
			resources = resourceAO.findAll();
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
		
		return resources;
	}
}
