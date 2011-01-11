package fi.pyramus.json.modules;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.domainmodel.modules.ModuleComponent;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveModuleComponentJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Long componentId = requestContext.getLong("componentId");
    ModuleComponent component = moduleDAO.getModuleComponent(componentId);
    moduleDAO.archiveModuleComponent(component);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
