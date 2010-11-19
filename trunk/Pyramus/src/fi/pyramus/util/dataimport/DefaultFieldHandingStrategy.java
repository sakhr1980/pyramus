package fi.pyramus.util.dataimport;

import java.lang.reflect.Constructor;

import fi.pyramus.ErrorLevel;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;

@SuppressWarnings("rawtypes")
public class DefaultFieldHandingStrategy implements FieldHandlingStrategy {

  private Class entityClass;
  private String fieldName = null;

  public DefaultFieldHandingStrategy(Class entityClass) {
    this.entityClass = entityClass;
  }

  public DefaultFieldHandingStrategy(Class entityClass, String fieldName) {
    this.entityClass = entityClass;
    this.fieldName  = fieldName;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void handleField(String fieldName, Object fieldValue, DataImportContext context) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
    Object entity = context.getEntity(entityClass);

    if (entity == null) {
      Constructor<?> defaultConstructor;
      try {
        defaultConstructor = entityClass.getDeclaredConstructor(new Class[] {});
        defaultConstructor.setAccessible(true);
        entity = defaultConstructor.newInstance(new Object[] {});
        context.addEntity(entityClass, entity);
      } catch (Exception e) {
        throw new PyramusRuntimeException(ErrorLevel.CRITICAL, StatusCode.OK, "Couldn't instantiate entityClass");
      }
    }
  
    DataImportUtils.setValue(entity, this.fieldName != null ? this.fieldName : fieldName, fieldValue);
  }

}
