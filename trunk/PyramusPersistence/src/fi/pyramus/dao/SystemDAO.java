package fi.pyramus.dao;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.stat.Statistics;

@Stateless
public class SystemDAO {
  
  SystemDAO() {
  }
  
  // JPA methods

  @SuppressWarnings({ "unchecked", "rawtypes" }) 
  public Object findEntityById(Class referencedClass, Object id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(referencedClass, id);
  }
  
  public SingularAttribute<?, ?> getEntityIdAttribute(Class<?> entityClass) {
    EntityType<?> entityType = getEntityManager().getMetamodel().entity(entityClass);
    return entityType.getId(entityType.getIdType().getJavaType());
  }

  public Set<EntityType<?>> getEntities() {
    return getEntityManager().getMetamodel().getEntities();
  }
  
  public javax.persistence.metamodel.Attribute<?, ?> getEntityAttribute(Class<?> entityClass, String fieldName) {
    EntityType<?> entityType = getEntityManager().getMetamodel().entity(entityClass);
    return entityType.getAttribute(fieldName);
  }
  
  public Set<javax.persistence.metamodel.Attribute<?, ?>> getEntityAttributes(Class<?> entityClass) {
    Set<javax.persistence.metamodel.Attribute<?, ?>> result = new HashSet<javax.persistence.metamodel.Attribute<?,?>>();
    
    EntityType<?> entityType = getEntityManager().getMetamodel().entity(entityClass);
    for (javax.persistence.metamodel.Attribute<?, ?> attribute : entityType.getAttributes()) {
      result.add(attribute);
    }
    
    return result;
  }
  
  public void persistEntity(Object entity) {
    getEntityManager().persist(entity);
  }
  
  public Set<ConstraintViolation<Object>> validateEntity(Object entity) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator().validate(entity);
  }

  // Hibernate methods
  
  @Deprecated
  private Session getHibernateSession() {
    EntityManagerImpl entityManagerImpl = (EntityManagerImpl) getEntityManager().getDelegate();
    return entityManagerImpl.getSession();
  }

  @Deprecated
  public Query createHQLQuery(String hql) {
    return getHibernateSession().createQuery(hql);
  }
  
  @Deprecated
  public Criteria createHibernateCriteria(Class<?> entity) {
    return getHibernateSession().createCriteria(entity);
  }

  @Deprecated
  public Map<String, ClassMetadata> getHibernateClassMetadata() {
    return getHibernateSession().getSessionFactory().getAllClassMetadata();
  } 
  
  @Deprecated
  public Statistics getHibernateStatistics() {
    return getHibernateSession().getSessionFactory().getStatistics();
  }
  
  public void reindexHibernateSearchObjects() throws InterruptedException {
    EntityManager entityManager = getEntityManager();
    
    FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
    MassIndexer massIndexer = fullTextSession.createIndexer();
    
    massIndexer.batchSizeToLoadObjects(10);
    massIndexer.threadsForSubsequentFetching(1);
    massIndexer.threadsToLoadObjects(1);
    massIndexer.cacheMode(CacheMode.IGNORE);
    
    massIndexer.startAndWait();
  }
  
  private EntityManager getEntityManager() {
    return entityManager;
  }
  
  @PersistenceContext
  private EntityManager entityManager;
}
