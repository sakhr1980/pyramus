package fi.pyramus.views.generic;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.PyramusViewController;

public class IndexViewController extends PyramusViewController {

  public void process(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/index.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
