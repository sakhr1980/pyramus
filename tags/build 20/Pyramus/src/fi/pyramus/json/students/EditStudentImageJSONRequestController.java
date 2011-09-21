package fi.pyramus.json.students;

import org.apache.commons.fileupload.FileItem;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentImage;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of modifying an existing student group.
 * 
 * @see fi.pyramus.views.students.EditStudentGroupViewController
 */
public class EditStudentImageJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit a student group.
   * 
   * @param requestContext
   *          The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    String deleteImage = requestContext.getString("deleteImage");
    Long studentId = requestContext.getLong("studentId");
    FileItem studentImage = requestContext.getFile("studentImage");

    Student student = studentDAO.getStudent(studentId);
    StudentImage oldImage = studentDAO.findStudentImageByStudent(student);
    
    if (deleteImage == null) {
      if (studentImage != null) {
        String contentType = studentImage.getContentType();
        byte[] data = studentImage.get();
        
        if (oldImage != null) {
          studentDAO.updateStudentImage(oldImage, contentType, data);
        } else {
          studentDAO.createStudentImage(student, contentType, data);
        }
      }
    } else {
      studentDAO.deleteStudentImage(oldImage);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
