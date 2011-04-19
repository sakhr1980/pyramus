package fi.pyramus.views.users;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SearchUsersDialogViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/users/searchusersdialog.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
