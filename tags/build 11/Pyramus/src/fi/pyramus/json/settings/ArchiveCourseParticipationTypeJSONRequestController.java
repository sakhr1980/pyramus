package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving a course participation type. 
 */
public class ArchiveCourseParticipationTypeJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long courseParticipationTypeId = jsonRequestContext.getLong("courseParticipationTypeId");
    courseDAO.archiveCourseParticipationType(courseDAO.getCourseParticipationType(courseParticipationTypeId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
