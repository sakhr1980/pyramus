package fi.pyramus.views.system;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.views.PyramusViewController;

public class ReindexHibernateObjects implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    systemDAO.reindexHibernateSearchObjects();
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
