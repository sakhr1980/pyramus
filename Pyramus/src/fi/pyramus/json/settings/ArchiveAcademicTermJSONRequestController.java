package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving a grading scale. 
 */
public class ArchiveAcademicTermJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to create a new grading scale.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    Long academicTermId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("academicTermId"));
    baseDAO.archiveAcademicTerm(baseDAO.getAcademicTerm(academicTermId));
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
