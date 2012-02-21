package fi.pyramus.views.settings;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.SchoolVariableKeyDAO;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.SchoolVariableKey;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;
import fi.pyramus.util.StringAttributeComparator;

/**
 * The controller responsible of the Edit School view of the application.
 * 
 * @see fi.pyramus.json.settings.EditSchoolJSONRequestController
 */
public class ViewSchoolViewController extends PyramusViewController implements Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    SchoolVariableKeyDAO schoolVariableKeyDAO = DAOFactory.getInstance().getSchoolVariableKeyDAO();

    Long schoolId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("school"));
    School school = schoolDAO.findById(schoolId);

    List<SchoolVariableKey> schoolUserEditableVariableKeys = schoolVariableKeyDAO.listUserEditableVariableKeys();
    Collections.sort(schoolUserEditableVariableKeys, new StringAttributeComparator("getVariableName"));

    pageRequestContext.getRequest().setAttribute("school", school);
    pageRequestContext.getRequest().setAttribute("variableKeys", schoolUserEditableVariableKeys);

    pageRequestContext.setIncludeJSP("/templates/settings/viewschool.jsp");
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
    return Messages.getInstance().getText(locale, "settings.viewSchool.pageTitle");
  }

}
