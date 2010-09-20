package fi.pyramus.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.stat.Statistics;

import fi.pyramus.domainmodel.system.Setting;
import fi.pyramus.domainmodel.system.SettingKey;

public class SystemDAO extends PyramusDAO {
  
  // Setting keys
  
  public SettingKey createSettingKey(String name) {
    EntityManager entityManager = getEntityManager();
    
    SettingKey settingKey = new SettingKey();
    settingKey.setName(name);
    
    entityManager.persist(settingKey);
    
    return settingKey;
  }
  
  public SettingKey findSettingKeyById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(SettingKey.class, id);
  } 
  
  public SettingKey findSettingKeyByName(String name) {
    Session session = getHibernateSession();
    
    return (SettingKey) session.createCriteria(SettingKey.class)
      .add(Restrictions.eq("name", name))
      .uniqueResult();
  }
  
  @SuppressWarnings("unchecked")
  public List<SettingKey> listSettingKeys() {
    Session session = getHibernateSession();
    
    return (List<SettingKey>) session.createCriteria(SettingKey.class)
      .list();
  }
  
  public void updateSettingKey(SettingKey settingKey, String name) {
    EntityManager entityManager = getEntityManager();
    
    settingKey.setName(name);
    
    entityManager.persist(settingKey);
  }
  
  public void deleteSettingKey(SettingKey settingKey) {
    EntityManager entityManager = getEntityManager();
    
    entityManager.remove(settingKey);
  }
  
  // Settings
  
  public Setting createSetting(SettingKey settingKey, String value) {
    EntityManager entityManager = getEntityManager();
    
    Setting setting = new Setting();
    setting.setKey(settingKey);
    setting.setValue(value);
    
    entityManager.persist(setting);
    
    return setting;
  }
  
  public Setting findSettingById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(Setting.class, id);
  } 
  
  public Setting findSettingByKey(SettingKey key) {
    Session session = getHibernateSession();
    
    return (Setting) session.createCriteria(Setting.class)
      .add(Restrictions.eq("key", key))
      .uniqueResult();
  }
  
  public void updateSetting(Setting setting, SettingKey settingKey, String value) {
    EntityManager entityManager = getEntityManager();
    
    setting.setKey(settingKey);
    setting.setValue(value);
    
    entityManager.persist(setting);
  }
  
  public void deleteSetting(Setting setting) {
    EntityManager entityManager = getEntityManager();
    
    entityManager.remove(setting);
  }

  public void forceReindex(Object o) {
    super.forceReindex(o);
  }
  
  // Hibernate methods
  
  public Session getHibernateSession() {
    return super.getHibernateSession();
  }
  
  public Set<ConstraintViolation<Object>> validateEntity(Object entity) {
	Validator validator = validatorFactory.getValidator();
	return validator.validate(entity);
  }
  
  public Query createHQLQuery(String hql) {
    return getHibernateSession().createQuery(hql);
  }
  
  public Criteria createHibernateCriteria(Class<?> entity) {
    return getHibernateSession().createCriteria(entity);
  }
  
  @SuppressWarnings("unchecked")
  public Map<Object, ClassMetadata> getHibernateClassMetadata() {
    return getHibernateSession().getSessionFactory().getAllClassMetadata();
  } 
  
  public Statistics getHibernateStatistics() {
    return getHibernateSession().getSessionFactory().getStatistics();
  }
  
  public void reindexHibernateSearchObjects() {
    Session s = getHibernateSession();
    
    Map<Object, ClassMetadata> classMetaData = getHibernateClassMetadata();

    for (Object key : classMetaData.keySet()) {
      ClassMetadata cmd = classMetaData.get(key);
      Class<?> pojo = cmd.getMappedClass(EntityMode.POJO);
      if (pojo.getAnnotation(Indexed.class) != null) {

        FullTextSession fullTextSession = Search.getFullTextSession(s);
        
        fullTextSession.purgeAll(pojo);
        
        ScrollableResults results = s.createCriteria(pojo).scroll();
        try {
          while (results.next())
            fullTextSession.index(results.get()[0]);                  
        } finally {
          results.close();
        }  
        
        fullTextSession.flushToIndexes();
        fullTextSession.getSearchFactory().optimize();         
      }
    }
  }
  
  private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
}
