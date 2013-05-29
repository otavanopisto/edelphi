package fi.internetix.edelphi.auth;

/**
 * Exception class for errors during authentication.
 */
public class AuthenticationRuntimeException extends RuntimeException {

  public AuthenticationRuntimeException(Throwable cause) {
    super(cause);
  }
  
  /**
   * 
   */
  private static final long serialVersionUID = -2598554724783382055L;
}
