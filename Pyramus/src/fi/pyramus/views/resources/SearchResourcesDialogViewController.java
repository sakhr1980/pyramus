package fi.pyramus.views.resources;

import fi.pyramus.PageRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.ResourceType;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class SearchResourcesDialogViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();
    requestContext.getRequest().setAttribute("resourceCategories", resourceDAO.listResourceCategories());
    requestContext.getRequest().setAttribute("resourceTypes", ResourceType.values());
    requestContext.setIncludeJSP("/templates/resources/searchresourcesdialog.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
