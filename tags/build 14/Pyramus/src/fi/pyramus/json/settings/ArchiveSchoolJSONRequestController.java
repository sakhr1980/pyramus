package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveSchoolJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Long schoolId = NumberUtils.createLong(requestContext.getRequest().getParameter("schoolId"));
    School school = baseDAO.getSchool(schoolId);
    baseDAO.archiveSchool(school);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
