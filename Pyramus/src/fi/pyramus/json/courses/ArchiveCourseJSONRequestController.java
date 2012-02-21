package fi.pyramus.json.courses;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.pyramus.dao.courses.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.framework.JSONRequestController;
import fi.pyramus.framework.UserRole;

public class ArchiveCourseJSONRequestController extends JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    Long courseId = NumberUtils.createLong(requestContext.getRequest().getParameter("courseId"));
    Course course = courseDAO.findById(courseId);
    courseDAO.archive(course);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
