package fi.pyramus.logging;

import org.apache.log4j.Logger;

public class Logging {
  
  public static void logException(Exception e) {
    logger.error(e);
  } 
  
  public static void logDebug(String msg) {
    logger.debug(msg);
  }
  
  public static void logInfo(String msg) {
    logger.info(msg);
  }
  
  public static void logError(String msg) {
    logger.error(msg);
  } 
  
  private static Logger logger = Logger.getLogger("pyramus");
}
