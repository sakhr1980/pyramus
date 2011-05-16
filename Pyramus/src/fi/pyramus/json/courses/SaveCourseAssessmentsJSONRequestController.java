package fi.pyramus.json.courses;

import java.util.Date;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of modifying an existing course. 
 * 
 * @see fi.pyramus.views.modules.EditCourseViewController
 */
public class SaveCourseAssessmentsJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit a course.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    int rowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long modified = requestContext.getLong(colPrefix + ".modified");
      if ((modified == null) || (modified.intValue() != 1))
        continue;
      
      Long courseStudentId = requestContext.getLong(colPrefix + ".courseStudentId");
      CourseStudent courseStudent = courseDAO.findCourseStudentById(courseStudentId);

      if (courseStudent != null) {
        Long assessingUserId = requestContext.getLong(colPrefix + ".assessingUserId");
        User assessingUser = userDAO.getUser(assessingUserId);
        Long gradeId = requestContext.getLong(colPrefix + ".gradeId");
        Grade grade = gradeId == null ? null : gradingDAO.findGradeById(gradeId);
        Date assessmentDate = requestContext.getDate(colPrefix + ".assessmentDate");

        Long participationTypeId = requestContext.getLong(colPrefix + ".participationType");
        CourseParticipationType participationType = courseDAO.getCourseParticipationType(participationTypeId);

        CourseAssessment assessment = gradingDAO.findCourseAssessmentByCourseStudent(courseStudent);

        if (assessment != null) {
          assessment = gradingDAO.updateCourseAssessment(assessment, assessingUser, grade, assessmentDate, assessment.getVerbalAssessment());
        } else {
          assessment = gradingDAO.createCourseAssessment(courseStudent, assessingUser, grade, assessmentDate, null);
        }

        // Update Participation type
        courseDAO.updateCourseStudent(courseStudent, courseStudent.getStudent(), 
            courseStudent.getCourseEnrolmentType(), participationType, courseStudent.getEnrolmentTime(), 
            courseStudent.getLodging(), courseStudent.getOptionality());
      } else
        throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.UNDEFINED, "CourseStudent was not defined");
    }
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
