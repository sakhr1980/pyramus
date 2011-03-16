package fi.pyramus.views.system;

import java.util.Date;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SystemInfoViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    
    requestContext.getRequest().setAttribute("properties", System.getProperties());
    requestContext.getRequest().setAttribute("env", System.getenv());
    requestContext.getRequest().setAttribute("date", new Date());
    
    requestContext.setIncludeJSP("/templates/system/systeminfo.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
