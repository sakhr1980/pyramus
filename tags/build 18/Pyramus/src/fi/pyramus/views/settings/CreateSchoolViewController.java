package fi.pyramus.views.settings;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.ContactURLType;
import fi.pyramus.domainmodel.base.SchoolVariableKey;
import fi.pyramus.UserRole;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Create School view of the application.
 * 
 * @see fi.pyramus.json.settings.CreateSchoolJSONRequestController
 */
public class CreateSchoolViewController implements PyramusViewController, Breadcrumbable {
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    List<ContactURLType> contactURLTypes = baseDAO.listContactURLTypes();
    Collections.sort(contactURLTypes, new StringAttributeComparator("getName"));

    List<SchoolVariableKey> schoolUserEditableVariableKeys = baseDAO.listSchoolUserEditableVariableKeys();
    Collections.sort(schoolUserEditableVariableKeys, new StringAttributeComparator("getVariableName"));

    List<ContactType> contactTypes = baseDAO.listContactTypes();
    Collections.sort(contactTypes, new StringAttributeComparator("getName"));

    pageRequestContext.getRequest().setAttribute("contactTypes", contactTypes);
    pageRequestContext.getRequest().setAttribute("contactURLTypes", contactURLTypes);
    pageRequestContext.getRequest().setAttribute("variableKeys", schoolUserEditableVariableKeys);
    pageRequestContext.getRequest().setAttribute("schoolFields", baseDAO.listSchoolFields());
    pageRequestContext.setIncludeJSP("/templates/settings/createschool.jsp");
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
    return Messages.getInstance().getText(locale, "settings.createSchool.pageTitle");
  }

}
