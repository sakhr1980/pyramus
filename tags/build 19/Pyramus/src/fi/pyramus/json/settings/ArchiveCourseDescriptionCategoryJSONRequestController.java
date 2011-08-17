package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving. 
 */
public class ArchiveCourseDescriptionCategoryJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to archive.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long categoryId = jsonRequestContext.getLong("categoryId");
    courseDAO.archiveCourseDescriptionCategory(courseDAO.findCourseDescriptionCategoryById(categoryId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
