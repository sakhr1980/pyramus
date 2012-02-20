package fi.pyramus.json.courses;

import java.util.Date;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.pyramus.JSONRequestController;
import fi.pyramus.PyramusStatusCode;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.courses.CourseParticipationTypeDAO;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.dao.grading.CourseAssessmentDAO;
import fi.pyramus.dao.grading.GradeDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.users.User;

/**
 * The controller responsible of modifying an existing course. 
 * 
 * @see fi.pyramus.views.modules.EditCourseViewController
 */
public class SaveCourseAssessmentsJSONRequestController extends JSONRequestController {

  /**
   * Processes the request to edit a course.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    CourseParticipationTypeDAO participationTypeDAO = DAOFactory.getInstance().getCourseParticipationTypeDAO();
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    CourseAssessmentDAO courseAssessmentDAO = DAOFactory.getInstance().getCourseAssessmentDAO();

    int rowCount = requestContext.getInteger("studentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studentsTable." + i;

      Long modified = requestContext.getLong(colPrefix + ".modified");
      if ((modified == null) || (modified.intValue() != 1))
        continue;
      
      Long courseStudentId = requestContext.getLong(colPrefix + ".courseStudentId");
      CourseStudent courseStudent = courseStudentDAO.findById(courseStudentId);

      if (courseStudent != null) {
        Long assessingUserId = requestContext.getLong(colPrefix + ".assessingUserId");
        User assessingUser = userDAO.findById(assessingUserId);
        Long gradeId = requestContext.getLong(colPrefix + ".gradeId");
        Grade grade = gradeId == null ? null : gradeDAO.findById(gradeId);
        Date assessmentDate = requestContext.getDate(colPrefix + ".assessmentDate");

        Long participationTypeId = requestContext.getLong(colPrefix + ".participationType");
        CourseParticipationType participationType = participationTypeDAO.findById(participationTypeId);
        String verbalAssessment = null;

        CourseAssessment assessment = courseAssessmentDAO.findByCourseStudent(courseStudent);

        Long verbalModified = requestContext.getLong(colPrefix + ".verbalModified");
        if ((verbalModified != null) && (verbalModified.intValue() == 1)) {
          verbalAssessment = requestContext.getString(colPrefix + ".verbalAssessment");
        } else {
          if (assessment != null)
            verbalAssessment = assessment.getVerbalAssessment();
        }

        if (assessment != null) {
          assessment = courseAssessmentDAO.update(assessment, assessingUser, grade, assessmentDate, verbalAssessment);
        } else {
          assessment = courseAssessmentDAO.create(courseStudent, assessingUser, grade, assessmentDate, verbalAssessment);
        }

        // Update Participation type
        courseStudentDAO.update(courseStudent, courseStudent.getStudent(), 
            courseStudent.getCourseEnrolmentType(), participationType, courseStudent.getEnrolmentTime(), 
            courseStudent.getLodging(), courseStudent.getOptionality());
      } else
        throw new SmvcRuntimeException(PyramusStatusCode.UNDEFINED, "CourseStudent was not defined");
    }
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
