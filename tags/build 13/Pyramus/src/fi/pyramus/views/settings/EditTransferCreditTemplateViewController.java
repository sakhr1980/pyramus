package fi.pyramus.views.settings;

import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Manage Transfer Credit Template view of the application.
 */
public class EditTransferCreditTemplateViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    Long transferCreditTemplateId = pageRequestContext.getLong("transferCreditTemplate");

    TransferCreditTemplate transferCreditTemplate = gradingDAO.findTransferCreditTemplateById(transferCreditTemplateId);
    List<Subject> subjects = baseDAO.listSubjects();
    List<EducationalTimeUnit> timeUnits = baseDAO.listEducationalTimeUnits();
    List<School> schools = baseDAO.listSchools();

    pageRequestContext.getRequest().setAttribute("transferCreditTemplate", transferCreditTemplate);
    pageRequestContext.getRequest().setAttribute("subjects", subjects);
    pageRequestContext.getRequest().setAttribute("timeUnits", timeUnits);
    pageRequestContext.getRequest().setAttribute("schools", schools);
    
    pageRequestContext.setIncludeJSP("/templates/settings/edittransfercredittemplate.jsp");
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
    return Messages.getInstance().getText(locale, "settings.editTransferCreditTemplate.pageTitle");
  }

}
