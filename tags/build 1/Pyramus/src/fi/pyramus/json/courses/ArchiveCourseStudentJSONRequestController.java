package fi.pyramus.json.courses;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveCourseStudentJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long courseStudentId = requestContext.getLong("courseStudentId"); 
    CourseStudent courseStudent = courseDAO.getCourseStudent(courseStudentId);
    courseDAO.archiveCourseStudent(courseStudent);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
