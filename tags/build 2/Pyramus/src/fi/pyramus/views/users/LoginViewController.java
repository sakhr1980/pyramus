package fi.pyramus.views.users;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.plugin.auth.AuthenticationProviderVault;
import fi.pyramus.plugin.auth.ExternalAuthenticationProvider;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Login view of the application. 
 * 
 * @see fi.pyramus.json.users.LoginJSONRequestController
 */
public class LoginViewController implements PyramusViewController {

  /**
   * Processes the page request. This is a simple case of just including the corresponding login JSP page.
   * Since the login form is submitted via JSON, the actual logic of logging in takes place in
   * {@link fi.pyramus.json.users.LoginJSONRequestController}. 
   * 
   * @param requestContext Page request context
   */
  public void process(PageRequestContext requestContext) {
    AuthenticationProviderVault authorizationProviders = AuthenticationProviderVault.getInstance();
    boolean hasInternals = authorizationProviders.hasInternalStrategies();
    boolean hasExternals = authorizationProviders.hasExternalStrategies();
    
    if (hasExternals && hasInternals) {
      requestContext.setIncludeJSP("/templates/users/login_both.jsp");
    } else {
      if (hasExternals) {
        // TODO: Does not support multiple external providers, yet
        ExternalAuthenticationProvider authorizationProvider = authorizationProviders.getExternalAuthorizationProviders().get(0);
        authorizationProvider.performDiscovery(requestContext);
      } else { 
        requestContext.setIncludeJSP("/templates/users/login_internal.jsp");
      }
    }
  }

  /**
   * Returns the roles allowed to access this page. Naturally, logging in is available for {@link Role#EVERYONE}.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}