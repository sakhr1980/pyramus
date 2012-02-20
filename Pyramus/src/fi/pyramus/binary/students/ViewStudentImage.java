package fi.pyramus.binary.students;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.pyramus.BinaryRequestController;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.students.StudentImageDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentImage;

public class ViewStudentImage extends BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudentImageDAO imageDAO = DAOFactory.getInstance().getStudentImageDAO();
    
    Long studentId = binaryRequestContext.getLong("studentId");
    
    Student student = studentDAO.findById(studentId);
    StudentImage studentImage = imageDAO.findByStudent(student);
    
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
