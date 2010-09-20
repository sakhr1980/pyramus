package fi.pyramus.json.locale;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.JavaScriptMessages;
import fi.pyramus.json.JSONRequestController;

public class GetJavaScriptLocaleJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    Map<String, String> localeStrings = new HashMap<String, String>();
    
    ResourceBundle resourceBundle = JavaScriptMessages.getInstance().getResourceBundle(requestContext.getRequest().getLocale());
    Enumeration<String> keys = resourceBundle.getKeys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      String value = resourceBundle.getString(key);
      localeStrings.put(key, value.trim());
    }
    
    requestContext.addResponseParameter("localeStrings", localeStrings);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }
}

