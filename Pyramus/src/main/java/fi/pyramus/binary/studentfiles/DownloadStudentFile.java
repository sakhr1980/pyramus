package fi.pyramus.binary.studentfiles;

import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.file.StudentFileDAO;
import fi.pyramus.domainmodel.file.StudentFile;
import fi.pyramus.framework.BinaryRequestController;
import fi.pyramus.framework.UserRole;

public class DownloadStudentFile extends BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentFileDAO studentFileDAO = DAOFactory.getInstance().getStudentFileDAO();
    
    Long fileId = binaryRequestContext.getLong("fileId");
    
    StudentFile studentFile = studentFileDAO.findById(fileId);
    
    if (studentFile != null) {
      binaryRequestContext.setFileName(studentFile.getFileName());
      binaryRequestContext.setResponseContent(studentFile.getData(), studentFile.getContentType());
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
}
