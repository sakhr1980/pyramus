package fi.pyramus.views.students;

import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class EditStudentViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long abstractStudentId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("abstractStudent"));
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    
    pageRequestContext.getRequest().setAttribute("abstractStudent", abstractStudent);
    pageRequestContext.getRequest().setAttribute("activityTypes", studentDAO.listStudentActivityTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", baseDAO.listContactURLTypes());
    pageRequestContext.getRequest().setAttribute("contactTypes", baseDAO.listContactTypes());
    pageRequestContext.getRequest().setAttribute("examinationTypes", studentDAO.listStudentExaminationTypes());
    pageRequestContext.getRequest().setAttribute("educationalLevels", studentDAO.listStudentEducationalLevels());
    pageRequestContext.getRequest().setAttribute("nationalities", baseDAO.listNationalities());
    pageRequestContext.getRequest().setAttribute("municipalities", baseDAO.listMunicipalities());
    pageRequestContext.getRequest().setAttribute("languages", baseDAO.listLanguages());
    pageRequestContext.getRequest().setAttribute("schools", baseDAO.listSchools());
    pageRequestContext.getRequest().setAttribute("studyProgrammes", baseDAO.listStudyProgrammes());
    pageRequestContext.getRequest().setAttribute("studyEndReasons", studentDAO.listTopLevelStudentStudyEndReasons());
    pageRequestContext.getRequest().setAttribute("variableKeys", studentDAO.listUserEditableStudentVariableKeys());
    
    pageRequestContext.setIncludeJSP("/templates/students/editstudent.jsp");
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
    return Messages.getInstance().getText(locale, "students.editStudent.pageTitle");
  }

}
