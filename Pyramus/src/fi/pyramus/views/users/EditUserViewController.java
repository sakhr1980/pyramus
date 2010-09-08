package fi.pyramus.views.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.pyramus.PageRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.plugin.auth.AuthenticationProvider;
import fi.pyramus.plugin.auth.AuthenticationProviderVault;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit User view of the application.
 * 
 * @see fi.pyramus.json.users.EditUserJSONRequestController
 */
public class EditUserViewController implements PyramusViewController {

  /**
   * Processes the page request by including the corresponding JSP page to the response. 
   * 
   * @param requestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    // TODO loggedUserRole vs. user role
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    User user = userDAO.getUser(pageRequestContext.getLong("userId"));
    
    Set<String> registeredAuthorizationProviders = AuthenticationProviderVault.getAuthenticationProviderClasses().keySet();
    Map<String, Boolean> activeAuthorizationProviders = new HashMap<String, Boolean>();
    
    for (AuthenticationProvider authorizationProvider : AuthenticationProviderVault.getInstance().getAuthenticationProviders()) {
      activeAuthorizationProviders.put(authorizationProvider.getName(), registeredAuthorizationProviders.contains(authorizationProvider.getName()));
    }
    
    pageRequestContext.getRequest().setAttribute("user", user);
    pageRequestContext.getRequest().setAttribute("contactTypes", baseDAO.listContactTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", baseDAO.listContactURLTypes());
    pageRequestContext.getRequest().setAttribute("variableKeys", userDAO.listUserVariableKeys());
    pageRequestContext.getRequest().setAttribute("activeAuthorizationProviders", activeAuthorizationProviders);
    pageRequestContext.getRequest().setAttribute("registeredAuthorizationProviders", registeredAuthorizationProviders);
    
    pageRequestContext.setIncludeJSP("/templates/users/edituser.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Available for only those
   * with {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
