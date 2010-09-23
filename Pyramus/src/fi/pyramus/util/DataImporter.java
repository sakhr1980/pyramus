package fi.pyramus.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.persistence.usertypes.MonetaryAmount;
import fi.pyramus.persistence.usertypes.ProjectModuleOptionality;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.persistence.usertypes.StudentContactLogEntryType;
import fi.pyramus.persistence.usertypes.VariableType;

public class DataImporter {
  
  public void importDataFromFile(String filename, Collection<String> entities) {
    try {
      DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
      Document initialDataDocument = db.parse(filename);
      importDataFromDocument(initialDataDocument, entities);
    } catch (ParserConfigurationException e) {
      throw new PyramusRuntimeException(e);
    } catch (SAXException e) {
      throw new PyramusRuntimeException(e);
    } catch (IOException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  public void importDataFromStream(InputStream stream, Collection<String> entities) {
    try {
      DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
      Document initialDataDocument = db.parse(stream);
      importDataFromDocument(initialDataDocument, entities);
    } catch (ParserConfigurationException e) {
      throw new PyramusRuntimeException(e);
    } catch (SAXException e) {
      throw new PyramusRuntimeException(e);
    } catch (IOException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  @SuppressWarnings({ "rawtypes" })
  public void importDataFromDocument(Document initialDataDocument, Collection<String> entities) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    try {
      NodeIterator storeIterator = XPathAPI.selectNodeIterator(initialDataDocument.getDocumentElement(), "store");      
      Node storeNode;
      while ((storeNode = storeIterator.nextNode()) != null) {
        if (storeNode instanceof Element) {
          Element storeElement = (Element) storeNode;
          String variableName = storeElement.getAttribute("storeVariable");
          String hql = storeElement.getAttribute("hql");
          Object result = null;
          
          System.out.println("Processing " + variableName);
          
          if (!StringUtils.isBlank(hql)) {
            result = systemDAO.createHQLQuery(hql).uniqueResult();
            
            if (result == null)
              throw new PyramusRuntimeException(new Exception("storeVariable hql=\"" + hql + "\" returned null"));
          } else {
            Class variableClass;
            try {
              variableClass = Class.forName(storeElement.getAttribute("class"));
              Criteria criteriaQuery = systemDAO.createHibernateCriteria(variableClass);
            
              NodeList criteriaList = storeElement.getChildNodes();
              for (int i = 0; i < criteriaList.getLength(); i++) {
                if (criteriaList.item(i) instanceof Element) {
                  Element criteriaElement = (Element) criteriaList.item(i);
                  StoreVariableCriteria criteria = StoreVariableCriteria.getCriteria(criteriaElement.getNodeName());
                  switch (criteria) {
                    case Equals:
                      String property = criteriaElement.getAttribute("name");
                      String value = ((Text) criteriaElement.getFirstChild()).getData();
                      criteriaQuery.add(Restrictions.eq(property, value));
                    break;
                  }
                }
              }
              
              result = criteriaQuery.uniqueResult();
              
              if (result == null)
                throw new PyramusRuntimeException(new Exception("storeVariable class=\"" + variableClass.getName() + "\" returned null"));
            } catch (ClassNotFoundException e) {
              throw new PyramusRuntimeException(e);
            }  
          }
          
          System.out.println("Storing " + variableName);
          storeValue(variableName, getPojoId(result));
        }
      }
      
      NodeIterator entityIterator = XPathAPI.selectNodeIterator(initialDataDocument.getDocumentElement(), "entity");
      Node node;

      Map<Object, ClassMetadata> classMetaData = systemDAO.getHibernateClassMetadata();
      
      while ((node = entityIterator.nextNode()) != null) {
        if (node instanceof Element) {

          Element element = (Element) node;
          String entityPackageName = element.getAttribute("package");
          String entityClassName = element.getAttribute("class");
          String className = entityPackageName + '.' + entityClassName;
          
          if ((entities == null)||entities.contains(className)) {
            ClassMetadata metadata = classMetaData.get(className);
            Class<?> entityClass = metadata.getMappedClass(EntityMode.POJO);
         
            NodeIterator entryIterator = XPathAPI.selectNodeIterator(element, "e");
            Element entry;
            
            while ((entry = (Element) entryIterator.nextNode()) != null) {
              processEntryTag(null, entityClass, entry, null);
              System.out.println("  >> added entity " + className);
            }
          }
        }
      }
    } catch (TransformerException e) {
      throw new PyramusRuntimeException(e);
    } 
  }

  private Object processEntryTag(Object parent, Class<?> entityClass, Element entry, Element parentListElement) { 
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    System.out.println("Processing entity: " + entityClass.getName());
    try {
      
      Constructor<?> defaultConstructor = entityClass.getDeclaredConstructor(new Class[] {});
      defaultConstructor.setAccessible(true);
      Object pojo = defaultConstructor.newInstance(new Object[] {});
      
      NamedNodeMap attributes = entry.getAttributes();
      for (int i = 0, len = attributes.getLength(); i < len; i++) {
        Node attribute = attributes.item(i);
        String propertyName = attribute.getNodeName();
        
        if (!"storeVariable".equals(propertyName)) {
          String propertyValue = attribute.getNodeValue();
          if (propertyValue.startsWith("{") && propertyValue.endsWith("}")) {
            propertyValue = String.valueOf(getStoredValue(propertyValue.substring(1, propertyValue.length() - 1)));
          }
          
          System.out.println("    >> property: " + propertyName + " to " + propertyValue);
          setValue(pojo, propertyName, propertyValue);
        }
      }
  
      NodeList entryChildren = entry.getChildNodes();
      for (int j = 0, len = entryChildren.getLength(); j < len; j++) {
        Node n = entryChildren.item(j);
        if (n instanceof Element) {
          Element element = (Element) n;
          String nodeName = element.getTagName();
          EntityDirective entityDirective = EntityDirective.getDirective(nodeName);
          if (entityDirective == null)
            throw new PyramusRuntimeException(new Exception("Unknown entity directive '" + nodeName + "'"));
          
          switch (entityDirective) {
            case Map:
              String mapName = element.getAttribute("name");
              String methodName = element.getAttribute("method");
              NodeIterator itemIterator = XPathAPI.selectNodeIterator(element, "item");
              Element itemElement;
              while ((itemElement = (Element) itemIterator.nextNode()) != null) {
                Element keyElement = (Element) XPathAPI.selectSingleNode(itemElement, "key");
                Element valueElement = (Element) XPathAPI.selectSingleNode(itemElement, "value");
                if (keyElement == null||valueElement == null)
                  throw new PyramusRuntimeException(new Exception("Malformed map item"));
                
                String keyValue = ((Text) keyElement.getFirstChild()).getData();
                String valueValue = ((Text) valueElement.getFirstChild()).getData();
                
                Field mapField = getField(pojo, mapName);
                ParameterizedType genericType = (ParameterizedType) mapField.getGenericType();
                Class<?> mapKeyTypeClass = (Class<?>) genericType.getActualTypeArguments()[0];
                Class<?> mapValueTypeClass = (Class<?>) genericType.getActualTypeArguments()[1];
                
                Object key;
                Object value;
                
                if (!isHibernateClass(mapKeyTypeClass)) {
                  ValueInterpreter valueInterpreter = interpreters.get(mapKeyTypeClass);
                  if (valueInterpreter != null)
                    key = valueInterpreter.interpret(keyValue);
                  else
                    throw new PyramusRuntimeException(new Exception("Value interpreter for " + mapKeyTypeClass + " is not implemented yet"));
                } else {
                  key = getPojo(mapKeyTypeClass, keyValue);
                }
                
                if (!isHibernateClass(mapValueTypeClass)) {
                  ValueInterpreter valueInterpreter = interpreters.get(mapValueTypeClass);
                  if (valueInterpreter != null)
                    value = valueInterpreter.interpret(valueValue);
                  else
                    throw new PyramusRuntimeException(new Exception("Value interpreter for " + mapValueTypeClass + " is not implemented yet"));
                } else {
                  value = getPojo(mapValueTypeClass, valueValue);
                }
                
                Class<?>[] params = {key.getClass(), value.getClass()};
                Object[] paramValues = {key, value}; 
                Method method = getMethod(pojo, methodName, params);
                method.invoke(pojo, paramValues);
              }
            break;
            case List:
              String listName = element.getAttribute("name");
              
              Field listField = getField(pojo, listName);
              ParameterizedType genericType = (ParameterizedType) listField.getGenericType();
              Class<?> listTypeClass = (Class<?>) genericType.getActualTypeArguments()[0];
              NodeIterator listEntryIterator = XPathAPI.selectNodeIterator(element, "e");
              
              Element listEntry;
              while ((listEntry = (Element) listEntryIterator.nextNode()) != null) {
                Object listEntity = processEntryTag(pojo, listTypeClass, listEntry, element);
                System.out.println("  >> added list entity " + listEntity.getClass().toString());
              }
            break;
            case Join:
              String className = element.getAttribute("class");
              
              String idField = ((Text) element.getFirstChild()).getData();
              
              Field joinField = getField(pojo, element.getAttribute("name"));
              
              if ("PARENT".equals(idField)) {
                String parentListMethod = parentListElement.getAttribute("method");
                AccessType parentListAccessType = AccessType.Field;
                
                if (!StringUtils.isEmpty(parentListMethod))
                  parentListAccessType = AccessType.Method;
                
                if (parentListAccessType == AccessType.Method) {
                  Class<?>[] params = {pojo.getClass()};
                  Method method = getMethod(parent, parentListMethod, params);
                  method.invoke(parent, pojo);
                } else {
                  setFieldValue(pojo, joinField, parent);
                }
              } else {
                Object joinedPojo = getPojo(className, idField);
                if (joinedPojo != null) {
                  setFieldValue(pojo, joinField, joinedPojo);
                } else {
                  throw new PyramusRuntimeException(new Exception(className + " #" + idField + " could not be found"));
                }
              }
            break;
          }
        }
      }
      
     
      Set<ConstraintViolation<Object>> constraintViolations = systemDAO.validateEntity(pojo);
      
	  if (constraintViolations.size() == 0) {
	    systemDAO.getHibernateSession().saveOrUpdate(pojo);
	  } else {
	    String message = "";
	    for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
	      message += constraintViolation.getMessage() + '\n';
	    }
	    
	    throw new PyramusRuntimeException(new Exception("Validation failure: " + message));
	  }
  
      Long id = getPojoId(pojo);
      
      if (id != null) {
        String storeVariable = entry.getAttribute("storeVariable");
        if (!StringUtils.isBlank(storeVariable)) {
          storeValue(storeVariable, id);
          System.out.println("  >> # " + id + " stored as " + storeVariable);
        }
      }
      
      return id;
    } catch (Exception e) {
      throw new PyramusRuntimeException(new Exception("Error while processing entity: " + entityClass.getName() + " " + e.getMessage(), e));
    }
  } 
  
  private Long getPojoId(Object pojo) {
    if (pojo != null) {
      try {
        Method getIdMethod = getMethod(pojo, "getId", new Class<?>[] {});
        if (getIdMethod != null) {
          return (Long) getIdMethod.invoke(pojo, new Object[] {});
        }
      } catch (Exception e) { 
        throw new PyramusRuntimeException(new Exception("getId failed for " + pojo.getClass().getName()));
      }
    }

    return null;
  }
  
  private Object getPojo(Class<?> clazz, String identifier) throws ClassNotFoundException {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    Long id;
    if (identifier.startsWith("{") && identifier.endsWith("}")) {
      id = getStoredValue(identifier.substring(1, identifier.length() - 1));
      if (id == null)
        throw new PyramusRuntimeException(new Exception("Could not resolve: " + identifier));
    } else  {
      id = NumberUtils.createLong(identifier);
    }
    
    return systemDAO.getHibernateSession().load(clazz, id);
  }
  
  private Object getPojo(String className, String identifier) throws ClassNotFoundException {
    Class<?> pojoClass = Class.forName(className);
    return getPojo(pojoClass, identifier);
  }
  
  private void setValue(Object pojo, String property, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,  IllegalAccessException {
    Field field = getField(pojo, property);
    Class<?> fieldType = field.getType();

    ValueInterpreter valueInterpreter = interpreters.get(fieldType);

    if (valueInterpreter != null)
      setFieldValue(pojo, field, valueInterpreter.interpret(value));
    else
      throw new PyramusRuntimeException(new Exception("Value interpreter for " + fieldType + " is not implemented yet"));
  }
  
  private Field getField(Object pojo, String property) {
    Field field = null;
    
    Class<?> cClass = pojo.getClass();
    while (cClass != null && field == null) {
      try {
        field = cClass.getDeclaredField(property);
      } catch (NoSuchFieldException nsf) {
        cClass = cClass.getSuperclass();
      }
    }
    
    return field;
  }
  
  private Method getMethod(Object pojo, String methodName, Class<?>[] params) {
    Method method = null;
    
    Class<?> cClass = pojo.getClass();
    while (cClass != null && method == null) {
      try {
        method = cClass.getDeclaredMethod(methodName, params);
      } catch (NoSuchMethodException nsf) {
        cClass = cClass.getSuperclass();
      }
    }
    
    return method;
  }
  
  private boolean isHibernateClass (String className) {
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    return systemDAO.getHibernateClassMetadata().containsKey(className);
  }
  
  private boolean isHibernateClass (Class<?> clazz) {
    return isHibernateClass(clazz.getName());
  }
  
  private Long getStoredValue(String name) {
    return storedValues.get(name);
  }
  
  private void storeValue(String name, Long id) {
    storedValues.put(name, id);
  } 
  
  private enum AccessType {
    Field,
    Method
  }
  
  private Map<String, Long> storedValues = new HashMap<String, Long>();

  private void setFieldValue(Object pojo, Field field, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
      IllegalAccessException {
    field.setAccessible(true);
    field.set(pojo, value);
  }

  private interface ValueInterpreter {
    Object interpret(Object o);
  }
  
  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private static Map<Class<?>, ValueInterpreter> interpreters = new HashMap<Class<?>, ValueInterpreter>();

  static {
    interpreters.put(String.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return o;
      }
    });

    interpreters.put(Long.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return NumberUtils.createLong((String) o);
      }
    });

    interpreters.put(Double.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return NumberUtils.createDouble((String) o);
      }
    });

    interpreters.put(Boolean.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return "true".equals(o) ? Boolean.TRUE : Boolean.FALSE;
      }
    });

    interpreters.put(Date.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return new Date("NOW".equals(o) ? System.currentTimeMillis() : NumberUtils.createLong((String) o));
      }
    });

    interpreters.put(UserRole.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return UserRole.getRole(NumberUtils.createInteger((String) o));
      }
    });
    
    interpreters.put(Role.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return Role.getRole(NumberUtils.createInteger((String) o));
      }
    });
    
    interpreters.put(MonetaryAmount.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return new MonetaryAmount(NumberUtils.createDouble((String) o));
      }
    });

    interpreters.put(Sex.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return "male".equals(o) ? Sex.MALE : Sex.FEMALE;
      }
    });

    interpreters.put(ProjectModuleOptionality.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return ProjectModuleOptionality.getOptionality(NumberUtils.createInteger((String) o));
      }
    });
    
    interpreters.put(VariableType.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return VariableType.getType(NumberUtils.createInteger((String) o));
      }
    });

    interpreters.put(StudentContactLogEntryType.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return StudentContactLogEntryType.getType(NumberUtils.createInteger((String) o));
      }
    });
  }
  
  public enum EntityDirective {
    Join ("j"),
    List ("list"),
    Map  ("map");
    
    private EntityDirective(String name) {
      this.name = name;
    }
    
    private String name;
    
    public static EntityDirective getDirective(String name) {
      for (EntityDirective entityDirectiveNode : values()) {
        if (entityDirectiveNode.name.equals(name))
          return entityDirectiveNode;
      }
      
      return null;
    } 
  }
  
  public enum StoreVariableCriteria {
    Equals ("eq");
    
    private StoreVariableCriteria(String name) {
      this.name = name;
    }
    
    private String name;
    
    public static StoreVariableCriteria getCriteria(String name) {
      for (StoreVariableCriteria criteria : values()) {
        if (criteria.name.equals(name))
          return criteria;
      }
      
      return null;
    } 
  }
}
