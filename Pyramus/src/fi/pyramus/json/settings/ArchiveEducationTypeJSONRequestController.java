package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving an education type. 
 */
public class ArchiveEducationTypeJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to archive an education type.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Long educationTypeId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("educationTypeId"));
    baseDAO.archiveEducationType(baseDAO.getEducationType(educationTypeId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
