package fi.pyramus.json.courses;

import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.pyramus.JSONRequestController;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.domainmodel.courses.CourseStudent;

public class ArchiveCourseStudentJSONRequestController extends JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    Long courseStudentId = requestContext.getLong("courseStudentId"); 
    CourseStudent courseStudent = courseStudentDAO.findById(courseStudentId);
    courseStudentDAO.archive(courseStudent);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
