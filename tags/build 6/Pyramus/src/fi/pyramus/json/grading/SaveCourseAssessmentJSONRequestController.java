package fi.pyramus.json.grading;

import java.util.Date;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

public class SaveCourseAssessmentJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long courseStudentId = jsonRequestContext.getLong("courseStudentId");
    Date assessmentDate = jsonRequestContext.getDate("assessmentDate");
    Long assessingUserId = jsonRequestContext.getLong("assessingUserId");
    Long gradeId = jsonRequestContext.getLong("gradeId");
    String verbalAssessment = jsonRequestContext.getString("verbalAssessment");
    
    CourseStudent courseStudent = courseDAO.findCourseStudentById(courseStudentId);
    User assessingUser = userDAO.getUser(assessingUserId);
    Grade grade = gradingDAO.findGradeById(gradeId);

    CourseAssessment assessment = gradingDAO.findCourseAssessmentByCourseStudent(courseStudent);
    
    if (assessment == null) {
      assessment = gradingDAO.createCourseAssessment(courseStudent, 
          assessingUser, grade, assessmentDate, verbalAssessment);
    } else {
      assessment = gradingDAO.updateCourseAssessment(assessment, 
          assessingUser, grade, assessmentDate, verbalAssessment);
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
