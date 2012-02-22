package fi.pyramus.plugin.simple.dao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.plugin.simple.domainmodel.users.SimpleAuth;

public class SimpleAuthDAO extends PyramusEntityDAO<SimpleAuth> {

  public SimpleAuth create(String username, String password) {
    EntityManager entityManager = getEntityManager();
    
    SimpleAuth simpleAuth = new SimpleAuth();
    simpleAuth.setUsername(username);
    simpleAuth.setPassword(password);
    
    entityManager.persist(simpleAuth);
    
    return simpleAuth;
  }

  public SimpleAuth findByUserNameAndPassword(String username, String password) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SimpleAuth> criteria = criteriaBuilder.createQuery(SimpleAuth.class);
    Root<SimpleAuth> root = criteria.from(SimpleAuth.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(SimpleAuth_.username), username),
            criteriaBuilder.equal(root.get(SimpleAuth_.password), password)
        ));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public void updateUsername(SimpleAuth simpleAuth, String username) {
    EntityManager entityManager = getEntityManager();
    
    simpleAuth.setUsername(username);
    
    entityManager.persist(simpleAuth);
  }
  
  public void updatePassword(SimpleAuth simpleAuth, String password) {
    EntityManager entityManager = getEntityManager();
    
    simpleAuth.setPassword(password);
    
    entityManager.persist(simpleAuth);
  }
  
  @Override
  public void delete(SimpleAuth simpleAuth) {
    super.delete(simpleAuth);
  }
}
