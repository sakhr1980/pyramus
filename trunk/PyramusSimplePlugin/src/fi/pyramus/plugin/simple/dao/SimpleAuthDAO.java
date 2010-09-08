package fi.pyramus.plugin.simple.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import fi.pyramus.dao.PyramusDAO;
import fi.pyramus.plugin.simple.domainmodel.users.SimpleAuth;

public class SimpleAuthDAO extends PyramusDAO {

  public SimpleAuth findSimpleAuthById(Long id) {
    Session session = getHibernateSession();
    return (SimpleAuth) session.load(SimpleAuth.class, id);
  }
  
  public SimpleAuth findSimpleAuthByUserNameAndPassword(String username, String password) {
    Session session = getHibernateSession();
    
    return (SimpleAuth) session.createCriteria(SimpleAuth.class)
      .add(Restrictions.eq("username", username))
      .add(Restrictions.eq("password", password))
      .uniqueResult();
  }
  
  public SimpleAuth createSimpleAuth(String username, String password) {
    Session session = getHibernateSession();
    
    SimpleAuth simpleAuth = new SimpleAuth();
    simpleAuth.setUsername(username);
    simpleAuth.setPassword(password);
    
    session.saveOrUpdate(simpleAuth);
    
    return simpleAuth;
  }
  
  public void updateSimpleAuth(SimpleAuth simpleAuth, String username, String password) {
    Session session = getHibernateSession();
    
    simpleAuth.setUsername(username);
    simpleAuth.setPassword(password);
    
    session.saveOrUpdate(simpleAuth);
  }
  
  public void deleteSimpleAuth(SimpleAuth simpleAuth) {
    Session session = getHibernateSession();
    
    session.delete(simpleAuth);
  }
}
