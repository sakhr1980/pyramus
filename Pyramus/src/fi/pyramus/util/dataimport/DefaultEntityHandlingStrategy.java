package fi.pyramus.util.dataimport;

import java.util.Set;

import javax.validation.ConstraintViolation;

import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.SystemDAO;

@SuppressWarnings("rawtypes")
public class DefaultEntityHandlingStrategy implements EntityHandlingStrategy {

  private final String entityStrategy;

  public DefaultEntityHandlingStrategy(Class entityClass, String entityStrategy) {
    this.entityClass = entityClass;
    this.entityStrategy = entityStrategy;
  }
  
  @Override
  public void handleValue(String fieldName, Object value, DataImportContext context) 
      throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
    FieldHandlingStrategy fieldHandler = DataImportStrategyProvider.instance().getFieldHandler(entityStrategy, fieldName); 
    
    if (fieldHandler == null)
      throw new NoSuchFieldException("Entity handler cannot find required field handler: " + fieldName);
    
    fieldHandler.handleField(fieldName, value, context);
  }
  
  private Class entityClass;

  public void initializeContext(DataImportContext context) {
  }
  
  protected void bindEntities(DataImportContext context) {
  }
  
  @Override
  public void saveEntities(DataImportContext context) {
    bindEntities(context);
    
    Object[] entities = context.getEntities();
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();
    
    for (int i = 0; i < entities.length; i++) {
      Object entity = entities[i];
      
      Set<ConstraintViolation<Object>> constraintViolations = systemDAO.validateEntity(entity);

      if (constraintViolations.size() == 0) {
        systemDAO.getHibernateSession().saveOrUpdate(entity);
      } else {
        String message = "";
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
          message += constraintViolation.getMessage() + '\n';
        }
        
        throw new PyramusRuntimeException(new Exception("Validation failure: " + message));
      }
    }
  }

  @Override
  public Class getMainEntityClass() {
    return entityClass;
  }
}
