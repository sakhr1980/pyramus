package fi.pyramus.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader {

  public JarLoader(ClassLoader parentClassLoader) {
    this.parentClassLoader = parentClassLoader;
  }

  public JarLoader() {
    this(JarLoader.class.getClassLoader());
  }

  public URLClassLoader getPluginsClassLoader() {
    if (classLoader == null) {
      classLoader = new URLClassLoader(new URL[] {}, parentClassLoader);
    }

    return classLoader;
  }

  public void loadJar(File jarFile) {
    try {
      loadJar(jarFile.toURI().toURL());
    } catch (MalformedURLException e) {
    }
  }

  public void loadJar(URL jarUrl) {
    if (isJarLoaded(jarUrl))
      return;

    try {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
      method.setAccessible(true);
      method.invoke(getPluginsClassLoader(), new Object[] { jarUrl });
    } catch (SecurityException e) {
      throw new PluginManagerException(e);
    } catch (NoSuchMethodException e) {
      throw new PluginManagerException(e);
    } catch (IllegalArgumentException e) {
      throw new PluginManagerException(e);
    } catch (IllegalAccessException e) {
      throw new PluginManagerException(e);
    } catch (InvocationTargetException e) {
      throw new PluginManagerException(e);
    }
  }
  
  public boolean isJarLoaded(File jarFile) {
    try {
      return isJarLoaded(jarFile.toURI().toURL());
    } catch (MalformedURLException e) {
    }
    
    return false;
  }

  public boolean isJarLoaded(URL jarUrl) {
    for (URL url : getPluginsClassLoader().getURLs()) {
      if (url.equals(jarUrl))
        return true;
    }
    
    return false;
  }

  private ClassLoader parentClassLoader = null;
  private URLClassLoader classLoader = null;
}
