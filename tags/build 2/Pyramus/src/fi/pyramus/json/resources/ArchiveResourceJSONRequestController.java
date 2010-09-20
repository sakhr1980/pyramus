package fi.pyramus.json.resources;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveResourceJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    Long resourceId = NumberUtils.createLong(requestContext.getRequest().getParameter("resource"));
    Resource resource = resourceDAO.getResource(resourceId);
    resourceDAO.archiveResource(resource);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
