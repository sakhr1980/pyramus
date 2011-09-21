package fi.pyramus.views.students;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.util.StringAttributeComparator;
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

    List<StudyProgramme> studyProgrammes = baseDAO.listStudyProgrammes();
    Collections.sort(studyProgrammes, new StringAttributeComparator("getName"));
    
    List<Nationality> nationalities = baseDAO.listNationalities();
    Collections.sort(nationalities, new StringAttributeComparator("getName"));
    
    List<Municipality> municipalities = baseDAO.listMunicipalities();
    Collections.sort(municipalities, new StringAttributeComparator("getName"));

    List<Language> languages = baseDAO.listLanguages();
    Collections.sort(languages, new StringAttributeComparator("getName"));
    
    pageRequestContext.getRequest().setAttribute("nationalities", nationalities);
    pageRequestContext.getRequest().setAttribute("municipalities", municipalities);
    pageRequestContext.getRequest().setAttribute("languages", languages);
    pageRequestContext.getRequest().setAttribute("studyProgrammes", studyProgrammes);

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
