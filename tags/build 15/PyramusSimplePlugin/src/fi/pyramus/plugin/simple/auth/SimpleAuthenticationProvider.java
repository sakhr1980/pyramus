package fi.pyramus.plugin.simple.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;

import fi.pyramus.ErrorLevel;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.users.InternalAuth;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.plugin.auth.AuthenticationException;
import fi.pyramus.plugin.auth.InternalAuthenticationProvider;
import fi.pyramus.plugin.simple.dao.SimpleAuthDAO;
import fi.pyramus.plugin.simple.domainmodel.users.SimpleAuth;

@SuppressWarnings("unused")
public class SimpleAuthenticationProvider implements InternalAuthenticationProvider {

  @Override
  public String getName() {
    return "simple";
  }

  @Override
  public User getUser(String username, String password) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findSimpleAuthByUserNameAndPassword(username, password);
    
    if (simpleAuth != null) {
      User user = userDAO.getUser(String.valueOf(simpleAuth.getId()), getName());
      return user;
    } else {
      return null;
    }
  }
  
  @Override
  public String getUsername(String externalId) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findSimpleAuthById(Long.parseLong(externalId));
    
    if (simpleAuth != null)
      return  simpleAuth.getUsername();
    
    return null;
  }

  @Override
  public boolean canUpdateCredentials() {
    return true;
  }
  
  @Override
  public String createCredentials(String username, String password) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.createSimpleAuth(username, password);
    
    String externalId = simpleAuth.getId().toString();
  
    return externalId;
  }
  
  @Override
  public void updatePassword(String externalId, String password) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findSimpleAuthById(Long.parseLong(externalId));
    
    simpleAuthDAO.updateSimpleAuthPassword(simpleAuth, password);
  }
  
  @Override
  public void updateUsername(String externalId, String username) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findSimpleAuthById(Long.parseLong(externalId));
    
    simpleAuthDAO.updateSimpleAuthUsername(simpleAuth, username);
  }

}
