package fi.pyramus;

public enum StatusCode {
  
  /* Generic exceptions  (0000 - 0999) */
  
  /**
   * Undefined error.
   */
  UNDEFINED   (-1),
  /**
   * Request handled successfully.
   */
  OK            (0),
  /**
   * An entity has been modified by two people at the same time.
   */
  CONCURRENT_MODIFICATION (1), 
  /**
   * Entity validation failed
   */
  VALIDATION_FAILURE (2),
  
  /* Course exceptions   (1000 - 1999) */ 

  /* Module exceptions   (2000 - 2999) */ 

  /* Resource exceptions (3000 - 3999) */ 

  /* Student exceptions  (4000 - 4999)  */ 

  /* System exceptions   (5000 - 5999)  */ 

  /* User exception      (6000 - 6999)  */ 
  
  /**
   * User tries to login when (s)he is already logged in.
   */
  ALREADY_LOGGED_IN  (6000), 
  /**
   * Invalid credentials
   */
  UNAUTHORIZED       (6001), 
  /**
   * When password has to be entered twice, user fails to do so :P
   */
  PASSWORD_MISMATCH  (6002),
  /**
   * Program calls an operation that is unsupported for current authentication strategy
   */
  UNSUPPORTED_AUTHENTICATION_OPERATION (6003),
  /**
   * External authentication service has accepted login but local user is missing
   */
  LOCAL_USER_MISSING (6004),
  /**
   * Anonymous user tries to use feature that is not authorized for anonymous users
   */
   NOT_LOGGED_IN (6005),
   /**
    * User does not have enough permissions to perform requested operation
    */
   PERMISSION_DENIED (6006);
  
  /**
   * Constructor specifying the status code.
   * 
   * @param value The status code
   */
  private StatusCode(int value) {
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