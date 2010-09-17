package fi.pyramus.views.students;

import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

/**
 * ViewController to search for students.
 * 
 * @author antti.viljakainen
 */
public class SearchStudentsViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Returns roles that are allowed to use this resource.
   *  
   * @see fi.pyramus.views.PyramusViewController#getAllowedRoles()
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Processes the page request.
   * 
   * @param pageRequestContext Request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    pageRequestContext.getRequest().setAttribute("nationalities", baseDAO.listNationalities());
    pageRequestContext.getRequest().setAttribute("municipalities", baseDAO.listMunicipalities());
    pageRequestContext.getRequest().setAttribute("languages", baseDAO.listLanguages());
    pageRequestContext.getRequest().setAttribute("studyProgrammes", baseDAO.listStudyProgrammes());

    pageRequestContext.setIncludeJSP("/templates/students/searchstudents.jsp");
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "students.searchStudents.pageTitle");
  }

}
