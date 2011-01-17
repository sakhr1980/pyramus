package fi.pyramus.json.students;

import java.util.HashMap;
import java.util.Map;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller for reading a contact entry comment.
 * 
 * @author antti.viljakainen
 */
public class GetContactEntryCommentJSONRequestController implements JSONRequestController {

  /**
   * Method to process JSON requests.
   * 
   * In parameters
   * - commentId - Long parameter to identify the contact entry comment that is being read
   * 
   * Page parameters
   * - results Map including
   * * id - Comment id
   * * entryId - Entry id
   * * creatorName - Comment creator name
   * * timestamp - Comment date
   * * text - Comment message
   * 
   * @param jsonRequestContext JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    try {
      Long commentId = jsonRequestContext.getLong("commentId");
      
      StudentContactLogEntryComment comment = studentDAO.findStudentContactLogEntryCommentById(commentId);
      
      Map<String, Object> info = new HashMap<String, Object>();
      info.put("id", comment.getId());
      info.put("entryId", comment.getEntry().getId());
      info.put("creatorName", comment.getCreatorName());
      info.put("timestamp", comment.getCommentDate().getTime());
      info.put("text", comment.getText());
      
      jsonRequestContext.addResponseParameter("results", info);
    } catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
