package fi.pyramus.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import net.sf.json.*;

public class JSONArrayExtractor {
  
  private List<String> attributeNames;
  
  public JSONArrayExtractor() {
    attributeNames = new ArrayList<String>();
  }
  
  public JSONArrayExtractor(String... args) {
    attributeNames = Arrays.asList(args);
  }
  
  public <T> String extractString(List<T> sourceObjects) {
    return extract(sourceObjects).toString();
  }
  
  public <T> JSONArray extract(List<T> sourceObjects) {
    JSONArray destObjects = new JSONArray();
    for (Object sourceObject : sourceObjects) {
      JSONObject destObject = new JSONObject();
      for (String attributeName : attributeNames) {
        Object[] params = new Object[] {};
        String methodName = "get" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1);
        Method attributeMethod = getMethod(sourceObject, methodName, null);
        Object attributeValue;
        // Nulls are deliberately skipped so that they are undefined in JS
        try {
          attributeValue = attributeMethod.invoke(sourceObject, params);
        } catch (NullPointerException e) {
          continue;
        } catch (IllegalAccessException e) {
          e.printStackTrace();
          continue;
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
          continue;
        } catch (InvocationTargetException e) {
          e.printStackTrace();
          continue;
        }
        destObject.put(attributeName, attributeValue);
      }
      destObjects.add(destObject);
    }
    
    return destObjects;
  }
  
  private static Method getMethod(Object pojo, String methodName, Class<?>[] params) {
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

}
