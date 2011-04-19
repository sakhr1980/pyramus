package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.base.StudyProgrammeCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveStudyProgrammesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("studyProgrammesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "studyProgrammesTable." + i;
      Long studyProgrammeId = jsonRequestContext.getLong(colPrefix + ".studyProgrammeId");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      String code = jsonRequestContext.getString(colPrefix + ".code");
      Long categoryId = jsonRequestContext.getLong(colPrefix + ".category");
      
      StudyProgrammeCategory category = null;
      
      if (categoryId != null) {
        category = baseDAO.getStudyProgrammeCategory(categoryId);
      }
      
      boolean modified = jsonRequestContext.getInteger(colPrefix + ".modified") == 1;
      if (studyProgrammeId == -1) {
        baseDAO.createStudyProgramme(name, category, code); 
      }
      else if (modified) {
        StudyProgramme studyProgramme = baseDAO.getStudyProgramme(studyProgrammeId);
        baseDAO.updateStudyProgramme(studyProgramme, name, category, code);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
