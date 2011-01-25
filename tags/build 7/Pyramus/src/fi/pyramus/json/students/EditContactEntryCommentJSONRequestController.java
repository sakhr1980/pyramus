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
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller for editing a contact entry.
 * 
 * @author antti.viljakainen
 */
public class EditContactEntryCommentJSONRequestController implements JSONRequestController {

  /**
   * Method to process JSON requests.
   * 
   * In parameters
   * - commentId - Id to identify the comment being modified
   * - commentText - Textual message or description about the contact
   * - commentCreatorName - Name of the person who made the contact
   * - commentDate - Date of the entry
   * 
   * Page parameters
   * - results Map including
   * * id - Comment id
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
      Long commentId = jsonRequestContext.getLong("commentId");
      
      StudentContactLogEntryComment comment = studentDAO.findStudentContactLogEntryCommentById(commentId);
      
      String commentText = jsonRequestContext.getRequest().getParameter("commentText");
      String commentCreatorName = jsonRequestContext.getRequest().getParameter("commentCreatorName");
      Date commentDate = new Date(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("commentDate")));
      
      studentDAO.updateStudentContactLogEntryComment(comment, commentText, commentDate, commentCreatorName);

      Map<String, Object> info = new HashMap<String, Object>();
      info.put("id", comment.getId());
      info.put("creatorName", comment.getCreatorName());
      info.put("timestamp", comment.getCommentDate().getTime());
      info.put("text", comment.getText());
      info.put("entryId", comment.getEntry().getId());

      jsonRequestContext.addResponseParameter("results", info);
    } catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
