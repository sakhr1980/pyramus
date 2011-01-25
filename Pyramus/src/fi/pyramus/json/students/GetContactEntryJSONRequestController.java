package fi.pyramus.json.students;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller for reading a contact entry.
 * 
 * @author antti.viljakainen
 */
public class GetContactEntryJSONRequestController implements JSONRequestController {

  /**
   * Method to process JSON requests.
   * 
   * In parameters
   * - entryId - Long parameter to identify the contact entry that is being modified
   * 
   * Page parameters
   * - results Map including
   * * id - Entry id
   * * creatorName - Entry creator name
   * * date - Entry date
   * * text - Entry message
   * * type - Entry type
   * 
   * @param jsonRequestContext JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    try {
      Long entryId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("entryId"));
      
      StudentContactLogEntry entry = studentDAO.findStudentContactLogEntryById(entryId);
      
      Map<String, Object> info = new HashMap<String, Object>();
      info.put("id", entry.getId());
      info.put("creatorName", entry.getCreatorName());
      info.put("timestamp", entry.getEntryDate().getTime());
      info.put("text", entry.getText());
      info.put("type", entry.getType());
      info.put("studentId", entry.getStudent().getId());
      
      jsonRequestContext.addResponseParameter("results", info);
    } catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
