package fi.pyramus.json.courses;

import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.courses.CourseComponentDAO;
import fi.pyramus.domainmodel.courses.CourseComponent;
import fi.pyramus.framework.JSONRequestController;
import fi.pyramus.framework.UserRole;

public class ArchiveCourseComponentJSONRequestController extends JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseComponentDAO componentDAO = DAOFactory.getInstance().getCourseComponentDAO();
    Long componentId = requestContext.getLong("componentId");    
    CourseComponent component = componentDAO.findById(componentId);
    componentDAO.archive(component);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
