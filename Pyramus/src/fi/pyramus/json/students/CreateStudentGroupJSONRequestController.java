package fi.pyramus.json.students;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

public class CreateStudentGroupJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();    

    // StudentGroup basic information

    String name = requestContext.getString("name");
    String description = requestContext.getString("description");
    Date beginDate = requestContext.getDate("beginDate");
    String tagsText = requestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        Tag tagEntity = baseDAO.findTagByText(tag.trim());
        if (tagEntity == null)
          tagEntity = baseDAO.createTag(tag);
        tagEntities.add(tagEntity);
      }
    }

    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());

    StudentGroup studentGroup = studentDAO.createStudentGroup(name, description, beginDate, loggedUser);

    // Tags
    
    studentDAO.setStudentGroupTags(studentGroup, tagEntities);
    
    // Personnel

    int rowCount = requestContext.getInteger("usersTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "usersTable." + i;
      Long userId = requestContext.getLong(colPrefix + ".userId");
      User user = userDAO.getUser(userId);
      
      studentDAO.addStudentGroupUser(studentGroup, user, loggedUser);
    }

    // Students

    int studentsTableRowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < studentsTableRowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long studentId = requestContext.getLong(colPrefix + ".studentId");
      Student student = studentDAO.getStudent(studentId);
      
      studentDAO.addStudentGroupStudent(studentGroup, student, loggedUser);
    }
    
    String redirectURL = requestContext.getRequest().getContextPath() + "/students/editstudentgroup.page?studentgroup=" + studentGroup.getId();
    String refererAnchor = requestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    requestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}