package fi.pyramus.views;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;

public abstract class PyramusFormViewController implements PyramusViewController {

  public void process(PageRequestContext pageRequestContext) {
    if ("POST".equals(pageRequestContext.getRequest().getMethod())) {
      processSend(pageRequestContext);
    }
    else {
      processForm(pageRequestContext);
    }
  }
  
  public abstract UserRole[] getAllowedRoles();
  public abstract void processForm(PageRequestContext requestContext);
  public abstract void processSend(PageRequestContext requestContext);
}
