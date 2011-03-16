package fi.pyramus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.views.PyramusViewController;

public class RequestContextFactory {
  
  public RequestContext createRequestContext(RequestController controller, HttpServletRequest request, HttpServletResponse response) {
    if (controller instanceof PyramusViewController) {
      return new PageRequestContext(request, response);
    }
    else if (controller instanceof JSONRequestController) {
      return new JSONRequestContext(request, response);
    }
    else if (controller instanceof BinaryRequestController) {
      return new BinaryRequestContext(request, response);
    }
    // TODO PyramusRuntimeException?
    throw new InternalError("Invalid request controller: " + controller);
  }
  
}
