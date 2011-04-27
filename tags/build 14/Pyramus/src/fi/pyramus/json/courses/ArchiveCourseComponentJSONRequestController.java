package fi.pyramus.json.courses;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseComponent;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveCourseComponentJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long componentId = requestContext.getLong("componentId");    
    CourseComponent component = courseDAO.getCourseComponent(componentId);
    courseDAO.archiveCourseComponent(component);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
