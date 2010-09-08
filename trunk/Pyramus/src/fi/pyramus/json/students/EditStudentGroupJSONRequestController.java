package fi.pyramus.json.students;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.domainmodel.students.StudentGroupStudent;
import fi.pyramus.domainmodel.students.StudentGroupUser;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of modifying an existing student group.
 * 
 * @see fi.pyramus.views.students.EditStudentGroupViewController
 */
public class EditStudentGroupJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit a student group.
   * 
   * @param requestContext
   *          The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    // StudentGroup basic information

    String name = requestContext.getString("name");
    String description = requestContext.getString("description");
    Date beginDate = requestContext.getDate("beginDate");

    StudentGroup studentGroup = studentDAO.findStudentGroupById(requestContext.getLong("studentGroupId"));
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());

    studentDAO.updateStudentGroup(studentGroup, name, description, beginDate, loggedUser);

    // Personnel

    StudentGroupUser[] users = studentGroup.getUsers().toArray(new StudentGroupUser[0]);
    StudentGroupStudent[] students = studentGroup.getStudents().toArray(new StudentGroupStudent[0]);

    List<Long> removables = new ArrayList<Long>();
    Iterator<StudentGroupUser> userIterator = studentGroup.getUsers().iterator();
    while (userIterator.hasNext())
      removables.add(userIterator.next().getId());
    
    int rowCount = requestContext.getInteger("usersTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "usersTable." + i;
      Long userId = requestContext.getLong(colPrefix + ".userId");
      Long studentGroupUserId = requestContext.getLong(colPrefix + ".studentGroupUserId");
      
      if (studentGroupUserId == null) {
        // New User
        User user = userDAO.getUser(userId);
        studentDAO.addStudentGroupUser(studentGroup, user, loggedUser);
      } else {
        // Old User, still in list
        removables.remove(studentGroupUserId);
      }
    }

    // Remove the ones that were deleted from group
    for (int i = 0; i < users.length; i++) {
      if (removables.contains(users[i].getId()))
        studentDAO.removeStudentGroupUser(studentGroup, users[i], loggedUser);
    }

    // Students

    removables.clear();
    Iterator<StudentGroupStudent> studentIterator = studentGroup.getStudents().iterator();
    while (studentIterator.hasNext())
      removables.add(studentIterator.next().getId());

    rowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long studentId = requestContext.getLong(colPrefix + ".studentId");
      Long studentGroupStudentId = requestContext.getLong(colPrefix + ".studentGroupStudentId");
      
      if (studentGroupStudentId == null) {
        // New Student
        Student student = studentDAO.getStudent(studentId);
        studentDAO.addStudentGroupStudent(studentGroup, student, loggedUser);
      } else {
        // Old User, still in list, we'll update if the student has changed student group
        removables.remove(studentGroupStudentId);
        
        StudentGroupStudent sgStudent = studentDAO.findStudentGroupStudentById(studentGroupStudentId);
        if (!sgStudent.getStudent().getId().equals(studentId))
          studentDAO.updateStudentGroupStudent(sgStudent, studentDAO.getStudent(studentId), loggedUser);
      }
    }

    // Remove the ones that were deleted from group
    for (int i = 0; i < students.length; i++) {
      if (removables.contains(students[i].getId()))
        studentDAO.removeStudentGroupStudent(studentGroup, students[i], loggedUser);
    }
    
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
