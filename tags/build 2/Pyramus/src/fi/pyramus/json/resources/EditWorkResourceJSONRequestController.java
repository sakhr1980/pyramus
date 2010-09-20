package fi.pyramus.json.resources;

import org.apache.commons.lang.math.NumberUtils;
import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.domainmodel.resources.WorkResource;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class EditWorkResourceJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {    
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    Long resourceId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("resource"));
    Double hourlyCost = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("hourlyCost"));
    Double costPerUse = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("costPerUse"));
    Long version = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("version"));
    
    WorkResource workResource = resourceDAO.getWorkResource(resourceId);
    if (!version.equals(workResource.getVersion())) 
      throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.CONCURRENT_MODIFICATION, Messages.getInstance().getText(jsonRequestContext.getRequest().getLocale(), "generic.errors.concurrentModification"));
    
    ResourceCategory resourceCategory = resourceDAO.getResourceCategory(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("category")));
    
    resourceDAO.updateWorkResource(workResource, name, resourceCategory, costPerUse, hourlyCost);
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
