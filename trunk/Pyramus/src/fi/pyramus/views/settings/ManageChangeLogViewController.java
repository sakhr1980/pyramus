package fi.pyramus.views.settings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.persistence.Version;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.dao.ChangeLogDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.changelog.TrackedEntityProperty;
import fi.pyramus.persistence.events.TrackedEntityUtils;
import fi.pyramus.views.PyramusFormViewController;

/**
 * The controller responsible of the system settings view of the application.
 */
public class ManageChangeLogViewController extends PyramusFormViewController {

  @Override
  public void processForm(PageRequestContext requestContext) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    ChangeLogDAO changeLogDAO = DAOFactory.getInstance().getChangeLogDAO();
    
    List<ManageChangeLogViewEntityBean> entityBeans = new ArrayList<ManageChangeLogViewEntityBean>();
    List<EntityType<?>> entities = new ArrayList<EntityType<?>>(systemDAO.getEntities());
    for (EntityType<?> entity : entities) {
      try {
        List<ManageChangeLogViewEntityPropertyBean> properties = new ArrayList<ManageChangeLogViewEntityPropertyBean>();

        String entityName = entity.getName();
        Class<?> entityClass = Class.forName(entityName);
        SingularAttribute<?, ?> idAttribute = systemDAO.getEntityIdAttribute(entityClass);
        
        Set<Attribute<?, ?>> attributes = systemDAO.getEntityAttributes(entityClass);
        for (Attribute<?, ?> attribute : attributes) {
          switch (attribute.getPersistentAttributeType()) {
            case BASIC:
            case ONE_TO_ONE:
            case MANY_TO_ONE:
              if ((!attribute.equals(idAttribute)) && !this.isVersion(entityClass, attribute)) {
                String propertyName = attribute.getName();
                TrackedEntityProperty trackedEntityProperty = changeLogDAO.findTrackedEntityPropertyByEntityAndProperty(entityName, propertyName);
                ManageChangeLogViewEntityPropertyBean propertyBean = new ManageChangeLogViewEntityPropertyBean(propertyName, StringUtils.capitalize(propertyName), trackedEntityProperty != null);
                properties.add(propertyBean);
              }
            break;
          }
        }
        
        Collections.sort(properties, new Comparator<ManageChangeLogViewEntityPropertyBean>() {
          @Override
          public int compare(ManageChangeLogViewEntityPropertyBean o1, ManageChangeLogViewEntityPropertyBean o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
          }
        });
        
        ManageChangeLogViewEntityBean entityBean = new ManageChangeLogViewEntityBean(entityClass.getName(), entityClass.getSimpleName(), properties);
        entityBeans.add(entityBean);
      } catch (ClassNotFoundException e) {
        throw new PyramusRuntimeException(e);
      }
    }
    
    Collections.sort(entityBeans, new Comparator<ManageChangeLogViewEntityBean>() {
      @Override
      public int compare(ManageChangeLogViewEntityBean o1, ManageChangeLogViewEntityBean o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
      }
    });
    
    requestContext.getRequest().setAttribute("entities", entityBeans);
    
    requestContext.setIncludeJSP("/templates/settings/managechangelog.jsp");
  }
  
  @Override
  public void processSend(PageRequestContext requestContext) {
    ChangeLogDAO changeLogDAO = DAOFactory.getInstance().getChangeLogDAO();
    
    int rowCount = requestContext.getInteger("settingsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "settingsTable." + i;
      boolean track = "1".equals(requestContext.getString(colPrefix + ".track"));
      String entity = requestContext.getString(colPrefix + ".entity");
      String property = requestContext.getString(colPrefix + ".property");
      
      if (!StringUtils.isBlank(entity) && !StringUtils.isBlank(property)) {
        TrackedEntityProperty trackedEntityProperty = changeLogDAO.findTrackedEntityPropertyByEntityAndProperty(entity, property);
        if (track == false && trackedEntityProperty != null)
          changeLogDAO.deleteTrackedEntityProperty(trackedEntityProperty);
        else if (track == true && trackedEntityProperty == null)
          changeLogDAO.createTrackedEntityProperty(entity, property);
      }
    }
    
    TrackedEntityUtils.flushTrackedEntityFields();
    
    requestContext.setRedirectURL(requestContext.getRequest().getContextPath() + "/settings/managechangelog.page");
  }
  
 
  /**
   * Returns the roles allowed to access this page.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR, UserRole.MANAGER };
  }

  private boolean isVersion(Class<?> entityClass, Attribute<?, ?> attribute) {
    try {
      Field field = getField(entityClass, attribute.getName());
      if (field != null) {
        for (Annotation annotation : field.getAnnotations()) {
          if (annotation.annotationType().equals(Version.class))
            return true;
        }
      }
    } catch (SecurityException e) {
    } 
    
    return false;
  }
    
  private Field getField(Class<?> clazz, String name) {
    try {
      return clazz.getDeclaredField(name);
    } catch (SecurityException e) {
      return null;
    } catch (NoSuchFieldException e) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null && !Object.class.equals(superClass))
        return getField(superClass, name);
    }
    
    return null;
  }
}
