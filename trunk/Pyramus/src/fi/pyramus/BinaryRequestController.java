package fi.pyramus;

import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class BinaryRequestController implements fi.internetix.smvc.controllers.BinaryRequestController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    throw new RuntimeException("Not implemented");
  }

  public abstract void process(BinaryRequestContext binaryRequestContext);
  
  public abstract UserRole[] getAllowedRoles();
}
