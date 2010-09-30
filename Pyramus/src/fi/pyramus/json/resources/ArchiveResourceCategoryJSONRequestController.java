package fi.pyramus.json.resources;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving a resource category. 
 */
public class ArchiveResourceCategoryJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to archive a resource category.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    Long resourceCategoryId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("resourceCategoryId"));
    resourceDAO.archiveResourceCategory(resourceDAO.findResourceCategoryById(resourceCategoryId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
