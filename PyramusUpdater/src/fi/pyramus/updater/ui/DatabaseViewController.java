package fi.pyramus.updater.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.dialect.Dialect;

import fi.pyramus.updater.core.Preferences;
import fi.pyramus.updater.core.UpdaterException;

public class DatabaseViewController {

  public DatabaseViewController() {
    view = new DatabaseView(this);
    view.showUi();
    readPreferences();
  }
  
  public Connection getConnection() {
    return databaseConnection;
  }
  
  private Dialect getDialect() {
    Dialect dialect = null;
    Class<?> dialectClass = view.getSelectedDatabase().getDialect();
    if (dialectClass != null) {
      try {
        dialect = (Dialect) dialectClass.newInstance();
      } catch (InstantiationException e) {
        logger.error("Error occured while initializing dialect. " + e.getMessage());
        throw new UpdaterException(e);
      } catch (IllegalAccessException e) {
        logger.error("Error occured while initializing dialect. " + e.getMessage());
        throw new UpdaterException(e);
      }
    }
    
    return dialect;
  }
  
  public void connectToDatabase() {
    try {
      databaseConnection = DriverManager.getConnection(view.getDatabaseUrl(), view.getDatabaseUsername(), view.getDatabasePassword());
      databaseConnection.setAutoCommit(false);
    } catch (SQLException e) {
      logger.error("Could not connect to database: " + e.getMessage());
      throw new UpdaterException(e);
    }
    
    this.view.dispose();
    openUpdaterView();
    
    logger.info("Connection to database established");
  }
  
  private void openUpdaterView() {
    new UpdaterViewController(databaseConnection, getDialect());
  }
  
  private void readPreferences() {
    view.setDatabaseVendor(Preferences.get("database.vendor"));
    view.setDatabaseUrl(Preferences.get("database.url"));
    view.setDatabaseUsername(Preferences.get("database.username"));
    view.setDatabasePassword(Preferences.get("database.password"));
  }
  
  private Logger logger = Logger.getRootLogger();
  private DatabaseView view;
  private Connection databaseConnection;
}
