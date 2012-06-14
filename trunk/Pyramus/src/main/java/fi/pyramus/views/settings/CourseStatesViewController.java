package fi.pyramus.views.settings;

import java.util.List;
import java.util.Locale;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.DefaultsDAO;
import fi.pyramus.dao.courses.CourseStateDAO;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;

/**
 * The controller responsible of the Manage Time Units view of the application.
 * 
 * @see fi.pyramus.json.settings.SaveTimeUnitsJSONRequestController
 */
public class CourseStatesViewController extends PyramusViewController implements Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    CourseStateDAO courseStateDAO = DAOFactory.getInstance().getCourseStateDAO();
    DefaultsDAO defaultsDAO = DAOFactory.getInstance().getDefaultsDAO();

    CourseState initalCourseState = defaultsDAO.getDefaults().getInitialCourseState();
    List<CourseState> courseStates = courseStateDAO.listUnarchived();
    
    pageRequestContext.getRequest().setAttribute("courseStates", courseStates);
    pageRequestContext.getRequest().setAttribute("initalCourseState", initalCourseState);
    
    pageRequestContext.setIncludeJSP("/templates/settings/coursestates.jsp");
  }

  /**
   * Returns the roles allowed to access this page.
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
    return Messages.getInstance().getText(locale, "settings.courseStates.pageTitle");
  }

}
