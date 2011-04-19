package fi.pyramus.views.courses;

import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SearchCoursesViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    pageRequestContext.getRequest().setAttribute("states", courseDAO.listCourseStates());
    pageRequestContext.getRequest().setAttribute("subjects", baseDAO.listSubjects());
    pageRequestContext.setIncludeJSP("/templates/courses/searchcourses.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "courses.searchCourses.pageTitle");
  }
  
}
