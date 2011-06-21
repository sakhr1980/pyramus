package fi.pyramus.json.grading;

import java.util.Date;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.CourseOptionality;

public class SaveTransferCreditsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long studentId = jsonRequestContext.getLong("studentId");
    Student student = studentDAO.getStudent(studentId);

    int rowCount = jsonRequestContext.getInteger("transferCreditsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "transferCreditsTable." + i;
      
      Long id = jsonRequestContext.getLong(colPrefix + ".creditId");
      String courseName = jsonRequestContext.getString(colPrefix + ".courseName");
      CourseOptionality courseOptionality = (CourseOptionality) jsonRequestContext.getEnum(colPrefix + ".courseOptionality", CourseOptionality.class);
      Integer courseNumber = jsonRequestContext.getInteger(colPrefix + ".courseNumber");
      Long gradeId = jsonRequestContext.getLong(colPrefix + ".grade");
      Long subjectId = jsonRequestContext.getLong(colPrefix + ".subject"); 
      Double courseLength = jsonRequestContext.getDouble(colPrefix + ".courseLength");
      Long courseLengthUnitId = jsonRequestContext.getLong(colPrefix + ".courseLengthUnit");
      Long schooId = jsonRequestContext.getLong(colPrefix + ".school");
      Date date = jsonRequestContext.getDate(colPrefix + ".date");
      Long userId = jsonRequestContext.getLong(colPrefix + ".user");
      
      Grade grade = gradingDAO.findGradeById(gradeId);
      Subject subject = baseDAO.getSubject(subjectId);
      EducationalTimeUnit timeUnit = baseDAO.findEducationalTimeUnitById(courseLengthUnitId);
      School school = baseDAO.getSchool(schooId);
      User user = userDAO.getUser(userId);

      TransferCredit transferCredit;
      
      if (id != null && id >= 0) {
        transferCredit = gradingDAO.findTransferCreditById(id);
        gradingDAO.updateTransferCredit(transferCredit, courseName, courseNumber, courseLength, timeUnit, school, subject, courseOptionality, student, user, grade, date, transferCredit.getVerbalAssessment());
      } else {
        transferCredit = gradingDAO.createTransferCredit(courseName, courseNumber, courseLength, timeUnit, school, subject, courseOptionality, student, user, grade, date, ""); 
      }
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
