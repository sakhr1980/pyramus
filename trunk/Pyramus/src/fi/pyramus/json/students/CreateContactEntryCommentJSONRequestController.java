package fi.pyramus.json.students;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller for creating new contact entry.
 * 
 * @author antti.viljakainen
 */
public class CreateContactEntryCommentJSONRequestController implements JSONRequestController {

  /**
   * Method to process JSON requests.
   * 
   * In parameters
   * - entryId - Id to identify the entry where the comment is being bind into
   * - commentText - Textual message or description about the contact
   * - commentCreatorName - Name of the person who made the contact
   * - commentDate - Date of the entry
   * 
   * Page parameters
   * - results Map including
   * * id - New comment id
   * * entryId - Entry id
   * * creatorName - Comment creator
   * * timestamp - Comment date
   * * text - Comment message
   * 
   * @param jsonRequestContext JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    try {
      Long entryId = jsonRequestContext.getLong("entryId");
      StudentContactLogEntry entry = studentDAO.findStudentContactLogEntryById(entryId);

      String commentText = jsonRequestContext.getRequest().getParameter("commentText");
      String commentCreatorName = jsonRequestContext.getRequest().getParameter("commentCreatorName");
      Date commentDate = new Date(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("commentDate"))); 
      
      StudentContactLogEntryComment comment = studentDAO.createStudentContactLogEntryComment(entry, commentText, commentDate, commentCreatorName);

      Map<String, Object> info = new HashMap<String, Object>();
      info.put("id", comment.getId());
      info.put("creatorName", comment.getCreatorName());
      info.put("timestamp", comment.getCommentDate().getTime());
      info.put("text", comment.getText());
      info.put("entryId", entryId);

      jsonRequestContext.addResponseParameter("results", info);
    } catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
