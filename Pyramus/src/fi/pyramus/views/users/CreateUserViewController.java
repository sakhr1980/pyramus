package fi.pyramus.views.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.plugin.auth.AuthenticationProvider;
import fi.pyramus.plugin.auth.AuthenticationProviderVault;
import fi.pyramus.plugin.auth.InternalAuthenticationProvider;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Create User view of the application.
 * 
 * @see fi.pyramus.json.users.CreateUserJSONRequestController
 */
public class CreateUserViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response. 
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
        
    List<AuthorizationProviderInfoBean> authorizationProviders = new ArrayList<AuthorizationProviderInfoBean>();
    for (String authorizationProviderName : AuthenticationProviderVault.getAuthenticationProviderClasses().keySet()) {
      boolean active = AuthenticationProviderVault.getInstance().getAuthorizationProvider(authorizationProviderName) != null;
      boolean canUpdateCredentials;
      
      AuthenticationProvider authenticationProvider = AuthenticationProviderVault.getInstance().getAuthorizationProvider(authorizationProviderName);
      
      if (authenticationProvider instanceof InternalAuthenticationProvider) {
        InternalAuthenticationProvider internalAuthenticationProvider = (InternalAuthenticationProvider) authenticationProvider;
        canUpdateCredentials = internalAuthenticationProvider.canUpdateCredentials();
      } else {
        canUpdateCredentials = false;
      }
      
      authorizationProviders.add(new AuthorizationProviderInfoBean(authorizationProviderName, active, canUpdateCredentials));
    }
    
    pageRequestContext.getRequest().setAttribute("contactTypes", baseDAO.listContactTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", baseDAO.listContactURLTypes());
    pageRequestContext.getRequest().setAttribute("authorizationProviders", authorizationProviders);
    
    pageRequestContext.setIncludeJSP("/templates/users/createuser.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Creating new users requires
   * {@link Role#MANAGER} or {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "users.createUser.pageTitle");
  }

}
