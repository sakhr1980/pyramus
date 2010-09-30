package fi.pyramus.json.resources;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveResourceCategoriesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("resourceCategoriesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "resourceCategoriesTable." + i;
      Long resourceCategoryId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".resourceCategoryId"));
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      boolean modified = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter(colPrefix + ".modified")) == 1;
      if (resourceCategoryId == -1) {
        resourceDAO.createResourceCategory(name); 
      }
      else if (modified) {
        ResourceCategory resourceCategory = resourceDAO.findResourceCategoryById(resourceCategoryId);
        resourceDAO.updateResourceCategory(resourceCategory, name);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
