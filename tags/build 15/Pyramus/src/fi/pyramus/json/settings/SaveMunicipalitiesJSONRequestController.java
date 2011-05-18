package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.json.JSONRequestController;

public class SaveMunicipalitiesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("municipalitiesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "municipalitiesTable." + i;
      Long municipalityId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".id"));
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      String code = jsonRequestContext.getRequest().getParameter(colPrefix + ".code");
      boolean modified = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter(colPrefix + ".modified")) == 1;
      if (municipalityId == -1) {
        baseDAO.createMunicipality(name, code); 
      }
      else if (modified) {
        Municipality municipality = baseDAO.getMunicipality(municipalityId);
        baseDAO.updateMunicipality(municipality, name, code);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
