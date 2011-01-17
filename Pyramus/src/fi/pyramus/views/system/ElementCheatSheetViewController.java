package fi.pyramus.views.system;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class ElementCheatSheetViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/system/elementcheatsheet.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
