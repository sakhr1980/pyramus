package fi.pyramus.views.students;

import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.views.PyramusViewController;

/**
 * ViewController for viewing student information within popup dialog.
 * 
 * @author antti.leppa
 */
public class StudentInfoPopupViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Returns allowed roles for this page. Everyone is allowed to use this view.
   * 
   * @return allowed roles
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

  /**
   * Processes the page request.
   * 
   * In parameters
   * - student
   * 
   * Page parameters
   * - student - Student object
   * - studentImage - url to student's image
   * - studentNationality - students nationality
   * - studentLanguage - students native language
   * - studentPhoneNumber - student phone number
   * 
   * @param pageRequestContext pageRequestContext
   */
  public void process(PageRequestContext pageRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    Long studentId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("student"));
    Long abstractStudentId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("abstractStudent"));
    
    AbstractStudent abstractStudent;

    if (abstractStudentId != null) {
    	abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    } else {
      Student student = studentDAO.getStudent(studentId);
      abstractStudent = student.getAbstractStudent();
    }
  
    String studentImage = pageRequestContext.getRequest().getContextPath() + "/gfx/default-user-image.png";
    // TODO Actual image once we have it :)
    
		pageRequestContext.getRequest().setAttribute("abstractStudent", abstractStudent);
    pageRequestContext.getRequest().setAttribute("studentImage", studentImage);
  
    pageRequestContext.setIncludeJSP("/templates/students/studentinfopopup.jsp");
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * This view does not need a name because it's used as a content to popup dialog
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return "";
  }

}

