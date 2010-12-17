package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.pyramus.domainmodel.grading.TransferCreditTemplateCourse;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.CourseOptionality;

public class SaveTransferCreditTemplateJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    Long transferCreditTemplateId = jsonRequestContext.getLong("transferCreditTemplateId");
    String name = jsonRequestContext.getString("name");
    
    TransferCreditTemplate transferCreditTemplate;
    
    if (transferCreditTemplateId != null && transferCreditTemplateId >= 0) {
       transferCreditTemplate = gradingDAO.findTransferCreditTemplateById(transferCreditTemplateId);
       gradingDAO.updateTransferCreditTemplate(transferCreditTemplate, name);
    } else {
      transferCreditTemplate = gradingDAO.createTransferCreditTemplate(name);
    }
    
    int rowCount = jsonRequestContext.getInteger("coursesTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "coursesTable." + i;
      
      Long courseId = jsonRequestContext.getLong(colPrefix + ".courseId"); 
      String courseName = jsonRequestContext.getString(colPrefix + ".courseName"); 
      CourseOptionality courseOptionality = (CourseOptionality) jsonRequestContext.getEnum(colPrefix + ".courseOptionality", CourseOptionality.class);
      Integer courseNumber = jsonRequestContext.getInteger(colPrefix + ".courseNumber"); 
      Double courseLength = jsonRequestContext.getDouble(colPrefix + ".courseLength"); 
      Long subjectId = jsonRequestContext.getLong(colPrefix + ".subject"); 
      Long courseLengthUnitId = jsonRequestContext.getLong(colPrefix + ".courseLengthUnit"); 
      
      Subject subject = baseDAO.getSubject(subjectId);
      EducationalTimeUnit courseLengthUnit = baseDAO.findEducationalTimeUnitById(courseLengthUnitId);;
      
      TransferCreditTemplateCourse course;
      
      if (courseId != null && courseId > 0) {
        course = gradingDAO.findTransferCreditTemplateCourseById(courseId);
        gradingDAO.updateTransferCreditTemplateCourse(course, courseName, courseNumber, courseOptionality, courseLength, courseLengthUnit, subject);
      } else {
        course = gradingDAO.createTransferCreditTemplateCourse(transferCreditTemplate, courseName, courseNumber, courseOptionality, courseLength, courseLengthUnit, subject);
      }
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
