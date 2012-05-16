package fi.pyramus.binary.studentfiles;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.file.StudentFileDAO;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.students.StudentImageDAO;
import fi.pyramus.domainmodel.file.StudentFile;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentImage;
import fi.pyramus.framework.BinaryRequestController;
import fi.pyramus.framework.UserRole;

public class DownloadStudentFile extends BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentFileDAO studentFileDAO = DAOFactory.getInstance().getStudentFileDAO();
    
    Long fileId = binaryRequestContext.getLong("fileId");
    
    StudentFile studentFile = studentFileDAO.findById(fileId);
    
    if (studentFile != null) {
      binaryRequestContext.getResponse().setHeader("Content-Disposition", "inline; filename=\"" + studentFile.getFileName() + "\"");
      binaryRequestContext.getResponse().setContentType(studentFile.getContentType());
  
      try {
        ServletOutputStream outputStream = binaryRequestContext.getResponse().getOutputStream();
        
        outputStream.write(studentFile.getData());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
}
