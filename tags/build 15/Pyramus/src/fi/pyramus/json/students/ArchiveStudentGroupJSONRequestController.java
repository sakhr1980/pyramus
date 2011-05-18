package fi.pyramus.json.students;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

public class ArchiveStudentGroupJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long studentGroupId = NumberUtils.createLong(requestContext.getRequest().getParameter("studentGroupId"));
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());

    StudentGroup studentGroup = studentDAO.findStudentGroupById(studentGroupId);    
    studentDAO.archiveStudentGroup(studentGroup, loggedUser);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
