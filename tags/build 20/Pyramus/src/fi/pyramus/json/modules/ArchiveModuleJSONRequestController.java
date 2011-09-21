package fi.pyramus.json.modules;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class ArchiveModuleJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Long moduleId = NumberUtils.createLong(requestContext.getRequest().getParameter("moduleId"));
    Module module = moduleDAO.getModule(moduleId);
    moduleDAO.archiveModule(module);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
