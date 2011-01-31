package fi.pyramus.json.students;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class UnarchiveStudentJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Long studentId = requestContext.getLong("student");
    Student student = studentDAO.getStudent(studentId);
    if (student != null) {
      studentDAO.unarchiveStudent(student);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
