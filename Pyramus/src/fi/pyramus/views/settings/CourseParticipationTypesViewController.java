package fi.pyramus.views.settings;

import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Manage Course Participation Types view of the application.
 * 
 * @see fi.pyramus.json.settings.SaveTimeUnitsJSONRequestController
 */
public class CourseParticipationTypesViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    
    CourseParticipationType initialCourseParticipationType = baseDAO.getDefaults().getInitialCourseParticipationType();
    List<CourseParticipationType> courseParticipationTypes = courseDAO.listCourseParticipationTypes();
    
    pageRequestContext.getRequest().setAttribute("courseParticipationTypes", courseParticipationTypes);
    pageRequestContext.getRequest().setAttribute("initialCourseParticipationType", initialCourseParticipationType);
    
    pageRequestContext.setIncludeJSP("/templates/settings/courseparticipationtypes.jsp");
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
    return Messages.getInstance().getText(locale, "settings.courseParticipationTypes.pageTitle");
  }

}
