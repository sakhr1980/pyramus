package fi.pyramus.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.misc.Service;

public class PluginVault {
  
  public static final PluginVault getInstance() {
    return instance;
  }
  
  private static final PluginVault instance = new PluginVault();
  
  public synchronized List<PluginDescriptor> getPlugins() {
    return plugins;
  }
  
  private synchronized void registerPlugin(PluginDescriptor plugin) {
    plugins.add(plugin);
  }
  
  private List<PluginDescriptor> plugins = new ArrayList<PluginDescriptor>();
  
  @SuppressWarnings("unchecked")
  private static final void registerPlugins() {
    Iterator<PluginDescriptor> pluginDescriptors = Service.providers(PluginDescriptor.class);
    while (pluginDescriptors.hasNext()) {
      PluginDescriptor pluginDescriptor = pluginDescriptors.next();
      getInstance().registerPlugin(pluginDescriptor);
    }
  }
  
  static {
    registerPlugins();
  }
}
