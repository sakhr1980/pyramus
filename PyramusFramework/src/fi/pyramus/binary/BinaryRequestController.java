package fi.pyramus.binary;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.RequestController;

public interface BinaryRequestController extends RequestController {
  
  public void process(BinaryRequestContext binaryRequestContext);

}
