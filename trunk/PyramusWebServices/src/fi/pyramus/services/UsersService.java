package fi.pyramus.services;

import javax.persistence.EnumType;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.users.UserEntity;

public class UsersService extends PyramusService {

  public UserEntity[] listUsers() {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return (UserEntity[]) EntityFactoryVault.buildFromDomainObjects(userDAO.listUsers()); 
  }
  
  public UserEntity[] listUsersByUserVariable(String key, String value) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return (UserEntity[]) EntityFactoryVault.buildFromDomainObjects(userDAO.listUsersByUserVariable(key, value)); 
  }
  
  public UserEntity createUser(String firstName, String lastName, String externalId, String authProvider, String role) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    Role userRole = EnumType.valueOf(Role.class, role);
    User user = userDAO.createUser(firstName, lastName, externalId, authProvider, userRole);
    validateEntity(user);
    return EntityFactoryVault.buildFromDomainObject(user);
  }
  

  public void updateUser(Long userId, String firstName, String lastName, String role) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    User user = userDAO.getUser(userId);
    Role userRole = EnumType.valueOf(Role.class, role);
    userDAO.updateUser(user, firstName, lastName, userRole);
    validateEntity(user);
  }
  
  public UserEntity getUserById(Long userId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.getUser(userId));
  }
  
  public UserEntity getUserByExternalId(String externalId, String authProvider) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.getUser(externalId, authProvider));
  }
  
  public UserEntity getUserByEmail(String email) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.getUserByEmail(email));
  }
  
  public void addUserEmail(Long userId, String address) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    fi.pyramus.domainmodel.users.User user = userDAO.getUser(userId);
    // TODO contact type, default address
    ContactType contactType = baseDAO.getContactTypeById(new Long(1));
    Email email = baseDAO.createEmail(user.getContactInfo(), contactType, Boolean.TRUE, address);
    validateEntity(email);
  }
  
  public void removeUserEmail(Long userId, String address) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    fi.pyramus.domainmodel.users.User user = userDAO.getUser(userId);
    for (Email email : user.getContactInfo().getEmails()) {
      if (email.getAddress().equals(address)) {
        baseDAO.removeEmail(email);
        break;
      }
    }
  }
  
  public void updateUserEmail(Long userId, String fromAddress, String toAddress) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    fi.pyramus.domainmodel.users.User user = userDAO.getUser(userId);
    for (Email email : user.getContactInfo().getEmails()) {
      if (email.getAddress().equals(fromAddress)) {
        email = baseDAO.updateEmail(email, email.getContactType(), email.getDefaultAddress(), toAddress);
        validateEntity(email);
        break;
      }
    }
  }
  
  public String getUserVariable(Long userId, String key) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return userDAO.getUserVariable(userDAO.getUser(userId), key);
  }
  
  public void setUserVariable(Long userId, String key, String value) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    userDAO.setUserVariable(userDAO.getUser(userId), key, value);
  }

}
