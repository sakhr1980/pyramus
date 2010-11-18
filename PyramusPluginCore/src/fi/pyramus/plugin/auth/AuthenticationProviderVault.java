package fi.pyramus.plugin.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.plugin.PluginDescriptor;
import fi.pyramus.plugin.PluginVault;

/**
 * The class responsible of managing the authorization providers of the application.
 */
public class AuthenticationProviderVault {
  
  /**
   * Returns a singleton instance of this class.
   * 
   * @return A singleton instance of this class
   */
  public static AuthenticationProviderVault getInstance() {
    return instance;
  }
  
 
  /**
   * Returns a collection of all authorization providers registered to this class.
   * 
   * @return A collection of all authorization providers registered to this class
   */
  public Collection<AuthenticationProvider> getAuthenticationProviders() {
    return authenticationProviders.values();
  }
  
  public List<InternalAuthenticationProvider> getInternalAuthorizationProviders() {
    List<InternalAuthenticationProvider> internalAuthorizationProviders = new ArrayList<InternalAuthenticationProvider>();
    for (AuthenticationProvider authorizationProvider : getAuthenticationProviders()) {
      if (authorizationProvider instanceof InternalAuthenticationProvider)
        internalAuthorizationProviders.add((InternalAuthenticationProvider) authorizationProvider);
    }
    return internalAuthorizationProviders;
  }
  
  public List<ExternalAuthenticationProvider> getExternalAuthorizationProviders() {
    List<ExternalAuthenticationProvider> externalAuthorizationProviders = new ArrayList<ExternalAuthenticationProvider>();
    for (AuthenticationProvider authorizationProvider : getAuthenticationProviders()) {
      if (authorizationProvider instanceof ExternalAuthenticationProvider)
        externalAuthorizationProviders.add((ExternalAuthenticationProvider) authorizationProvider);
    }
    
    return externalAuthorizationProviders;
  }
  
  public static Map<String, Class<AuthenticationProvider>> getAuthenticationProviderClasses() {
    return authenticationProviderClasses;
  }
  
  public boolean hasExternalStrategies() {
    return getExternalAuthorizationProviders().size() > 0;
  }
  
  public boolean hasInternalStrategies() {
    return getInternalAuthorizationProviders().size() > 0;
  }
  
  /**
   * Returns the authorization provider corresponding to the given name. If it doesn't exists, returns <code>null</code>.
   * 
   * @param name The authorization provider name
   * 
   * @return The authorization provider corresponding to the given name, or <code>null</code> if not found
   */
  public AuthenticationProvider getAuthorizationProvider(String name) {
    return authenticationProviders.get(name);
  }
  
  /**
    Registers the various authorization providers to this class.
  **/
  public void initializeStrategies() {
    String strategiesConf = System.getProperty("authentication.enabledStrategies");
    if ((strategiesConf == null)||("".equals(strategiesConf)))
      strategiesConf = "internal";
    
    String[] strategies = strategiesConf.split(",");
    for (String strategyName : strategies) {
      AuthenticationProvider provider;
      try {
        provider = authenticationProviderClasses.get(strategyName.trim()).newInstance();
        registerAuthorizationProvider(provider);
      } catch (InstantiationException e) {
        throw new PyramusRuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new PyramusRuntimeException(e);
      }
    }
  }
  
  /**
   * Registers an authorization provider to this class.
   * 
   * @param authorizationProvider The authorization provider to be registered
   */
  private void registerAuthorizationProvider(AuthenticationProvider authorizationProvider) {
    authenticationProviders.put(authorizationProvider.getName(), authorizationProvider);  
  }
  
  /** Map containing authorization provider names as keys and the providers themselves as values */ 
  private Map<String, AuthenticationProvider> authenticationProviders = new HashMap<String, AuthenticationProvider>();
  
  @SuppressWarnings("unchecked")
  public static void registerAuthorizationProviderClass(String name, Class<?> class1) {
    authenticationProviderClasses.put(name, (Class<AuthenticationProvider>) class1);
  }
  
  /** The singleton instance of this class */
  private static AuthenticationProviderVault instance = new AuthenticationProviderVault();
  /** All registered authorization provider classes **/
  private static Map<String, Class<AuthenticationProvider>> authenticationProviderClasses = new HashMap<String, Class<AuthenticationProvider>>();
  
  static {
    List<PluginDescriptor> plugins = PluginVault.getInstance().getPlugins();
    for (PluginDescriptor plugin : plugins) {
      if (plugin.getAuthenticationProviders() != null) {
        Map<String, Class<?>> authenticationProviders = plugin.getAuthenticationProviders();
        for (String authenticationProviderName : authenticationProviders.keySet()) {
          registerAuthorizationProviderClass(authenticationProviderName, authenticationProviders.get(authenticationProviderName));
        }
      }
    }
  }
}
