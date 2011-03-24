package fi.pyramus.persistence.events;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ejb.event.EJB3FlushEventListener;
import org.hibernate.event.FlushEvent;

import fi.pyramus.dao.SystemDAO;

public class EntityListener extends EJB3FlushEventListener {

  private static final long serialVersionUID = -1109310014693855151L;

  @Override
  public void onFlush(FlushEvent event) throws HibernateException {
    super.onFlush(event);
    flushEntityStateData();
  }

  @PostPersist
  void onPostPersist(Object entity) {
    if (TrackedEntityUtils.isTrackedEntity(entity.getClass().getName())) {
      try {
        Session session = createSession();

        MapMessage message = session.createMapMessage();
        message.setLong("time", System.currentTimeMillis());
        message.setString("entity", entity.getClass().getName());
        message.setString("eventType", EventType.Create.name());
        message.setObject("id", getEntityId(entity));

        sendMessage(session, message);
      } catch (JMSException e) {
        throw new EventException(e);
      } catch (NamingException e) {
        throw new EventException(e);
      }
    }
  }

  @PostLoad
  void onPostLoad(Object entity) {
    SystemDAO systemDAO = new SystemDAO();

    String entityName = entity.getClass().getName();

    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      Object id = getEntityId(entity);
      if (id != null) {
        Map<String, Object> updateBatch = getEntityStateData().getUpdateBatch(entityName, id);
        if (updateBatch == null) {
          updateBatch = getEntityStateData().addUpdateBatch(entityName, id);

          Set<javax.persistence.metamodel.Attribute<?, ?>> attributes = systemDAO.getEntityAttributes(entity.getClass());
          for (Attribute<?, ?> attribute : attributes) {
            if (!attribute.isCollection() && !attribute.isAssociation()) {
              String fieldName = attribute.getName();

              if (TrackedEntityUtils.isTrackedProperty(entityName, fieldName)) {
                try {
                  updateBatch.put(fieldName, getFieldValue(entity, fieldName));
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
      }
    }
  }

  @PostUpdate
  void onPostUpdate(Object entity) {
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
            Object oldValue = updateBatch.get(fieldName);
            Object newValue = getFieldValue(entity, fieldName);
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
    SystemDAO systemDAO = new SystemDAO();
    return systemDAO.getEntityId(entity);
  }

  private Object getFieldValue(Object entity, String name) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method getterMethod = getMethod(entity.getClass(), "get" + StringUtils.capitalize(name));
    if (getterMethod != null) {
      return getterMethod.invoke(entity);
    } else {
      Field field = getField(entity.getClass(), name);
      if (field != null) {
        field.setAccessible(true);
        return field.get(entity);
      } else {
        return null;
      }
    }
  }

  private Method getMethod(Class<?> entityClass, String name) {
    try {
      return entityClass.getDeclaredMethod(name);
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchMethodException e) {
      Class<?> superClass = entityClass.getSuperclass();
      if (superClass != null && !Object.class.equals(superClass))
        return getMethod(superClass, name);
    }

    return null;
  }

  private Field getField(Class<?> entityClass, String name) {
    try {
      return entityClass.getDeclaredField(name);
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchFieldException e) {
      Class<?> superClass = entityClass.getSuperclass();
      if (superClass != null && !Object.class.equals(superClass))
        return getField(superClass, name);
    }

    return null;
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
