package fi.pyramus.util.dataimport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.persistence.usertypes.MonetaryAmount;
import fi.pyramus.persistence.usertypes.ProjectModuleOptionality;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.persistence.usertypes.StudentContactLogEntryType;
import fi.pyramus.persistence.usertypes.VariableType;

public class DataImportUtils {

  /**
   * 
   * @param pojo
   * @param methodName
   * @param params
   * @return
   */
  public static Method getMethod(Object pojo, String methodName, Class<?>[] params) {
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

  /**
   * 
   * @param pojo
   * @param property
   * @param value
   * @throws SecurityException
   * @throws NoSuchFieldException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public static void setValue(Object pojo, String property, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,  IllegalAccessException {
    Field field = getField(pojo, property);
    Class<?> fieldType = field.getType();

    ValueInterpreter valueInterpreter = DataImportUtils.getValueInterpreter(fieldType);

    if (valueInterpreter != null)
      setFieldValue(pojo, field, valueInterpreter.interpret(value));
    else
      throw new PyramusRuntimeException(new Exception("Value interpreter for " + fieldType + " is not implemented yet"));
  }

  /**
   * 
   * @param pojo
   * @param property
   * @return
   */
  public static Field getField(Object pojo, String property) {
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
  
  /**
   * 
   * @param pojo
   * @param field
   * @param value
   * @throws SecurityException
   * @throws NoSuchFieldException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public static void setFieldValue(Object pojo, Field field, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
      IllegalAccessException {
    field.setAccessible(true);
    field.set(pojo, value);
  }
  
  /**
   * Returns Value Interpreter for given type
   * 
   * @param fieldType
   * @return
   */
  public static ValueInterpreter getValueInterpreter(Class<?> fieldType) {
    return interpreters.get(fieldType);
  }

  
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
        if ("NOW".equals(o))
          return System.currentTimeMillis();
        
        String s = (String) o;
        
        if (s.contains("-")) {
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          try {
            return df.parse(s);
          } catch (ParseException e) {
            e.printStackTrace();
            throw new PyramusRuntimeException(e);
          }
        }

        if (s.contains(".")) {
          DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
          try {
            return df.parse(s);
          } catch (ParseException e) {
            e.printStackTrace();
            throw new PyramusRuntimeException(e);
          }
        }
        
        return new Date(NumberUtils.createLong(s));
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
    
    interpreters.put(Locale.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return new Locale((String) o);
      }
    });    

    interpreters.put(Integer.class, new ValueInterpreter() {
      public Object interpret(Object o) {
        return NumberUtils.createInteger((String) o);
      }
    });    
  }
  
}
