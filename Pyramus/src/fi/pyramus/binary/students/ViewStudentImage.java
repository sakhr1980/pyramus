package fi.pyramus.binary.students;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentImage;

public class ViewStudentImage implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    Long studentId = binaryRequestContext.getLong("studentId");
    
    Student student = studentDAO.getStudent(studentId);
    StudentImage studentImage = studentDAO.findStudentImageByStudent(student);
    
    if (studentImage != null) {
      binaryRequestContext.getResponse().setContentType(studentImage.getContentType());
  
      try {
        ServletOutputStream outputStream = binaryRequestContext.getResponse().getOutputStream();
        
        outputStream.write(studentImage.getData());
        
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
}
