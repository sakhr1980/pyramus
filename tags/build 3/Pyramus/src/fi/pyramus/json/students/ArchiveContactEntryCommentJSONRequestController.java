package fi.pyramus.json.students;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.json.JSONRequestController;

public class ArchiveContactEntryCommentJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Long commentId = requestContext.getLong("commentId");
    
    StudentContactLogEntryComment comment = studentDAO.findStudentContactLogEntryCommentById(commentId);
    
    studentDAO.archiveContactEntryComment(comment);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
