package fi.pyramus.views.students;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SearchStudentsDialogViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/students/searchstudentsdialog.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
