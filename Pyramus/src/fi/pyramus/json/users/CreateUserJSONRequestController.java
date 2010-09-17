package fi.pyramus.json.users;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of creating a new Pyramus user. 
 * 
 * @see fi.pyramus.views.users.CreateUserViewController
 */
public class CreateUserJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to create a new user. Simply gathers the fields submitted from the
   * web page and adds the user to the database.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    // Fields from the web page

    String firstName = requestContext.getString("firstName");
    String lastName = requestContext.getString("lastName");
    Role role = Role.getRole(requestContext.getInteger("role"));

    // User

    User user = userDAO.createUser(firstName, lastName, "-1", "internal", role);
    
    // Addresses
    
    int addressCount = requestContext.getInteger("addressTable.rowCount");
    for (int i = 0; i < addressCount; i++) {
      String colPrefix = "addressTable." + i;
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String name = requestContext.getString(colPrefix + ".name");
      String street = requestContext.getString(colPrefix + ".street");
      String postal = requestContext.getString(colPrefix + ".postal");
      String city = requestContext.getString(colPrefix + ".city");
      String country = requestContext.getString(colPrefix + ".country");
      boolean hasAddress = name != null || street != null || postal != null || city != null || country != null;
      if (hasAddress) {
        baseDAO.createAddress(user.getContactInfo(), contactType, name, street, postal, city, country, defaultAddress);
      }
    }
    
    // Email addresses

    int emailCount = requestContext.getInteger("emailTable.rowCount");
    for (int i = 0; i < emailCount; i++) {
      String colPrefix = "emailTable." + i;
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String email = requestContext.getString(colPrefix + ".email");
      if (email != null) {
        baseDAO.createEmail(user.getContactInfo(), contactType, defaultAddress, email);
      }
    }
    
    // Phone numbers

    int phoneCount = requestContext.getInteger("phoneTable.rowCount");
    for (int i = 0; i < phoneCount; i++) {
      String colPrefix = "phoneTable." + i;
      Boolean defaultNumber = requestContext.getBoolean(colPrefix + ".defaultNumber");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String number = requestContext.getString(colPrefix + ".phone");
      if (number != null) {
        baseDAO.createPhoneNumber(user.getContactInfo(), contactType, defaultNumber, number);
      }
    }
    
    // Redirect to the Edit User view

    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/users/edituser.page?userId="
        + user.getId());
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
