package fi.pyramus.views.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.util.CSVImporter;
import fi.pyramus.views.PyramusFormViewController;

public class ImportCSVViewController extends PyramusFormViewController {


  @Override
  public void processForm(PageRequestContext requestContext) {
    requestContext.setIncludeJSP("/templates/system/importcsv.jsp");
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void processSend(PageRequestContext requestContext) {
    String className = requestContext.getString("entity");
    
    Class entityClass;
    try {
      entityClass = Class.forName(className);
    } catch (ClassNotFoundException e1) {
      throw new PyramusRuntimeException(e1);
    }

    CSVImporter dataImporter = new CSVImporter();
    List<Object> list;
    try {
      list = dataImporter.importDataFromStream(requestContext.getFile("file").getInputStream(), 
          entityClass, requestContext.getLoggedUserId(), requestContext.getRequest().getLocale());
    } catch (IOException e) {
      throw new PyramusRuntimeException(e);
    }
    
    List<Object> entityList = new ArrayList<Object>();
    for (int i = 0; i < list.size(); i++) {
      Object o = list.get(i);
      
      if ((o != null) && (o.getClass().equals(entityClass)))
        entityList.add(o);
    }
    
    requestContext.setIncludeJSP("/templates/system/importcsv.jsp");
    requestContext.getRequest().setAttribute("fields", dataImporter.getHeaderFields());
    requestContext.getRequest().setAttribute("entities", list);
    requestContext.getRequest().setAttribute("entityClassEntities", entityList);
    requestContext.getRequest().setAttribute("entityClass", entityClass.getName());
    
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }
 
  
}