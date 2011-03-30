package fi.pyramus.persistence.events;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.metamodel.Attribute;

import org.hibernate.HibernateException;
import org.hibernate.ejb.event.EJB3FlushEventListener;
import org.hibernate.event.FlushEvent;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.util.ReflectionApiUtils;

public class EntityListener extends EJB3FlushEventListener {

  private static final long serialVersionUID = -1109310014693855151L;

  @Override
  public void onFlush(FlushEvent event) throws HibernateException {
    super.onFlush(event);
    flushEntityStateData();
  }

  @PostPersist
  void onPostPersist(Object entity) {
    String entityName = entity.getClass().getName();
    
    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      try {
        Object id = getEntityId(entity);
        if (id != null) {
          Session session = createSession();
  
          MapMessage message = session.createMapMessage();
          message.setLong("time", System.currentTimeMillis());
          message.setString("entity", entity.getClass().getName());
          message.setString("eventType", EventType.Create.name());
          message.setObject("id", id);
          
          sendMessage(session, message);
        
          prepareUpdateBatch(entity, id, entityName);
        }
      } catch (JMSException e) {
        throw new EventException(e);
      } catch (NamingException e) {
        throw new EventException(e);
      }
    }
  }

  @PostLoad
  void onPostLoad(Object entity) {
    String entityName = entity.getClass().getName();

    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      Object id = getEntityId(entity);
      if (id != null) {
        prepareUpdateBatch(entity, id, entityName);
      }
    }
  }

  @PostUpdate
  void onPostUpdate(Object entity) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();

    String entityName = entity.getClass().getName();

    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      Object id = getEntityId(entity);
      if (id != null) {
        Map<String, Object> updateBatch = getEntityStateData().getUpdateBatch(entityName, id);
        if (updateBatch == null)
          throw new EventException("could not find update batch");

        Map<String, Object[]> changedFields = new HashMap<String, Object[]>();
        for (String fieldName : updateBatch.keySet()) {
          try {
            Attribute<?, ?> attribute = systemDAO.getEntityAttribute(entity.getClass(), fieldName);
            Object newValue = null;
            Object oldValue = updateBatch.get(fieldName);
            
            switch (attribute.getPersistentAttributeType()) {
              case BASIC:
                newValue = ReflectionApiUtils.getObjectFieldValue(entity, fieldName, true);
              break;
              case ONE_TO_ONE:
              case MANY_TO_ONE:
                Object joinedEntity = ReflectionApiUtils.getObjectFieldValue(entity, fieldName, true);
                if (joinedEntity != null) {
                  newValue = getEntityId(attribute.getJavaType(), joinedEntity);
                }
              break;
            }
            
            if ((oldValue != newValue) && ((oldValue == null) || (!oldValue.equals(newValue)))) {
              changedFields.put(fieldName, new Object[] { oldValue, newValue });
            }
          } catch (SecurityException e) {
            throw new EventException(e);
          } catch (IllegalArgumentException e) {
            throw new EventException(e);
          } catch (IllegalAccessException e) {
            throw new EventException(e);
          } catch (InvocationTargetException e) {
            throw new EventException(e);
          }
        }
        
        getEntityStateData().removeUpdateBatch(entityName, id);
        if (changedFields.size() > 0) {
          try {
            Session session = createSession();

            MapMessage message = session.createMapMessage();
            message.setLong("time", System.currentTimeMillis());
            message.setString("entity", entity.getClass().getName());
            message.setString("eventType", EventType.Update.name());
            message.setObject("id", getEntityId(entity));

            int i = 0;
            for (String fieldName : changedFields.keySet()) {
              Object oldValue = changedFields.get(fieldName)[0];
              Object newValue = changedFields.get(fieldName)[1];
              message.setString("field." + i + ".name", fieldName);
              if (oldValue != null)
                message.setObject("field." + i + ".oldValue", oldValue);
              if (newValue != null)
                message.setObject("field." + i + ".newValue", newValue);
              i++;
            }

            message.setInt("fieldCount", i);
            sendMessage(session, message);
          } catch (JMSException e) {
            throw new EventException(e);
          } catch (NamingException e) {
            throw new EventException(e);
          }
        }
      }
    }
  }

  @PreRemove
  void onPreRemove(Object entity) {
    if (TrackedEntityUtils.isTrackedEntity(entity.getClass().getName())) {
      try {
        Session session = createSession();

        MapMessage message = session.createMapMessage();
        message.setLong("time", System.currentTimeMillis());
        message.setString("entity", entity.getClass().getName());
        message.setString("eventType", EventType.Delete.name());
        message.setObject("id", getEntityId(entity));

        sendMessage(session, message);
      } catch (JMSException e) {
        throw new EventException(e);
      } catch (NamingException e) {
        throw new EventException(e);
      }
    }
  }
  
  private Object getEntityId(Object entity) {
    return getEntityId(entity.getClass(), entity);
  }
  
  private Object getEntityId(Class<?> entityClass, Object entity) {
    SystemDAO systemDAO = new SystemDAO();
    Attribute<?, ?> idAttribute = systemDAO.getEntityIdAttribute(entityClass);
    try {
      return ReflectionApiUtils.getObjectFieldValue(entity, idAttribute.getName(), true);
    } catch (Exception e) {
      return null;
    }
  }

  private void prepareUpdateBatch(Object entity, Object id, String entityName) {
    SystemDAO systemDAO = new SystemDAO();
    
    Map<String, Object> updateBatch = getEntityStateData().getUpdateBatch(entityName, id);
    if (updateBatch == null) {
      updateBatch = getEntityStateData().addUpdateBatch(entityName, id);

      Set<javax.persistence.metamodel.Attribute<?, ?>> attributes = systemDAO.getEntityAttributes(entity.getClass());
      for (Attribute<?, ?> attribute : attributes) {
        String fieldName = attribute.getName();

        if (TrackedEntityUtils.isTrackedProperty(entityName, fieldName)) {
          Object value = null;
          try {
            switch (attribute.getPersistentAttributeType()) {
              case BASIC:
                value = ReflectionApiUtils.getObjectFieldValue(entity, fieldName, true);
              break;
              case ONE_TO_ONE:
              case MANY_TO_ONE:
                Object joinedEntity = ReflectionApiUtils.getObjectFieldValue(entity, fieldName, true);
                if (joinedEntity != null) {
                  value = getEntityId(attribute.getJavaType(), joinedEntity);
                }
              break;
            }
            
            updateBatch.put(fieldName, value);
          } catch (IllegalArgumentException e) {
            throw new EventException(e);
          } catch (IllegalAccessException e) {
            throw new EventException(e);
          } catch (InvocationTargetException e) {
            throw new EventException(e);
          }
        } 
      }
    }
  }

  private void sendMessage(Session session, Message message) throws NamingException, JMSException {
    Topic topic = getTopic();
    MessageProducer producer = session.createProducer(topic);
    producer.send(message);
  }

  private Session createSession() throws JMSException, NamingException {
    ConnectionFactory factory = getConnectionFactory();
    Connection connection = factory.createConnection();
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    return session;
  }

  private ConnectionFactory getConnectionFactory() throws NamingException {
    return (ConnectionFactory) new InitialContext().lookup("jms/JPAEventsFactory");
  }

  private Topic getTopic() throws NamingException {
    return (Topic) new InitialContext().lookup("jms/JPAEvents");
  }

  private static EntityStateData getEntityStateData() {
    EntityStateData entityStateData = THREAD_LOCAL.get();
    if (entityStateData == null) {
      entityStateData = new EntityStateData();
      THREAD_LOCAL.set(entityStateData);
    }
    
    return entityStateData;
  }
  
  public static void flushEntityStateData() {
    THREAD_LOCAL.set(null);
  }
  
  private static final ThreadLocal<EntityStateData> THREAD_LOCAL = new ThreadLocal<EntityStateData>();
 }
