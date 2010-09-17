package fi.pyramus.views.users;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import fi.pyramus.ErrorLevel;
import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.I18N.Messages;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.plugin.auth.AuthenticationException;
import fi.pyramus.plugin.auth.AuthenticationProviderVault;
import fi.pyramus.plugin.auth.ExternalAuthenticationProvider;
import fi.pyramus.views.PyramusViewController;

public class ExternalLoginLoginViewController implements PyramusViewController {

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

  // TODO: Does not support multiple external strategies
  public void process(PageRequestContext requestContext) {
    // Ensure that the user trying to login isn't already logged in
    
    Locale locale = requestContext.getRequest().getLocale();
    HttpSession session = requestContext.getRequest().getSession(true);
    if (!session.isNew() && session.getAttribute("loggedUserId") != null) {
      String msg = Messages.getInstance().getText(locale, "users.login.alreadyLoggedIn");
      throw new PyramusRuntimeException(ErrorLevel.INFORMATION, StatusCode.ALREADY_LOGGED_IN, msg);
    }
    
    try {
      AuthenticationProviderVault authorizationProviders = AuthenticationProviderVault.getInstance();
      ExternalAuthenticationProvider authorizationProvider = authorizationProviders.getExternalAuthorizationProviders().get(0);
      User user = authorizationProvider.processResponse(requestContext);
      if (user != null) { 
        // User has been authorized, so store him in the session
        
        session.setAttribute("loggedUserId", user.getId());
        session.setAttribute("loggedUserName", user.getFullName());
        session.setAttribute("loggedUserRole", UserRole.valueOf(user.getRole().name()));
        
        // If the session contains a followup URL, redirect there and if not, redirect to the index page 
        
        if (session.getAttribute("loginFollowupURL") != null) {
          String url = (String) session.getAttribute("loginFollowupURL");
          session.removeAttribute("loginFollowupURL");
          requestContext.setRedirectURL(url);
        }
        else {
          requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
        }
      } else {
        String msg = Messages.getInstance().getText(requestContext.getRequest().getLocale(), "users.login.loginFailed");
        throw new PyramusRuntimeException(ErrorLevel.WARNING, StatusCode.UNAUTHORIZED, msg);
      }
    } catch (AuthenticationException ae) {
      if (ae.getErrorCode() == AuthenticationException.LOCAL_USER_MISSING)
        throw new PyramusRuntimeException(ErrorLevel.WARNING, StatusCode.LOCAL_USER_MISSING, Messages.getInstance().getText(locale, "users.login.localUserMissing"));
      else 
        throw new PyramusRuntimeException(ae);
    }
  }

}
