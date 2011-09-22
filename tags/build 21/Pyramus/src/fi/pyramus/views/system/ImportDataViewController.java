package fi.pyramus.views.system;

import java.io.IOException;

import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.util.DataImporter;
import fi.pyramus.views.PyramusFormViewController;

public class ImportDataViewController extends PyramusFormViewController {

  @Override
  public void processForm(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/system/importdata.jsp");
  }
  
  @Override
  public void processSend(PageRequestContext requestContext) {
    DataImporter dataImporter = new DataImporter();
    try {
      dataImporter.importDataFromStream(requestContext.getFile("file").getInputStream(), null);
    } catch (IOException e) {
      throw new PyramusRuntimeException(e);
    }
    
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }
 
  
}