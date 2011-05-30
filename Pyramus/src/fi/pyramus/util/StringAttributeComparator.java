package fi.pyramus.util;

import java.lang.reflect.Method;
import java.util.Comparator;

import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.util.dataimport.DataImportUtils;

public class StringAttributeComparator implements Comparator<Object> {
  
  private final String attributeMethod;
  private final boolean ignoreCase;

  public StringAttributeComparator(String attributeMethod) {
    this(attributeMethod, false);
  }

  public StringAttributeComparator(String attributeMethod, boolean ignoreCase) {
    this.attributeMethod = attributeMethod;
    this.ignoreCase = ignoreCase;
  }
  
  public int compare(Object o1, Object o2) {
    try {
      Object[] params = new Object[] {};
      
      Method method = DataImportUtils.getMethod(o1, attributeMethod, null);
      String value1 = (String) method.invoke(o1, params);

      method = DataImportUtils.getMethod(o2, attributeMethod, null);
      String value2 = (String) method.invoke(o2, params);
      
      return value1 == null ? -1 : value2 == null ? 1 : 
        (ignoreCase ? value1.compareToIgnoreCase(value2) : value1.compareTo(value2));
    } catch (Exception e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
}