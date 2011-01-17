package fi.pyramus.json.grading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.pyramus.domainmodel.grading.TransferCreditTemplateCourse;
import fi.pyramus.json.JSONRequestController;

public class LoadTransferCreditTemplateJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    
    Long transferCreditTemplateId = jsonRequestContext.getLong("transferCreditTemplateId");
    TransferCreditTemplate transferCreditTemplate = gradingDAO.findTransferCreditTemplateById(transferCreditTemplateId);
    
    List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
    
    for (TransferCreditTemplateCourse templateCourse : transferCreditTemplate.getCourses()) {
      Map<String, Object> result = new HashMap<String, Object>();
      
      result.put("courseId", templateCourse.getId());
      result.put("courseUnits", templateCourse.getCourseLength().getUnits());
      result.put("courseUnit", templateCourse.getCourseLength().getUnit().getId());
      result.put("courseName", templateCourse.getCourseName());
      result.put("courseNumber", templateCourse.getCourseNumber());
      result.put("courseOptionality", templateCourse.getOptionality().name());
      result.put("subjectId", templateCourse.getSubject().getId());
      result.put("subjectName", templateCourse.getSubject().getName());

      results.add(result);
    }
    
    jsonRequestContext.addResponseParameter("results", results);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
