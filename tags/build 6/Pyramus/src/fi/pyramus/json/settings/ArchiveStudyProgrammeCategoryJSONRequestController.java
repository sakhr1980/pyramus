package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.StudyProgrammeCategory;
import fi.pyramus.json.JSONRequestController;

public class ArchiveStudyProgrammeCategoryJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    Long studyProgrammeCategoryId = requestContext.getLong("studyProgrammeCategory");
    StudyProgrammeCategory studyProgrammeCategory = baseDAO.getStudyProgrammeCategory(studyProgrammeCategoryId);
    baseDAO.archiveStudyProgrammeCategory(studyProgrammeCategory);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
