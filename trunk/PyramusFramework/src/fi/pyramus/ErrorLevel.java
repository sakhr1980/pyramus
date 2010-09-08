package fi.pyramus;

/**
 * The error levels of the application.
 */
public enum ErrorLevel {
  
  /**
   * Minor error, e.g. the user having left a mandatory field empty.  
   */
  INFORMATION (1),
  /**
   * Moderate error, e.g. the user trying to access an unauthorized page. 
   */
  WARNING     (2),
  /**
   * Major error, reserved for unexpected situations that are still handled in a graceful manner. 
   */
  ERROR       (3),
  /**
   * Fatal error, most likely related to erroneous code or an unrecoverable situation.
   */
  CRITICAL    (4);
  
  /**
   * Constructor specifying the error level.
   * 
   * @param value The error level
   */
  private ErrorLevel(int value) {
    this.value = value;
  }
  
  /**
   * Returns the value of this enumeration as an integer.
   * 
   * @return The value of this enumeration as an integer
   */
  public int getValue() {
    return value;
  }
  
  /** The value of this enumeration */
  private int value;
}
