package fi.pyramus.services;

import javax.persistence.EnumType;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.ContactTypeDAO;
import fi.pyramus.dao.base.EmailDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.dao.users.UserVariableDAO;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.users.UserEntity;

public class UsersService extends PyramusService {

  public UserEntity[] listUsers() {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return (UserEntity[]) EntityFactoryVault.buildFromDomainObjects(userDAO.listAll()); 
  }
  
  public UserEntity[] listUsersByUserVariable(String key, String value) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return (UserEntity[]) EntityFactoryVault.buildFromDomainObjects(userDAO.listByUserVariable(key, value)); 
  }
  
  public UserEntity createUser(String firstName, String lastName, String externalId, String authProvider, String role) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    Role userRole = EnumType.valueOf(Role.class, role);
    User user = userDAO.create(firstName, lastName, externalId, authProvider, userRole);
    validateEntity(user);
    return EntityFactoryVault.buildFromDomainObject(user);
  }
  

  public void updateUser(Long userId, String firstName, String lastName, String role) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    User user = userDAO.findById(userId);
    Role userRole = EnumType.valueOf(Role.class, role);
    userDAO.update(user, firstName, lastName, userRole);
    validateEntity(user);
  }
  
  public UserEntity getUserById(Long userId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.findById(userId));
  }
  
  public UserEntity getUserByExternalId(String externalId, String authProvider) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.findByExternalIdAndAuthProvider(externalId, authProvider));
  }
  
  public UserEntity getUserByEmail(String email) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    return EntityFactoryVault.buildFromDomainObject(userDAO.findByEmail(email));
  }
  
  public void addUserEmail(Long userId, String address) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    ContactTypeDAO contactTypeDAO = DAOFactory.getInstance().getContactTypeDAO();
    fi.pyramus.domainmodel.users.User user = userDAO.findById(userId);
    // TODO contact type, default address
    ContactType contactType = contactTypeDAO.findById(new Long(1));
    Email email = emailDAO.create(user.getContactInfo(), contactType, Boolean.TRUE, address);
    validateEntity(email);
  }
  
  public void removeUserEmail(Long userId, String address) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    fi.pyramus.domainmodel.users.User user = userDAO.findById(userId);
    for (Email email : user.getContactInfo().getEmails()) {
      if (email.getAddress().equals(address)) {
        emailDAO.delete(email);
        break;
      }
    }
  }
  
  public void updateUserEmail(Long userId, String fromAddress, String toAddress) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    fi.pyramus.domainmodel.users.User user = userDAO.findById(userId);
    for (Email email : user.getContactInfo().getEmails()) {
      if (email.getAddress().equals(fromAddress)) {
        email = emailDAO.update(email, email.getContactType(), email.getDefaultAddress(), toAddress);
        validateEntity(email);
        break;
      }
    }
  }
  
  public String getUserVariable(Long userId, String key) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    UserVariableDAO userVariableDAO = DAOFactory.getInstance().getUserVariableDAO();
    return userVariableDAO.findByUserAndKey(userDAO.findById(userId), key);
  }
  
  public void setUserVariable(Long userId, String key, String value) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();  
    UserVariableDAO userVariableDAO = DAOFactory.getInstance().getUserVariableDAO();
    userVariableDAO.setUserVariable(userDAO.findById(userId), key, value);
  }

}
