package fi.pyramus;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import fi.pyramus.logging.Logging;

public class RequestControllerMapper {

  /**
   * Returns the page request controller for the given name. If a controller by the name cannot be
   * found, returns <code>null</code>.
   * 
   * @param name Page request controller nmae
   * 
   * @return The page request controller for the given name, or <code>null</code> if not found
   */
  public static RequestController getRequestController(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String ctxPath = request.getContextPath();
    String controllerName = uri.substring(ctxPath.length() + 1);
    return requestControllers.get(controllerName);
  }

  @SuppressWarnings("unchecked")
  public final static void mapControllers(Properties properties, String urlPostfix) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    Enumeration<Object> keys = properties.keys();
    while (keys.hasMoreElements()) {
      String name = (String) keys.nextElement();
      String key = name + urlPostfix;
      Class<RequestController> controller = (Class<RequestController>) Class.forName((String) properties.get(name));
      requestControllers.put(key, controller.newInstance());
      Logging.logDebug("Registered: " + key + " : " + controller);
    }
  }

  /**
   * Hashmap for page request controller names and their instances.
   */
  private static Map<String, RequestController> requestControllers = new HashMap<String, RequestController>();

}
