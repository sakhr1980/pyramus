package fi.pyramus.json.courses;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseComponentResource;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class DeleteCourseComponentResourceJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long componentResourceId = requestContext.getLong("courseComponentResourceId");    
    CourseComponentResource componentResource = courseDAO.findComponentResourceById(componentResourceId);
    courseDAO.deleteCourseComponentResource(componentResource);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
