package fi.pyramus.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fi.pyramus.domainmodel.changelog.ChangeLogEntry;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryEntity;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryEntityProperty;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryProperty;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryType;
import fi.pyramus.domainmodel.changelog.TrackedEntityProperty;
import fi.pyramus.domainmodel.users.User;

public class ChangeLogDAO extends PyramusDAO {
  
  /* ChangeLogEntry */
  
  public ChangeLogEntry createChangeLogEntry(ChangeLogEntryEntity entity, ChangeLogEntryType type, String entityId, Date time, User user) {
    EntityManager entityManager = getEntityManager();

    ChangeLogEntry changeLogEntry = new ChangeLogEntry();
    changeLogEntry.setEntityId(entityId);
    changeLogEntry.setEntity(entity);
    changeLogEntry.setType(type);
    changeLogEntry.setTime(time);
    changeLogEntry.setUser(user);
    
    entityManager.persist(changeLogEntry);
    
    return changeLogEntry;
  }
  
  public ChangeLogEntry findChangeLogEntryById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ChangeLogEntry.class, id);
  }
  
  /* ChangeLogEntryProperty */
  
  public ChangeLogEntryProperty createChangeLogEntryProperty(ChangeLogEntry entry, ChangeLogEntryEntityProperty property, String value) {
    EntityManager entityManager = getEntityManager();

    ChangeLogEntryProperty changeLogEntryProperty = new ChangeLogEntryProperty();
    changeLogEntryProperty.setEntry(entry);
    changeLogEntryProperty.setProperty(property);
    changeLogEntryProperty.setValue(value);
    
    entityManager.persist(changeLogEntryProperty);
    
    return changeLogEntryProperty;
  }
  
  public ChangeLogEntryProperty findLatestEntryPropertyByEntryEntityProperty(ChangeLogEntryEntityProperty entryEntityProperty, String entityId) {
    EntityManager entityManager = getEntityManager();
    
    Query query = entityManager.createQuery(
      "select " +  
      "  p " +
      "from " +   
      "  fi.pyramus.domainmodel.changelog.ChangeLogEntryProperty p " +   
      "where " +   
      "  p.property = :property AND " +
      "  p.entry.entityId = :entityId " +
      "order by " + 
      "  p.entry.time desc");
    
    query.setParameter("property", entryEntityProperty);
    query.setParameter("entityId", entityId);
    query.setMaxResults(1);
    @SuppressWarnings("unchecked") List<ChangeLogEntryProperty> result = query.getResultList();
    return result.size() == 1 ? result.get(0) : null;
  }
  
  public ChangeLogEntryProperty findChangeLogEntryPropertyById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ChangeLogEntryProperty.class, id);
  }
  
  /* ChangeLogEntryEntity */
  
  public ChangeLogEntryEntity createChangeLogEntryEntity(String name) {
    EntityManager entityManager = getEntityManager();

    ChangeLogEntryEntity changeLogEntryEntity = new ChangeLogEntryEntity();
    changeLogEntryEntity.setName(name);
    
    entityManager.persist(changeLogEntryEntity);
    
    return changeLogEntryEntity;
  }
  
  public ChangeLogEntryEntity findChangeLogEntryEntityById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ChangeLogEntryEntity.class, id);
  }
  
  public ChangeLogEntryEntity findChangeLogEntryEntityByName(String name) {
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("from ChangeLogEntryEntity where name = :name");
    query.setParameter("name", name);
    @SuppressWarnings("unchecked") List<ChangeLogEntryEntity> result = query.getResultList();
    return result.size() == 1 ? result.get(0) : null;
  }
  
  /* ChangeLogEntryEntityProperty */
  
  public ChangeLogEntryEntityProperty createChangeLogEntryEntityProperty(ChangeLogEntryEntity entity, String name) {
    EntityManager entityManager = getEntityManager();

    ChangeLogEntryEntityProperty changeLogEntryEntityProperty = new ChangeLogEntryEntityProperty();
    changeLogEntryEntityProperty.setEntity(entity);
    changeLogEntryEntityProperty.setName(name);
    
    entityManager.persist(changeLogEntryEntityProperty);
    
    return changeLogEntryEntityProperty;
  }
  
  public ChangeLogEntryEntityProperty findChangeLogEntryEntityPropertyByEntityAndName(ChangeLogEntryEntity entity, String name) {
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("from ChangeLogEntryEntityProperty where entity = :entity and name = :name");
    query.setParameter("name", name);
    query.setParameter("entity", entity);
    @SuppressWarnings("unchecked") List<ChangeLogEntryEntityProperty> result = query.getResultList();
    return result.size() == 1 ? result.get(0) : null;
  }
  
  public ChangeLogEntryEntityProperty findChangeLogEntryEntityPropertyById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ChangeLogEntryEntityProperty.class, id);
  }

  /* TrackedEntityProperty */
  
  public TrackedEntityProperty createTrackedEntityProperty(String entity, String property) {
    EntityManager entityManager = getEntityManager();
    
    TrackedEntityProperty trackedEntityProperty = new TrackedEntityProperty();
    trackedEntityProperty.setEntity(entity);
    trackedEntityProperty.setProperty(property);
    
    entityManager.persist(trackedEntityProperty);
    
    return trackedEntityProperty;
  }
  
  public TrackedEntityProperty findTrackedEntityProperty(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(TrackedEntityProperty.class, id);
  }
  
  public TrackedEntityProperty findTrackedEntityPropertyByEntityAndProperty(String entity, String property) {
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("from TrackedEntityProperty where entity = :entity and property = :property");
    query.setParameter("entity", entity);
    query.setParameter("property", property);
    @SuppressWarnings("unchecked") List<TrackedEntityProperty> result = query.getResultList();
    return result.size() == 1 ? result.get(0) : null;
  }
  
  @SuppressWarnings("unchecked") 
  public List<TrackedEntityProperty> listTrackedEntityProperties() {
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("from TrackedEntityProperty");
    return query.getResultList();
  }
  
  public TrackedEntityProperty updateTrackedEntityPropertyEntity(TrackedEntityProperty trackedEntityProperty, String entity) {
    EntityManager entityManager = getEntityManager();
    trackedEntityProperty.setEntity(entity);
    entityManager.persist(trackedEntityProperty);
    return trackedEntityProperty;
  }

  public TrackedEntityProperty updateTrackedEntityPropertyProperty(TrackedEntityProperty trackedEntityProperty, String property) {
    EntityManager entityManager = getEntityManager();
    trackedEntityProperty.setProperty(property);
    entityManager.persist(trackedEntityProperty);
    return trackedEntityProperty;
  }
  
  public void deleteTrackedEntityProperty(TrackedEntityProperty trackedEntityProperty) {
    EntityManager entityManager = getEntityManager();
    entityManager.remove(trackedEntityProperty);
  }
  
}
