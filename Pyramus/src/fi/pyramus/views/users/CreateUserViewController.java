package fi.pyramus.views.users;

import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Create User view of the application.
 * 
 * @see fi.pyramus.json.users.CreateUserJSONRequestController
 */
public class CreateUserViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response. 
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    // TODO user login types, usernames, passwords
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    pageRequestContext.getRequest().setAttribute("contactTypes", baseDAO.listContactTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", baseDAO.listContactURLTypes());
    pageRequestContext.setIncludeJSP("/templates/users/createuser.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Creating new users requires
   * {@link Role#ADMINISTRATOR} privileges.
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
    return Messages.getInstance().getText(locale, "users.createUser.pageTitle");
  }

}
