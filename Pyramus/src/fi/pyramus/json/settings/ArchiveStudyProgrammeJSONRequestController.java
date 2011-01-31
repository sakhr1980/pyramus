package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveStudyProgrammeJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    Long studyProgrammeId = NumberUtils.createLong(requestContext.getRequest().getParameter("studyProgrammeId"));
    StudyProgramme studyProgramme = baseDAO.getStudyProgramme(studyProgrammeId);
    baseDAO.archiveStudyProgramme(studyProgramme);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
