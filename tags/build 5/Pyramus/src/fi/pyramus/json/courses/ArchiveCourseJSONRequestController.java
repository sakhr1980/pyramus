package fi.pyramus.json.courses;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveCourseJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long courseId = NumberUtils.createLong(requestContext.getRequest().getParameter("courseId"));
    Course course = courseDAO.getCourse(courseId);
    courseDAO.archiveCourse(course);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
