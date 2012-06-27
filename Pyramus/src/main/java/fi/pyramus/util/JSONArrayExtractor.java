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
        String attributeValue;
        try {
          attributeValue = attributeMethod.invoke(sourceObject, params).toString();
        } catch (IllegalAccessException e) {
          attributeValue = "";
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          attributeValue = "";
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          attributeValue = "";
          e.printStackTrace();
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
