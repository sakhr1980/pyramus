package fi.pyramus.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


import net.sf.json.*;

/** A selective Java collection to <code>JSONArray</code> converter.
 * <code>JSONArrayExtractor</code> converts a <code>List</code>
 * of beans containing a given set of properties into
 * a <code>JSONArray</code> that contains <code>JSONObject</code>s
 * with the same properties, converted appropriately, using <code>JSONObject</code>'s
 * conversion rules, with the exception that properties containing <code>null</code>s
 * are skipped and so don't end up in the converted object.
 * Usage:
 * <pre>
 *   List&lt;Address&gt; addresses = school.getContactInfo().getAddresses();
 *   JSONArray jaAddresses = new JSONArrayExtractor("id",
 *                                                  "name",
 *                                                  "streetAddress",
 *                                                  "postalCode",
 *                                                  "city",
 *                                                  "country").extract(addresses); 
 * </pre> 
 * @author ilmo.euro@otavanopisto.fi
 */
public class JSONArrayExtractor {
  
  private List<String> attributeNames;
  
  /** Creates a new Java collection to <code>JSONArray</code> converter
   * that converts no properties, just emits empty <code>JSONObject</code>s.
   */
  public JSONArrayExtractor() {
    attributeNames = new ArrayList<String>();
  }
  
  /** Creates a new Java collection to <code>JSONArray</code> converter
   * that converts a given set of properties of each object.
   * 
   * @param args The properties to convert.
   */
  public JSONArrayExtractor(String... args) {
    attributeNames = Arrays.asList(args);
  }
  
  /** Returns <code>sourceObjects</code> converted to a string containing JSON.
   * 
   * @param sourceObjects The objects to convert.
   * @return <code>sourceObject</code>, converted to a JSON string.
   */
  public <T> String extractString(List<T> sourceObjects) {
    return extract(sourceObjects).toString();
  }
  
  /** Returns <code>sourceObjects</code> converted to a JSON array.
   * 
   * @param sourceObjects The objects to convert.
   * @return <code>sourceObject</code>, converted to a JSON array.
   */
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
