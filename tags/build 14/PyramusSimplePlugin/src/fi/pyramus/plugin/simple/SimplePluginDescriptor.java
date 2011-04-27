package fi.pyramus.plugin.simple;

import java.util.HashMap;
import java.util.Map;

import fi.pyramus.plugin.PluginDescriptor;
import fi.pyramus.plugin.simple.auth.SimpleAuthenticationProvider;
import fi.pyramus.plugin.simple.hooks.EditCourseSimpleTabHook;
import fi.pyramus.plugin.simple.hooks.EditCourseSimpleTabLabelHook;
import fi.pyramus.plugin.simple.views.LoggedUserInfoViewController;

public class SimplePluginDescriptor implements PluginDescriptor {
  
  @Override
  public Map<String, Class<?>> getBinaryRequestControllers() {
    return null;
  }
  
  @Override
  public Map<String, Class<?>> getJSONRequestControllers() {
    return null;
  }
  
  @Override
  public String getName() {
    return "simple";
  }
  
  @Override
  public Map<String, Class<?>> getPageHookControllers() {
    Map<String, Class<?>> hookControllers = new HashMap<String, Class<?>>();
    
    hookControllers.put("students.editStudent.tabs", EditCourseSimpleTabHook.class);
    hookControllers.put("students.editStudent.tabLabels", EditCourseSimpleTabLabelHook.class);

    return hookControllers;
  }
  
  @Override
  public Map<String, Class<?>> getPageRequestControllers() {
    Map<String, Class<?>> viewControllers = new HashMap<String, Class<?>>();
    
    viewControllers.put("users/loggeduserinfo", LoggedUserInfoViewController.class);
    
    return viewControllers;
  }
  
  public Map<String, Class<?>> getAuthenticationProviders() {
    Map<String, Class<?>> authenticationProviders = new HashMap<String, Class<?>>();
    
    authenticationProviders.put("simple", SimpleAuthenticationProvider.class);
    
    return authenticationProviders;
  }
  
  @Override
  public String getMessagesBundlePath() {
    return "fi.pyramus.plugin.simple.messages";
  }
}
