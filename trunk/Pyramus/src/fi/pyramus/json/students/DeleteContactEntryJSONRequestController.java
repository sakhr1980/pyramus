package fi.pyramus.json.students;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.json.JSONRequestController;

public class DeleteContactEntryJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Long entryId = requestContext.getLong("entryId");
    
    StudentContactLogEntry entry = studentDAO.findStudentContactLogEntryById(entryId);
    
    studentDAO.deleteContactEntry(entry);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
