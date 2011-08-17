package fi.pyramus.json.settings;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of creating a new grading scale. 
 * 
 * @see fi.pyramus.views.settings.CreateGradingScaleViewController
 */
public class CreateGradingScaleJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to create a new grading scale.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    String description = jsonRequestContext.getRequest().getParameter("description");
    GradingScale gradingScale = gradingDAO.createGradingScale(name, description);

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("gradesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "gradesTable." + i;
      
      String gradeName = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      String gradeQualification = jsonRequestContext.getRequest().getParameter(colPrefix + ".qualification");
      Double gradeGPA = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter(colPrefix + ".GPA"));
      String gradeDescription = jsonRequestContext.getRequest().getParameter(colPrefix + ".description");
      Boolean passingGrade = "1".equals(jsonRequestContext.getRequest().getParameter(colPrefix + ".passingGrade"));
      
      gradingDAO.createGrade(gradingScale, gradeName, gradeDescription, passingGrade, gradeGPA, gradeQualification);
    }
    
    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/settings/editgradingscale.page?gradingScaleId=" + gradingScale.getId();
    String refererAnchor = jsonRequestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    jsonRequestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
