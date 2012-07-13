package org.irods.scotty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.DataNotFoundException;
import org.irods.jargon.core.exception.DuplicateDataException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.User;



/**
 * UserController is the backing bean for the iRODS Users list page 
 * note that the getters/setters are used in the users.xhtml file for display and retrieval
 * of data
 * @author Lisa Stillwell - RENCI - (www.renci.org)
 *
 */
public class UserController implements Serializable {
	
	private static final long serialVersionUID = -8824350212931770902L;
	private LoginController loginInfo;
	private User allUserInfo;
	private List<User> allUsersList;
	private List<User> adminUsersList;
	private List<User> rodsUsersList;
	// may use this instead of sorting in primefaces table
	//private SortFilterModel <User> rodsListModel;
	private DataModel<User> adminListModel;
	private DataModel<User> rodsListModel;
	private boolean sortAscending = true;
	private String sortColumn;
	private String userName;
	private String userId;
	private String userTypes;
	private String userComment;
	private String userInfo;
	private String userPassword;
	private Date createTime;
	private Date modifyTime;
	private String selectedUser;
	private String selectedType;
	private String selectedZone;
	private String statusMsg;
	
	public UserController() {
		
	}
	
	// get access to the session scoped information in the LoginContoller bean
	// which is also defined as a managed property in WEB-INF/faces-config.xml
	public void setLoginInfo(LoginController bean) {
		this.loginInfo = bean;
	}
	public LoginController getLoginInfo() {
		return this.loginInfo;
	}
	
	public String getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
		setupUserById(selectedUser);
	}
	
	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}
	

	public String getSelectedZone() {
		return loginInfo.getZone();
	}

//	public void setSelectedZone(String selectedZone) {
//		this.selectedZone = selectedZone;
//	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * retrieve all user types from UserTypeEnum that are not rodsgroups
	 * or rodsunkown user types
	 * @return <code>List</code> of <code>String</code> specifying defined iRODS user types
	 */
	public List<String> getUserTypes() {
		List <String> abrevTypes = UserTypeEnum.getUserTypeList();
		int size = abrevTypes.size();
		
		// need to go backwards because removing from list changes it's size
		for (int i=size-1; i>=0; i--) {
			// get rid of weird user types for now
			if ((abrevTypes.get(i).equals(UserTypeEnum.RODS_GROUP.getTextValue()))
			 || (abrevTypes.get(i).equals(UserTypeEnum.RODS_UNKNOWN.getTextValue()))) {
				abrevTypes.remove(i);
			}
		}

		return abrevTypes;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}
	
	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	
	public void setUserPassword(String password) {
		this.userPassword = password;
	}
	
	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	
	public boolean isSortAscending() {
		return sortAscending;
	}
	
	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
	
	public String getSortColumn() {
		return sortColumn;
	}
	
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	} 
	
	/**
	 * Retrieve a Jargon User object given a user id
	 * @param id <code>String</code> representing a valid iRODS user id
	 * @return {@link User} Jargon User object representing an iRODS user
	 */
	public User getUserById(String id) {
		User user = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
				// find user using id
				user = userAO.findById(id);
        	} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		return user;
	}
	
	/**
	 * Retrieve a Jargon User object given a user name
	 * @param name <code>String</code> representing a valid iRODS user name
	 * 		not including zone name
	 * @return {@link User} Jargon User object representing an iRODS user
	 */
	public User getUserByName(String name) {
		User user = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
				// get user by name
				user = userAO.findByName(name);
        	} catch (JargonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
        	} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		return user;
	}
	
	/**
	 * Populate this classes current user given a user name
	 * @param name <code>String</code> representing a valid iRODS user name
	 */
	public void setupUserByName(String name) {
		User user = getUserByName(name);
		// populate this user's info
		setAllUserInfo(user);
	}
	
	
	/**
	 * Populate this classes current user given a user id
	 * @param id <code>String</code> representing a valid iRODS user id
	 */
	public void setupUserById(String id) {
		if (id != null) {
			User user = getUserById(id);
			// populate this user's info
			setAllUserInfo(user);
		}
	}
	
	/**
	 * Populate this class's Jargon User object given a User object
	 * @param info {@link User} a Jargon iRODS User object
	 */
	public void setAllUserInfo(User info) {
		this.allUserInfo = info;
		this.userName = info.getName();
		this.userId = info.getId();
		this.selectedType = info.getUserType().getTextValue();
		this.selectedZone = info.getZone();
		this.userComment = info.getComment();
		this.userInfo = info.getInfo();
		this.modifyTime = info.getModifyTime();
		this.createTime = info.getCreateTime();
	}
	
	public User getAllUserInfo() {
		return this.allUserInfo;
	}
	
	/**
	 * Get a list of all the iRODS users associated with this grid
	 * 
	 * @return <code>List</code> of  {@link User} for an iRODS grid
	 */
	public List<User> getAllUsersList() {
		List <User> users = null;
		IRODSAccessObjectFactory accessObjectFactory;
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
    	try {
    		accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
			UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
			users = userAO.findAll();
    	} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			irodsFileSystem.closeAndEatExceptions();
		}
    	
    	return users;
    }
	
	public void setAdminUsersList(DataModel<User> usersList) {
		this.adminListModel = usersList;
	}
	
	/**
	 * Get a list of iRODS administrative users for this iRODS grid
	 * 
	 * @return <code>DataModel</code> of  {@link User} representing complete list of rodsadmin
	 * 		users for an IRODS grid
	 */
	//public List<User> getAdminUsersList() { - may switch back to using List
	public DataModel<User> getAdminUsersList() {	
		
		if (this.adminListModel == null) {
			List<User> allUsers = getAllUsersList();
			List<User> adminUsers = new ArrayList<User>();
		
			// only collect admin users from allUsers list
			for (User user : allUsers) {
				if ((user.getUserType().equals(UserTypeEnum.RODS_ADMIN))
				&& (user.getZone().equals(getSelectedZone()))) {
					adminUsers.add(user);
				}
			}
			this.adminUsersList = adminUsers;
			adminListModel = new ListDataModel<User>(adminUsers);
		}
		
		//return adminUsers;
		return adminListModel;
	}
	
// 	 - may switch back to using List
//	public void setRodsUsersList(List<User> userList) {
//		this.rodsUsersList = userList;
//	}
	public void setRodsUsersList(DataModel<User> userList) {
		this.rodsListModel = userList;
	}
	
	/**
	 * Get a list of iRODS rods users for this iRODS grid
	 * 
	 * @return <code>DataModel</code> of  {@link User} representing complete list of rodsuser
	 * 		users for an IRODS grid
	 */
	//public List<User> getRodsUsersList() {  - may switch back to using List
	public DataModel<User> getRodsUsersList() {
		if (this.rodsListModel == null) {
			List<User> allUsers = getAllUsersList();
			List<User> rodsUsers = new ArrayList<User>();
		
			// only collect rods users from allUsers list
			for (User user : allUsers) {
				if ((user.getUserType().equals(UserTypeEnum.RODS_USER)) 
				&& (user.getZone().equals(getSelectedZone()))) {
					rodsUsers.add(user);
				}
			}
			//rodsListModel = new SortFilterModel<User>(new ListDataModel<User>(rodsUsers));
			this.rodsUsersList = rodsUsers;
			rodsListModel = new ListDataModel<User>(rodsUsers);
		
			//setRodsUsersList(rodsUsers);
		}
		//return rodsUsersList;
		return rodsListModel;
	}
	
	/**
	 * Add an iRODS User
	 * 
	 * @param user {@link User}  user to add
	 * @return <code>String</code> status, if any, generated from user add
	 */
	public String addUser(User user) { 
		IRODSAccessObjectFactory accessObjectFactory;
		String statusMsg = "";
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				// first add user
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
				// add the user to iRODS
				userAO.addUser(user);
				
				// add the password if it has been entered
				if ((this.userPassword != null) && (this.userPassword.length() > 0)) {
					userAO.changeAUserPasswordByAnAdmin(user.getName(), this.userPassword);
				}
			} catch (DuplicateDataException e) {
				statusMsg = e.getMessage();
				e.printStackTrace();
        	} catch (JargonException e) {
        		statusMsg = e.getMessage();
				e.printStackTrace();
			} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		return statusMsg;
	}
	
	/**
	 * Modiy an iRODS User
	 * 
	 * @param user {@link User}  user to modify
	 * @return <code>String</code> status, if any, generated from user update
	 */
	public String updateUser(User user) { 
		IRODSAccessObjectFactory accessObjectFactory;
		String statusMsg = "";
		
		// get iRODS access info from LoginController bean and use to get
		// Jargon User Access Object
		IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
		IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
		if ((irodsAccount != null)  && (irodsFileSystem != null)) {
			try {
				accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
				UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
				// modify the user
				userAO.updateUser(user);
				// change the password if it has been entered
				if ((this.userPassword != null) && (this.userPassword.length() > 0)) {
					userAO.changeAUserPasswordByAnAdmin(user.getName(), this.userPassword);
				}
			} catch (DataNotFoundException e) {
				statusMsg = e.getMessage();
				e.printStackTrace();
        	} catch (JargonException e) {
        		statusMsg = e.getMessage();
				e.printStackTrace();
			} finally {
				irodsFileSystem.closeAndEatExceptions();
			}
		}
		return statusMsg;
	}
	
	/**
	 * Remove an iRODS User
	 * note that removing the currently logged in user is not allowed
	 * 
	 * @param user <code>String</code> specifies user name to remove
	 * @return <code>String</code> status, if any, generated from user remove
	 */
	public String removeUser(String userName) { 
		IRODSAccessObjectFactory accessObjectFactory;
		String statusMsg = "";
		
		// first check to make sure you are not removing currently logged in user!!
		if (loginInfo.getName().equals(userName)) {
			statusMsg = "Cannot remove current Admin User";
		}
		else {
			// get iRODS access info from LoginController bean and use to get
			// Jargon User Access Object
			IRODSAccount irodsAccount = loginInfo.getIRODSAccount();
			IRODSFileSystem irodsFileSystem = loginInfo.getIRODSFileSystem();
			if ((irodsAccount != null)  && (irodsFileSystem != null)) {
				try {
					accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
					UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
					// remove the user
					userAO.deleteUser(userName);
				} catch (InvalidUserException e) {
					statusMsg = e.getMessage();
					e.printStackTrace();
	        	} catch (JargonException e) {
	        		statusMsg = e.getMessage();
					e.printStackTrace();
				} finally {
					irodsFileSystem.closeAndEatExceptions();
				}
			}
		}
		return statusMsg;
	}
	
	/**
	 * Action method to add an iRODS user
	 * @return <code>String</code> is outcome from addition of new iRODS user
	 * 		used for page navigation (defined in WEB-INF faces-config.xml)
	 */
	public String doAddUser() {
		String successMsg = "New user: '" + this.userName + "' created successfully";
		String failMsg = "New user: '" + this.userName + "' NOT created: ";
		String outcome = "user_admin";

		// create a new iRODS user
		User newUser = new User();
		newUser.setName(this.userName);
		newUser.setUserType(UserTypeEnum.findTypeByString(this.selectedType));
		newUser.setZone(getSelectedZone());
		newUser.setComment(this.userComment);
		newUser.setInfo(this.userInfo);
		
		// add the user and get exception message - if any
		String exMsg = addUser(newUser);
		if (exMsg.length() > 0) {
			MessagesController.addErrorMsg(failMsg + exMsg);
			//setStatusMsg(failMsg + exMsg);
			outcome = "fail";
		}
		else {
			//setStatusMsg(successMsg);
			MessagesController.addInfoMsg(successMsg);
		}
		
		return outcome;
	}
	
	/**
	 * Action method to modify an iRODS user
	 * @return <code>String</code> is outcome from update of an iRODS user
	 * 		used for page navigation
	 */
	public String doUpdateUser() {
		String successMsg = "User: '" + this.userName + "' updated successfully";
		String failMsg = "User: '" + this.userName + "' NOT updated: ";
		
		User changedUser = new User();
		changedUser.setId(this.userId);
		changedUser.setName(this.userName);
		changedUser.setUserType(UserTypeEnum.findTypeByString(this.selectedType));
		changedUser.setZone(getSelectedZone());
		changedUser.setComment(this.userComment);
		changedUser.setInfo(this.userInfo);
		
		// add the user and get exception message - if any
		String exMsg = updateUser(changedUser);
		if (exMsg.length() > 0) {
			MessagesController.addErrorMsg(failMsg + exMsg);
			//setStatusMsg(failMsg + exMsg);
		}
		else {
			MessagesController.addInfoMsg(successMsg);
			//setStatusMsg(successMsg);
		}
		
		// dont go anywhere
		return "happy";
	}
	
	/**
	 * Action method to remove an iRODS admin user
	 * @return <code>String</code> is outcome from remove of an iRODS admin user
	 * 		used for page navigation
	 */
	public String doRemoveAdminUser() {
		String userName = adminListModel.getRowData().getName();
		String successMsg = "User: '" + userName + "' successfully removed";
		String failMsg = "User: '" + userName + "' NOT removed: ";
		
		//first remove this user from admin users list to make the list refresh on page
		this.adminUsersList.remove(adminListModel.getRowIndex());

		// remove the user and get exception message - if any
		String exMsg = removeUser(userName);
		if (exMsg.length() > 0) {
			MessagesController.addErrorMsg(failMsg + exMsg);
			//setStatusMsg(failMsg + exMsg);
		}
		else {
			MessagesController.addInfoMsg(successMsg);
			//setStatusMsg(successMsg);
		}
			
		// dont go anywhere
		return "happy";
	}
	
	/**
	 * Action method to remove an iRODS rods user
	 * @return <code>String</code> is outcome from remove of an iRODS rods user
	 * 		used for page navigation
	 */
	public String doRemoveRodsUser() {
		String userName = rodsListModel.getRowData().getName();
		String successMsg = "User: '" + userName + "' successfully removed";
		String failMsg = "User: '" + userName + "' NOT removed: ";
		
		//first remove this user from rods users list to make the list refresh on page
		this.rodsUsersList.remove(rodsListModel.getRowIndex());
		
		// now remove the user and get exception message - if any
		String exMsg = removeUser(userName);
		if (exMsg.length() > 0) {
			MessagesController.addErrorMsg(failMsg + exMsg);
			//setStatusMsg(failMsg + exMsg);
		}
		else {
			MessagesController.addInfoMsg(successMsg);
			//setStatusMsg(successMsg);
		}
			
		// dont go anywhere
		return "happy";
	}
	
	// for testing
	public String doNothing() {
		return "success";
	}
	
	/**
	 *  Force navigation to the create iRODS user page
	 * @return <code>String</code> outcome of goToCreateUser - used for page navigation
	 * 		(defined in WEB-INF/faces-config.xml)
	 */
	public String goToCreateUser() {
		
		return "create";
	}
	
	/**
	 *  Force navigation to the update iRODS admin user page
	 * @return <code>String</code> outcome of goToUpdateAdminUser - used for page navigation
	 * 		(defined in WEB-INF/faces-config.xml)
	 */
	public String goToUpdateAdminUser() {

		User selectedUser = (User) adminListModel.getRowData();
		setAllUserInfo(selectedUser);

		return "update";
	}
	
	/**
	 *  Force navigation to the update iRODS rods user page
	 * @return <code>String</code> outcome of goToUpdateRodsUser - used for page navigation
	 * 		(defined in WEB-INF/faces-config.xml)
	 */
	public String goToUpdateRodsUser() {

		User selectedUser = (User) rodsListModel.getRowData();
		setAllUserInfo(selectedUser);

		return "update";
	}
	
	// sorting methods - may decide to use if not using primefaces table sorting
	/*
	public String sortByName() {
		Collections.sort(getRodsUsersList(), new Comparator<User>() {
			@Override
			public int compare(User u1, User u2) {
				return u1.getName().compareTo(u2.getName());
			}
		});
		
		return null;
	}

	public String sortById() {
		if (sortAscending) {
			rodsListModel.sortBy(new Comparator<User>() {
				//@Override
				public int compare(User u1, User u2) {	 
					return u1.getId().compareTo(u2.getId());
				}
			});
			sortAscending = false;
	 
		  } else {
			//descending order
			  rodsListModel.sortBy(new Comparator<User>() {
				//@Override
				public int compare(User u1, User u2) {
					return u2.getId().compareTo(u1.getId());
				}
			});
			sortAscending = true;
		  }
	 
		  return null;
	}
	
	public String sortByName() {
		if (sortAscending) {
			rodsListModel.sortBy(new Comparator<User>() {
				//@Override
				public int compare(User u1, User u2) {	 
					return u1.getName().compareTo(u2.getName());
				}
			});
			sortAscending = false;
	 
		  } else {
			//descending order
			  rodsListModel.sortBy(new Comparator<User>() {
				//@Override
				public int compare(User u1, User u2) {
					return u2.getName().compareTo(u1.getName());
				}
			});
			sortAscending = true;
		  }
	 
		  return null;
	}
	*/
}
