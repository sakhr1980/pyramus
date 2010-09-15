package fi.pyramus;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import fi.pyramus.updater.core.UpdaterException;
import fi.pyramus.updater.ui.DatabaseViewController;

public class Updater {

  public static void main(String[] args) {
    loadDrivers();

    new DatabaseViewController();
  }

  private static void loadDrivers() {
    try {
      File driversFolder = new File("drivers");
      File[] files = driversFolder.listFiles(new FileFilter() {
        @Override
        public boolean accept(File pathname) {
          return pathname.getName().endsWith(".jar");
        }
      });
      
      for (int i = 0, l = files.length; i < l; i++) {
        addDriverJar(files[i].toURI().toURL());
      }

    } catch (MalformedURLException e) {
      throw new UpdaterException(e);
    }
  }
  
  private static void addDriverJar(URL jarUrl) {
    URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    try {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class});
      method.setAccessible(true);
      method.invoke(classLoader, new Object[] {jarUrl});
    } catch (SecurityException e) {
      throw new UpdaterException(e);
    } catch (NoSuchMethodException e) {
      throw new UpdaterException(e);
    } catch (IllegalArgumentException e) {
      throw new UpdaterException(e);
    } catch (IllegalAccessException e) {
      throw new UpdaterException(e);
    } catch (InvocationTargetException e) {
      throw new UpdaterException(e);
    }
  }
}
