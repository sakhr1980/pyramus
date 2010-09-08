package fi.pyramus.json.users;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.I18N.Messages;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.plugin.auth.AuthenticationException;
import fi.pyramus.plugin.auth.AuthenticationProviderVault;
import fi.pyramus.plugin.auth.InternalAuthenticationProvider;

/**
 * The controller responsible of logging in the user with the credentials he has provided. 
 * 
 * @see fi.pyramus.views.users.LoginViewController
 */
public class LoginJSONRequestController implements JSONRequestController {
  
  /**
   * Processes the request to log in. Authorizes the given credentials and if they match a user,
   * stores the user into the session (keys <code>loggedUserId</code>, <code>loggedUserName</code>,
   * and <code>loggedUserRole</code>).
   * <p/>
   * If the session contains a <code>loginFollowupURL</code> key, redirects the user to that URL.
   * Otherwise, redirects back to the index page of the application.
   * <p/>
   * If the user is already logged in or the authorization fails, a <code>PyramusRuntimeException</code>
   * is thrown with a localized message stating so.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    
    // Fields submitted from the web page
    
    String username = jsonRequestContext.getRequest().getParameter("username");
    String password = jsonRequestContext.getRequest().getParameter("password");
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    // Ensure that the user trying to login isn't already logged in
    
    HttpSession session = jsonRequestContext.getRequest().getSession(true);
    if (!session.isNew() && session.getAttribute("loggedUserId") != null) {
      String msg = Messages.getInstance().getText(locale, "users.login.alreadyLoggedIn");
      throw new PyramusRuntimeException(ErrorLevel.INFORMATION, StatusCode.ALREADY_LOGGED_IN, msg);
    }
    
    // Go through all authorization providers and see if one authorizes the given credentials
    
    for (InternalAuthenticationProvider provider : AuthenticationProviderVault.getInstance().getInternalAuthorizationProviders()) {
      try {
      User user = provider.getUser(username, password);
        if (user != null) {
          
          // User has been authorized, so store him in the session
          
          session.setAttribute("loggedUserId", user.getId());
          session.setAttribute("loggedUserName", user.getFullName());
          session.setAttribute("loggedUserRole", UserRole.valueOf(user.getRole().name()));
          
          // If the session contains a followup URL, redirect there and if not, redirect to the index page 
          
          if (session.getAttribute("loginFollowupURL") != null) {
            String url = (String) session.getAttribute("loginFollowupURL");
            session.removeAttribute("loginFollowupURL");
            jsonRequestContext.setRedirectURL(url);
          }
          else {
            jsonRequestContext.setRedirectURL(jsonRequestContext.getRequest().getContextPath() + "/index.page");
          }
          return;
        }
      } catch (AuthenticationException ae) {
        if (ae.getErrorCode() == AuthenticationException.LOCAL_USER_MISSING)
          throw new PyramusRuntimeException(ErrorLevel.WARNING, StatusCode.LOCAL_USER_MISSING, Messages.getInstance().getText(locale, "users.login.localUserMissing"));
        else 
          throw new PyramusRuntimeException(ae);
      }
    }
    
    // Reaching this point means no authorization provider authorized the user, so throw a login exception 
    
    String msg = Messages.getInstance().getText(jsonRequestContext.getRequest().getLocale(), "users.login.loginFailed");
    throw new PyramusRuntimeException(ErrorLevel.WARNING, StatusCode.UNAUTHORIZED, msg);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
