package fi.pyramus.views.students;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.ContactURLType;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.UserRole;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

public class CreateStudentViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    List<StudyProgramme> studyProgrammes = baseDAO.listStudyProgrammes();
    Collections.sort(studyProgrammes, new StringAttributeComparator("getName"));

    List<Nationality> nationalities = baseDAO.listNationalities();
    Collections.sort(nationalities, new StringAttributeComparator("getName"));
    
    List<Municipality> municipalities = baseDAO.listMunicipalities();
    Collections.sort(municipalities, new StringAttributeComparator("getName"));
    
    List<Language> languages = baseDAO.listLanguages();
    Collections.sort(languages, new StringAttributeComparator("getName"));

    List<School> schools = baseDAO.listSchools();
    Collections.sort(schools, new StringAttributeComparator("getName"));

    List<ContactURLType> contactURLTypes = baseDAO.listContactURLTypes();
    Collections.sort(contactURLTypes, new StringAttributeComparator("getName"));
    
    List<ContactType> contactTypes = baseDAO.listContactTypes();
    Collections.sort(contactTypes, new StringAttributeComparator("getName"));

    pageRequestContext.getRequest().setAttribute("schools", schools);
    pageRequestContext.getRequest().setAttribute("activityTypes", studentDAO.listStudentActivityTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", contactURLTypes);
    pageRequestContext.getRequest().setAttribute("contactTypes", contactTypes);
    pageRequestContext.getRequest().setAttribute("examinationTypes", studentDAO.listStudentExaminationTypes());
    pageRequestContext.getRequest().setAttribute("educationalLevels", studentDAO.listStudentEducationalLevels());
    pageRequestContext.getRequest().setAttribute("nationalities", nationalities);
    pageRequestContext.getRequest().setAttribute("municipalities", municipalities);
    pageRequestContext.getRequest().setAttribute("languages", languages);
    pageRequestContext.getRequest().setAttribute("studyProgrammes", studyProgrammes);
    pageRequestContext.getRequest().setAttribute("variableKeys", studentDAO.listUserEditableStudentVariableKeys());
    pageRequestContext.getRequest().setAttribute("studyEndReasons", studentDAO.listTopLevelStudentStudyEndReasons());
    
    pageRequestContext.setIncludeJSP("/templates/students/createstudent.jsp");
  }

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
    return Messages.getInstance().getText(locale, "students.createStudent.pageTitle");
  }

}
