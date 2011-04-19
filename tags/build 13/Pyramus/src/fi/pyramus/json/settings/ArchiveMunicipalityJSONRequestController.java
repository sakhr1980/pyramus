package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of archiving a municipality. 
 */
public class ArchiveMunicipalityJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to archive a municipality.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Long muncipalityId = jsonRequestContext.getLong("municipalityId");
    baseDAO.archiveMunicipality(baseDAO.getMunicipality(muncipalityId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
