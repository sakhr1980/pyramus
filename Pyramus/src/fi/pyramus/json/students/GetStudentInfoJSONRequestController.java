package fi.pyramus.json.students;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller to view student info.
 * 
 * @author antti.viljakainen
 */
public class GetStudentInfoJSONRequestController implements JSONRequestController {
  
  /**
   * Processes JSON request
   * 
   * In parameters
   * - studentId - student id to retrieve information for
   * 
   * Page parameters
   * - student - Map including
   * * id - Student id
   * * firstname - First name
   * * lastname - Last name
   * 
   * @param requestContext JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long studentId = NumberUtils.createLong(requestContext.getRequest().getParameter("studentId"));
    Student student = studentDAO.getStudent(studentId);
    
    Map<String, Object> studentInfo = new HashMap<String, Object>();
    
    studentInfo.put("id", student.getId());
    studentInfo.put("firstname", student.getFirstName());
    studentInfo.put("lastname", student.getLastName());
    
    requestContext.addResponseParameter("student", studentInfo);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
