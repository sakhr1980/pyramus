package fi.pyramus.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageHookVault {
  
  public static final PageHookVault getInstance() {
    return pageHookVault;
  }
  
  private static final PageHookVault pageHookVault = new PageHookVault();

  public synchronized List<PageHookController> getPageHooks(String hookName) {
    return pageHooks.get(hookName);
  }
  
  private synchronized void registerPageHook(String hookName, Class<?> hookControllerClass) {
    List<PageHookController> hooks = pageHooks.get(hookName);
    
    if (hooks == null) {
      hooks = new ArrayList<PageHookController>();
      pageHooks.put(hookName, hooks);
    }
    
    try {
      hooks.add((PageHookController) hookControllerClass.newInstance());
    } catch (InstantiationException e) {
      throw new PluginManagerException(e);
    } catch (IllegalAccessException e) {
      throw new PluginManagerException(e);
    }
  }
  
  private Map<String, List<PageHookController>> pageHooks = new HashMap<String, List<PageHookController>>();

  static {
    List<PluginDescriptor> plugins = PluginManager.getInstance().getPlugins();
    for (PluginDescriptor plugin : plugins) {
      if (plugin.getPageHookControllers() != null) {
        Map<String, Class<?>> pageHooks = plugin.getPageHookControllers();
        for (String pageHookName : pageHooks.keySet()) {
          getInstance().registerPageHook(pageHookName, pageHooks.get(pageHookName));
        }
      }
    }
  }
}