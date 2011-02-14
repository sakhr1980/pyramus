package fi.pyramus.views;

import fi.pyramus.PageRequestContext;
import fi.pyramus.RequestController;

public interface PyramusViewController extends RequestController {
  
  public void process(PageRequestContext pageRequestContext);

}
