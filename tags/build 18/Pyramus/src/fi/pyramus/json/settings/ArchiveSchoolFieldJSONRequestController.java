package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.SchoolField;
import fi.pyramus.json.JSONRequestController;

public class ArchiveSchoolFieldJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Long schoolFieldId = NumberUtils.createLong(requestContext.getRequest().getParameter("schoolFieldId"));
    SchoolField schoolField = baseDAO.findSchoolFieldById(schoolFieldId);
    baseDAO.archiveSchoolField(schoolField);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
