package fi.pyramus.views.system;

import java.util.List;

import javax.servlet.http.HttpSession;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;

public class ReindexHibernateObjects extends PyramusViewController {

  public void process(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();

    HttpSession session = requestContext.getRequest().getSession();
    @SuppressWarnings("unchecked")
    List<Class<?>> pendingIndexingTasks = (List<Class<?>>) session.getAttribute("pendingIndexingTasks");
   
    if (pendingIndexingTasks == null) {
      pendingIndexingTasks = systemDAO.getIndexedEntities();
      session.setAttribute("pendingIndexingTasks", pendingIndexingTasks);
      requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/reindexhibernateobjects.page");
    } else {
      if (pendingIndexingTasks.size() == 0) {
        requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
      } else {
        Class<?> clazz = pendingIndexingTasks.get(0);
        pendingIndexingTasks.remove(0);
        session.setAttribute("pendingIndexingTasks", pendingIndexingTasks);
        
        try {
          systemDAO.reindexHibernateSearchObjects(clazz);
        } catch (InterruptedException e) {
          throw new SmvcRuntimeException(e);
        }
        
        requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/reindexhibernateobjects.page");
      }
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
