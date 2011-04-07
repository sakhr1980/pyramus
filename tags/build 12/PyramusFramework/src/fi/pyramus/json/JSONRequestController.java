package fi.pyramus.json;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.RequestController;

/**
 * Interface to define common functionality to all JSON request handlers. 
 * 
 * @author antti.leppä
 */
public interface JSONRequestController extends RequestController {

  /**
   * Method to process the JSON request.
   * 
   * @param jsonRequestContext JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext);

}
