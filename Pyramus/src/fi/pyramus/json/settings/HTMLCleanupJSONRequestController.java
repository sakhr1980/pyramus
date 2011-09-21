package fi.pyramus.json.settings;

import fi.internetix.fck.HTMLCleanup;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class HTMLCleanupJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    String htmlData = jsonRequestContext.getRequest().getParameter("htmlData");
    HTMLCleanup htmlCleanup = new HTMLCleanup();
    try {
      htmlData = htmlCleanup.cleanupHTML(htmlData);
      jsonRequestContext.addResponseParameter("htmlData", htmlData);
    }
    catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
