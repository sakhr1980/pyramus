package fi.pyramus.persistence.events;

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
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.metamodel.Attribute;

import fi.pyramus.dao.SystemDAO;
import fi.pyramus.util.ReflectionApiUtils;

public class EntityListener {

  private static final long serialVersionUID = -1109310014693855151L;

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
        }
      } catch (JMSException e) {
        throw new EventException(e);
      } catch (NamingException e) {
        throw new EventException(e);
      }
    }
  }

  @PostUpdate
  void onPostUpdate(Object entity) {
    String entityName = entity.getClass().getName();
   
    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      try {
        Object id = getEntityId(entity);
        if (id != null) {
          Session session = createSession();
  
          MapMessage message = session.createMapMessage();
          message.setLong("time", System.currentTimeMillis());
          message.setString("entity", entity.getClass().getName());
          message.setString("eventType", EventType.Update.name());
          message.setObject("id", id);
          
          sendMessage(session, message);
        }
      } catch (JMSException e) {
        throw new EventException(e);
      } catch (NamingException e) {
        throw new EventException(e);
      }
    }
  }

  @PreRemove
  void onPreRemove(Object entity) {
    String entityName = entity.getClass().getName();
    
    if (TrackedEntityUtils.isTrackedEntity(entityName)) {
      try {
        Session session = createSession();

        MapMessage message = session.createMapMessage();
        message.setLong("time", System.currentTimeMillis());
        message.setString("entity", entityName);
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
 }
