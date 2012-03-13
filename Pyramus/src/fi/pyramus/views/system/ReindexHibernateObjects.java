package fi.pyramus.views.system;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.impl.SimpleIndexingProgressMonitor;
import org.hibernate.search.jmx.IndexingProgressMonitor;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.logging.Logging;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;

public class ReindexHibernateObjects extends PyramusViewController {

  public void process(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    Class<?> indexClass = null;
    String indexClassName = requestContext.getString("class");
    if (StringUtils.isBlank(indexClassName)) {
      HttpSession session = requestContext.getRequest().getSession();
      @SuppressWarnings("unchecked")
      List<Class<?>> pendingIndexingTasks = (List<Class<?>>) session.getAttribute("pendingIndexingTasks");
      
      if (pendingIndexingTasks == null) {
        pendingIndexingTasks = systemDAO.getIndexedEntities();
        session.setAttribute("pendingIndexingTasks", pendingIndexingTasks);
        requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/reindexhibernateobjects.page");
        return;
      }
      
      if (pendingIndexingTasks.size() != 0) {
        indexClass = pendingIndexingTasks.get(0);
        pendingIndexingTasks.remove(0);
        session.setAttribute("pendingIndexingTasks", pendingIndexingTasks);
      } else {
        requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
        return;
      }
    } else {
      try {
        indexClass = Class.forName(indexClassName);
      } catch (ClassNotFoundException e) {
      }
    }

    if (indexClass == null) {
      requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
      return;
    }
   
    Logging.logInfo("Indexing class " + indexClass.toString());

    IndexingProgressMonitor progressMonitor = new IndexingProgressMonitor();
    try {
      systemDAO.reindexHibernateSearchObjects(indexClass, progressMonitor);
    } catch (InterruptedException e) {
      throw new SmvcRuntimeException(e);
    }
    
    if (StringUtils.isNotBlank(indexClassName)) {
      requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/index.page");
      return;
    }

    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/system/reindexhibernateobjects.page");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

}
