package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.StudyProgrammeCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveStudyProgrammeCategoriesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("studyProgrammeCategoriesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studyProgrammeCategoriesTable." + i;
      Long studyProgrammeCategoryId = jsonRequestContext.getLong(colPrefix + ".studyProgrammeCategoryId");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      Long educationTypeId = jsonRequestContext.getLong(colPrefix + ".educationTypeId");
      EducationType educationType = null;
      if (educationTypeId != null)
        educationType = baseDAO.getEducationType(educationTypeId);
      boolean modified = jsonRequestContext.getInteger(colPrefix + ".modified") == 1;

      if (studyProgrammeCategoryId == -1) {
        baseDAO.createStudyProgrammeCategory(name, educationType); 
      }
      else if (modified) {
        StudyProgrammeCategory studyProgrammeCategory = baseDAO.getStudyProgrammeCategory(studyProgrammeCategoryId);
        baseDAO.updateStudyProgrammeCategory(studyProgrammeCategory, name, educationType);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
