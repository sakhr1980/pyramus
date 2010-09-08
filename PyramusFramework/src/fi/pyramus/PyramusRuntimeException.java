package fi.pyramus;

/**
 * The base exception class for all gracefully handled error situations in the application.
 */
public class PyramusRuntimeException extends RuntimeException {

  /**
   * Constructor specifying an unhandled exception. This signals a critical, undefined error
   * situation.
   * 
   * @param e The occured exception
   */
  public PyramusRuntimeException(Exception e) {
    this(ErrorLevel.CRITICAL, StatusCode.UNDEFINED, e.getMessage());
  }

  /**
   * Class constructor specifying the severity, code, and message of the exception.
   * 
   * @param level The severity of the error
   * @param statusCode The status code of the error
   * @param message The message of the error
   */
  public PyramusRuntimeException(ErrorLevel level, StatusCode statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
    this.level = level;
  }

  /**
   * Returns the error level of this exception.
   * 
   * @return The error level of this exception
   */
  public ErrorLevel getLevel() {
    return level;
  }

  /**
   * Returns the status code of this exception.
   * 
   * @return The status code of this exception
   */
  public StatusCode getStatusCode() {
    return statusCode;
  }

  /** The status code of this exception */
  private StatusCode statusCode;

  /** The error level of this exception */
  private ErrorLevel level;

  /** The serial version UID of the class */
  private static final long serialVersionUID = -5069996150452823136L;

}
