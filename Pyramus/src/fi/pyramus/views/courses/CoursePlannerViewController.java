package fi.pyramus.views.courses;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Create Course view of the application.
 * 
 * @see fi.pyramus.json.users.CreateGradingScaleJSONRequestController
 */
public class CoursePlannerViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    
    List<CourseBean> courseBeans = new ArrayList<CoursePlannerViewController.CourseBean>();
    for (Course course : courseDAO.listCourses()) {
      courseBeans.add(new CourseBean(course));
    }

    pageRequestContext.getRequest().setAttribute("courseBeans", courseBeans);
    
    pageRequestContext.setIncludeJSP("/templates/courses/courseplanner.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Creating courses is available for users with
   * {@link Role#MANAGER} or {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "courses.coursePlanner.pageTitle");
  }

  public class CourseBean {
    
    public CourseBean(Course course) {
      this.course = course;
    }
    
    public String getCourseName() {
      if (StringUtils.isBlank(course.getNameExtension()))
        return course.getName();
      else 
        return course.getName() + " (" + course.getNameExtension() + ")";
    }
    
    public Course getCourse() {
      return course;
    }
    
    private Course course;
  }
}