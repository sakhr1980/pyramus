package fi.pyramus.plugin.simple.auth;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.plugin.auth.InternalAuthenticationProvider;
import fi.pyramus.plugin.simple.dao.SimpleAuthDAO;
import fi.pyramus.plugin.simple.domainmodel.users.SimpleAuth;

@SuppressWarnings("unused")
public class SimpleAuthenticationProvider implements InternalAuthenticationProvider {

  public String getName() {
    return "simple";
  }

  public User getUser(String username, String password) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findByUserNameAndPassword(username, password);
    
    if (simpleAuth != null) {
      User user = userDAO.findByExternalIdAndAuthProvider(String.valueOf(simpleAuth.getId()), getName());
      return user;
    } else {
      return null;
    }
  }
  
  public String getUsername(String externalId) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findById(Long.parseLong(externalId));
    
    if (simpleAuth != null)
      return  simpleAuth.getUsername();
    
    return null;
  }

  public boolean canUpdateCredentials() {
    return true;
  }
  
  public String createCredentials(String username, String password) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.create(username, password);
    
    String externalId = simpleAuth.getId().toString();
  
    return externalId;
  }
  
  public void updatePassword(String externalId, String password) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findById(Long.parseLong(externalId));
    
    simpleAuthDAO.updatePassword(simpleAuth, password);
  }
  
  public void updateUsername(String externalId, String username) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findById(Long.parseLong(externalId));
    
    simpleAuthDAO.updateUsername(simpleAuth, username);
  }

}
