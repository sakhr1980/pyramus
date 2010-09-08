package fi.pyramus.views.generic;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SimpleDialogViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    String localeId = requestContext.getString("localeId");
    String localeParamsParameter = requestContext.getString("localeParams");
    String[] localeParams = null;
    
    if (!StringUtils.isBlank(localeParamsParameter)) {
      localeParams = localeParamsParameter.split(",");
    }
    
    String message = Messages.getInstance().getText(requestContext.getRequest().getLocale(), localeId, localeParams);
    
    requestContext.getRequest().setAttribute("message", message);
    requestContext.setIncludeJSP("/templates/generic/simpledialog.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
