package fi.pyramus;

import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class JSONRequestController implements fi.internetix.smvc.controllers.JSONRequestController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    throw new RuntimeException("Not implemented");
  }

//  @Override
//  public abstract void process(BinaryRequestContext binaryRequestContext);

  public abstract UserRole[] getAllowedRoles();
}
