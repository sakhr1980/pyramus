package fi.pyramus.json.resources;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.domainmodel.resources.WorkResource;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class CreateWorkResourceJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {    
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    Double hourlyCost = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("hourlyCost"));
    Double costPerUse = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("costPerUse"));
    ResourceCategory resourceCategory = resourceDAO.getResourceCategory(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("category")));
    WorkResource workResource = resourceDAO.createWorkResource(name, resourceCategory, costPerUse, hourlyCost);
    
    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/resources/editworkresource.page?resource=" + workResource.getId();
    String refererAnchor = jsonRequestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    jsonRequestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
