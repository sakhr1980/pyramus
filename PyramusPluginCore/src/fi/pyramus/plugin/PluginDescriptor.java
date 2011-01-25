package fi.pyramus.plugin;

import java.util.Map;

public interface PluginDescriptor {

  public String getName();
  
  public Map<String, Class<?>> getPageRequestControllers();
  public Map<String, Class<?>> getJSONRequestControllers();
  public Map<String, Class<?>> getBinaryRequestControllers();
  public Map<String, Class<?>> getPageHookControllers();
  public Map<String, Class<?>> getAuthenticationProviders();
  public String getMessagesBundlePath();
}