package fi.pyramus.json.users;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of editing an existing Pyramus user. 
 * 
 * @see fi.pyramus.views.users.EditUserViewController
 */
public class EditUserJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit an user. Simply gathers the fields submitted from the
   * web page and updates the database.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long userId = requestContext.getLong("userId");

    User user = userDAO.getUser(userId);

    String firstName = requestContext.getString("firstName");
    String lastName = requestContext.getString("lastName");
    Role role = Role.getRole(requestContext.getInteger("role").intValue());
    
    userDAO.updateUser(user, firstName, lastName, role);
    
    // Addresses
    
    Set<Long> existingAddresses = new HashSet<Long>();
    int addressCount = requestContext.getInteger("addressTable.rowCount");
    for (int i = 0; i < addressCount; i++) {
      String colPrefix = "addressTable." + i;
      Long addressId = requestContext.getLong(colPrefix + ".addressId");
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String name = requestContext.getString(colPrefix + ".name");
      String street = requestContext.getString(colPrefix + ".street");
      String postal = requestContext.getString(colPrefix + ".postal");
      String city = requestContext.getString(colPrefix + ".city");
      String country = requestContext.getString(colPrefix + ".country");
      boolean hasAddress = name != null || street != null || postal != null || city != null || country != null;
      if (addressId == -1 && hasAddress) {
        Address address = baseDAO.createAddress(user.getContactInfo(), contactType, name, street, postal, city, country, defaultAddress);
        existingAddresses.add(address.getId());
      }
      else if (addressId > 0) {
        Address address = baseDAO.getAddressById(addressId);
        if (hasAddress) {
          existingAddresses.add(addressId);
          baseDAO.updateAddress(address, defaultAddress, contactType, name, street, postal, city, country);
        }
      }
    }
    List<Address> addresses = user.getContactInfo().getAddresses();
    for (int i = addresses.size() - 1; i >= 0; i--) {
      Address address = addresses.get(i);
      if (!existingAddresses.contains(address.getId())) {
        baseDAO.removeAddress(address);
      }
    }

    // E-mail addresses
    
    Set<Long> existingEmails = new HashSet<Long>();
    int emailCount = requestContext.getInteger("emailTable.rowCount");
    for (int i = 0; i < emailCount; i++) {
      String colPrefix = "emailTable." + i;
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String email = requestContext.getString(colPrefix + ".email");
      Long emailId = requestContext.getLong(colPrefix + ".emailId");
      if (emailId == -1 && email != null) {
        emailId = baseDAO.createEmail(user.getContactInfo(), contactType, defaultAddress, email).getId();
        existingEmails.add(emailId);
      }
      else if (emailId > 0 && email != null) {
        existingEmails.add(emailId);
        baseDAO.updateEmail(baseDAO.getEmailById(emailId), contactType, defaultAddress, email);
      }
    }
    List<Email> emails = user.getContactInfo().getEmails();
    for (int i = emails.size() - 1; i >= 0; i--) {
      Email email = emails.get(i);
      if (!existingEmails.contains(email.getId())) {
        baseDAO.removeEmail(email);
      }
    }

    // Phone numbers
    
    Set<Long> existingPhoneNumbers = new HashSet<Long>();
    int phoneCount = requestContext.getInteger("phoneTable.rowCount");
    for (int i = 0; i < phoneCount; i++) {
      String colPrefix = "phoneTable." + i;
      Boolean defaultNumber = requestContext.getBoolean(colPrefix + ".defaultNumber");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String number = requestContext.getString(colPrefix + ".phone");
      Long phoneId = requestContext.getLong(colPrefix + ".phoneId");
      if (phoneId == -1 && number != null) {
        phoneId = baseDAO.createPhoneNumber(user.getContactInfo(), contactType, defaultNumber, number).getId();
        existingPhoneNumbers.add(phoneId);
      }
      else if (phoneId > 0 && number != null) {
        baseDAO.updatePhoneNumber(baseDAO.getPhoneNumberById(phoneId), contactType, defaultNumber, number);
        existingPhoneNumbers.add(phoneId);
      }
    }
    List<PhoneNumber> phoneNumbers = user.getContactInfo().getPhoneNumbers();
    for (int i = phoneNumbers.size() - 1; i >= 0; i--) {
      PhoneNumber phoneNumber = phoneNumbers.get(i);
      if (!existingPhoneNumbers.contains(phoneNumber.getId())) {
        baseDAO.removePhoneNumber(phoneNumber);
      }
    }

    if (requestContext.getLoggedUserRole() == UserRole.ADMINISTRATOR) {
      String authProvider = requestContext.getString("authProvider");
      
      if (!user.getAuthProvider().equals(authProvider)) {
        userDAO.updateAuthProvider(user, authProvider);
      }
      
      int variableCount = requestContext.getInteger("variablesTable.rowCount");
      for (int i = 0; i < variableCount; i++) {
        String colPrefix = "variablesTable." + i;
        String variableKey = requestContext.getString(colPrefix + ".key");
        String variableValue = requestContext.getString(colPrefix + ".value");
        userDAO.setUserVariable(user, variableKey, variableValue);
      }
    }
    
    if (requestContext.getLoggedUserId().equals(user.getId())) {
      user = userDAO.getUser(user.getId());
      HttpSession session = requestContext.getRequest().getSession(true);
      session.setAttribute("loggedUserName", user.getFullName());
      session.setAttribute("loggedUserRole", UserRole.valueOf(user.getRole().name()));
    }

    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}