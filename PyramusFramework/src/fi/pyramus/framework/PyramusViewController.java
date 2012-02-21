package fi.pyramus.framework;

import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.PageController;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class PyramusViewController implements PageController {

  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    throw new RuntimeException("Not implemented");
  }

  public abstract UserRole[] getAllowedRoles();
}
