package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveEducationSubtypeJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    Long educationSubtypeId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("educationSubtypeId"));
    baseDAO.archiveEducationSubtype(baseDAO.getEducationSubtype(educationSubtypeId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
