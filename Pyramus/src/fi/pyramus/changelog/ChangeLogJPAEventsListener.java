package fi.pyramus.changelog;

import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import fi.pyramus.dao.ChangeLogDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.changelog.ChangeLogEntry;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryEntity;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryEntityProperty;
import fi.pyramus.domainmodel.changelog.ChangeLogEntryType;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.events.EventType;

@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") }, mappedName = "jms/JPAEvents")
public class ChangeLogJPAEventsListener implements MessageListener {

  public void onMessage(Message message) {
    try {
      MapMessage mapMessage = (MapMessage) message;
      
      EventType eventType = EventType.valueOf(mapMessage.getString("eventType"));
      
      switch (eventType) {
        case Create:
          handleCreate(mapMessage);
        break;
        case Update:
          handleUpdate(mapMessage);
        break;
        case Delete:
          handleDelete(mapMessage);
        break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void handleCreate(MapMessage mapMessage) throws JMSException  {
    String entity = mapMessage.getString("entity");
    Object id = mapMessage.getObject("id");
    Date time = new Date(mapMessage.getLong("time"));

    System.out.println('\n' +
        "—----EVENT-----" + '\n' +
        "Time" + time + '\n' +
        "Handle create" + '\n' +
        "Entity: " + entity + '\n' +
        "Id: " + id + '\n' + 
        "—————————————--");
  }
  
  private void handleDelete(MapMessage mapMessage) throws JMSException  {
    String entity = mapMessage.getString("entity");
    Object id = mapMessage.getObject("id");
    Date time = new Date(mapMessage.getLong("time"));
    
    System.out.println('\n' +
        "—----EVENT-----" + '\n' +
        "Time" + time + '\n' +
        "Handle delete" + '\n' +
        "Entity: " + entity + '\n' +
        "Id: " + id + '\n' + 
        "—————————————--");
  }
  
  private void handleUpdate(MapMessage mapMessage) throws JMSException  {
    ChangeLogDAO changeLogDAO = DAOFactory.getInstance().getChangeLogDAO();
    
    String entity = mapMessage.getString("entity");
    Object id = mapMessage.getObject("id");
    Date time = new Date(mapMessage.getLong("time"));
    User loggedUser = null; // TODO
    String ip = null; // TODO
    
    // First we need to check if ChangeLogEntryEntity is already in the database
    ChangeLogEntryEntity changeLogEntryEntity = changeLogDAO.findChangeLogEntryEntityByName(entity);
    if (changeLogEntryEntity == null) {
      // if not we need to add it 
      changeLogEntryEntity = changeLogDAO.createChangeLogEntryEntity(entity);
    }
    
    // Then we can add the log entry
    ChangeLogEntry entry = changeLogDAO.createChangeLogEntry(changeLogEntryEntity, ChangeLogEntryType.Update, String.valueOf(id), time, loggedUser, ip);

    // After the entry has been added we can add all the changed properties
    
    int fieldCount = mapMessage.getInt("fieldCount");
    for (int i = 0; i < fieldCount; i++) {
      String fieldName = mapMessage.getString("field." + i + ".name");
      Object newValue = mapMessage.getObject("field." + i + ".newValue");      

      // We need to check if database already contains this entity property
      ChangeLogEntryEntityProperty changeLogEntryEntityProperty = changeLogDAO.findChangeLogEntryEntityPropertyByEntityAndName(changeLogEntryEntity, fieldName);
      if (changeLogEntryEntityProperty == null) {
        // if not we add it there
        changeLogEntryEntityProperty = changeLogDAO.createChangeLogEntryEntityProperty(changeLogEntryEntity, fieldName);
      }
      
      // After entity property has been resolved we can add the property itself
      changeLogDAO.createChangeLogEntryProperty(entry, changeLogEntryEntityProperty, String.valueOf(newValue));
    }

    
  }  
}
