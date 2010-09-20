package fi.pyramus.dao;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.sun.enterprise.container.common.impl.EntityManagerWrapper;

public class PyramusDAO {
  
  protected EntityManager getEntityManager() {
    try {
      InitialContext initialContext = new InitialContext();
      return (EntityManager) initialContext.lookup("java:comp/env/persistence/pyramusEntityManager");
    } catch (NamingException e) {
      throw new PersistenceException(e);
    }
  }
  
  protected Session getHibernateSession() {
    EntityManagerWrapper entityManagerWrapper = (EntityManagerWrapper) getEntityManager();
    return ((EntityManagerImpl) entityManagerWrapper.getDelegate()).getSession();
  }

  protected void forceReindex(Object o) {
    FullTextSession fullTextSession = Search.getFullTextSession(getHibernateSession());
    fullTextSession.index(o);
  }

}
