package fi.pyramus.views.system;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.PyramusViewController;

public class ReindexHibernateObjects extends PyramusViewController {

  public void process(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    try {
      systemDAO.reindexHibernateSearchObjects();
    } catch (InterruptedException e) {
      throw new SmvcRuntimeException(e);
    }
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
