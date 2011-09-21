package fi.pyramus.views.students;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Student Image Dialog view of the application.
 */
public class EditStudentImageDialogViewController implements PyramusViewController {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long studentId = pageRequestContext.getLong("studentId");
    
    Student student = studentDAO.getStudent(studentId);
    Boolean hasImage = studentDAO.findStudentHasImage(student);
    
    pageRequestContext.getRequest().setAttribute("student", student);
    pageRequestContext.getRequest().setAttribute("studentHasImage", hasImage);
    
    pageRequestContext.setIncludeJSP("/templates/students/editstudentimagedialog.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing student groups is available for users with
   * {@link Role#MANAGER} or {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
