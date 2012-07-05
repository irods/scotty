package org.irods.scotty;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.InvalidUserException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.protovalues.UserTypeEnum;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.domain.User;

public class LoginController implements Serializable {

	private static final long serialVersionUID = -212130003200194733L;
	private String name;
    private String password;
    private String host = "iren.renci.org";
    private int port = 1247;
    private String zone = "renci";
    private String resource = "renci-vault1";
//    private String host;
//    private Integer port;
//    private String zone;
//    private String resource;
    public List<SelectItem> userList;
    private IRODSFileSystem irodsFileSystem = null;
    private IRODSAccessObjectFactory accessObjectFactory = null;
    private IRODSAccount irodsAccount = null;
    private String headerMsg = "Not Logged In";
    private String loginErrorMsg;
    private Boolean authenticated = false;

    public LoginController() {
    }
        

    public String getName ()
    {
        return name;
    }


    public void setName (final String name)
    {
        this.name = name;
    }


    public String getPassword ()
    {
        return password;
    }


    public void setPassword (final String password)
    {
        this.password = password;
    }
   
   
    public String getHost ()
    {
        return host;
    }


    public void setHost (final String host)
    {
        this.host = host;
    }
   

    public Integer getPort ()
    {
        return port;
    }
    
    
    public void setPort (final Integer port)
    {
        this.port = port;
    }


    public String getZone ()
    {
        return zone;
    }


    public void setZone (final String zone)
    {
        this.zone = zone;
    }


    public String getResource ()
    {
        return resource;
    }


    public void setResource (final String resource)
    {
        this.resource = resource;
    }
        
        
    public void setHeaderMsg(String msg) {
    	this.headerMsg = msg;
    }
    
    
    public String getHeaderMsg() {
    	return this.headerMsg;
    }
    
    
    public void setLoginErrorMsg(String msg) {
    	this.loginErrorMsg = msg;
    }
    
    
    public String getLoginErrorMsg() {
    	return this.loginErrorMsg;
    }


//        public Boolean isAuthenticated() {
//        	return this.authenticated;
//        }
    
    
    private void resetLoginCredentials() {
    	setName(null);
    	setPassword(null);
    }
    
    
    private void loginError(String msg) {
    	resetLoginCredentials();
    	//setLoginErrorMsg("Invalid User - Please retry.");
    	MessagesController.addErrorMsg("Cannot Login : " + msg);
    }
    
    
    public String logoutUser() {
    	String outcome = "logout";
    	
    	// TODO: This does not really work - need to use something like Spring Security
    	setHeaderMsg("Not Logged In");
    	resetLoginCredentials();
    	this.authenticated = false;
    	FacesContext facesContext = FacesContext.getCurrentInstance();
    	HttpSession httpSession = (HttpSession)facesContext.getExternalContext().getSession(false);
    	httpSession.invalidate();
    	
    	return outcome; 	
    }
    
    
    public IRODSFileSystem getIRODSFileSystem() {
    	return this.irodsFileSystem;
    }
    
    
    public IRODSAccount getIRODSAccount() {
    	return this.irodsAccount;
    }
    
    
    // create IRODSAccount from login credentials and use to connect, authentic,
    // and verify that this is an iRODS admin user
    public String validateUser() throws Exception
    {
        //IRODSSession irodsSession;
    	//IRODSFileSystem irodsFileSystem = null;
    	//IRODSAccessObjectFactory accessObjectFactory = null;
    	String outcome = "fail";
        
        //IRODSProtocolManager irodsConnectionManager = IRODSSimpleProtocolManager.instance();
    	setLoginErrorMsg("");
        irodsAccount = new IRODSAccount(this.host, this.port, this.name, this.password, "", this.zone, this.resource);

        try {

                //irodsSession = IRODSSession.instance(irodsConnectionManager);
                irodsFileSystem = IRODSFileSystem.instance();
                accessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();


                UserAO userAO = accessObjectFactory.getUserAO(irodsAccount);
                User adminUser = userAO.findByName(this.name);
                if (adminUser.getUserType().equals(UserTypeEnum.RODS_ADMIN)) {
                        outcome = "success";
                }
                else {
                	loginError("Attempted Login is NOT and iRODS Adminstrative User");
                }

        } 
        catch (InvalidUserException e) {
        	loginError("Invalid User/Password Combination");
            return null;
        }
        catch (AuthenticationException e) {
        	loginError("Invalid User/Password Combination");
            return null;
        }
        catch (JargonException e) {
        	loginError(e.getMessage());
        	return null;
        }
        finally {
        	irodsFileSystem.closeAndEatExceptions();
        }

        if(outcome.equals("success")) {
        	this.authenticated = true;
        	setHeaderMsg("Admin User:  ");
        }

        return outcome;
    }

}

