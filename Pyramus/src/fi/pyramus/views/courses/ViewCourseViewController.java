package fi.pyramus.views.courses;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.courses.CourseUser;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the View Course view of the application.
 */
public class ViewCourseViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    // The course to be edited
    
    Course course = courseDAO.getCourse(pageRequestContext.getLong("course"));
    pageRequestContext.getRequest().setAttribute("course", course);
    
    List<CourseStudent> courseStudents = courseDAO.listCourseStudentsByCourse(course);
    Collections.sort(courseStudents, new Comparator<CourseStudent>() {
      @Override
      public int compare(CourseStudent o1, CourseStudent o2) {
        int cmp = o1.getStudent().getLastName().compareToIgnoreCase(o2.getStudent().getLastName());
        if (cmp == 0)
          cmp = o1.getStudent().getFirstName().compareToIgnoreCase(o2.getStudent().getFirstName());
        return cmp;
      }
    });
    
    List<CourseUser> courseUsers = courseDAO.listCourseUsers(course);
    Collections.sort(courseUsers, new Comparator<CourseUser>() {
      @Override
      public int compare(CourseUser o1, CourseUser o2) {
        int cmp = o1.getUser().getLastName().compareToIgnoreCase(o2.getUser().getLastName());
        if (cmp == 0)
          cmp = o1.getUser().getFirstName().compareToIgnoreCase(o2.getUser().getFirstName());
        return cmp;
      }
    });

    pageRequestContext.getRequest().setAttribute("courseStudents", courseStudents);
    pageRequestContext.getRequest().setAttribute("courseUsers", courseUsers);
    pageRequestContext.getRequest().setAttribute("courseComponents", courseDAO.listCourseComponents(course));
    
    pageRequestContext.setIncludeJSP("/templates/courses/viewcourse.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing courses is available for users with
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
    return Messages.getInstance().getText(locale, "courses.viewCourse.breadcrumb");
  }

}
