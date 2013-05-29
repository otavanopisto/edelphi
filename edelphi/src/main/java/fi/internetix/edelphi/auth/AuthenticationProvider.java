package fi.internetix.edelphi.auth;

import java.util.Locale;

import fi.internetix.edelphi.domainmodel.base.AuthSource;
import fi.internetix.smvc.controllers.RequestContext;

/**
 * Defines a base interface for all authentication interfaces
 */
public interface AuthenticationProvider {
  
  /**
   * Initializes this authentication strategy to work as per the given authentication source.
   * 
   * @param authSource The authentication source
   */
  public void initialize(AuthSource authSource);

  /**
   * Returns the name of this authentication provider.
   * 
   * @return The name of this authentication provider
   */
  public String getName();
  
  /**
   * Returns whether this provider requires credentials, i.e. username and password
   * 
   * @return <code>true</code> if the provider requires credentials, otherwise <code>false</code>
   */
  public boolean requiresCredentials();
  
  /**
   * Returns the names of the setting attributes of this provider
   *  
   * @return The names of the setting attributes of this provider
   */
  public String[] getKeys();
  
  /**
   * Returns a localized name for the given setting attribute.
   * 
   * @param locale The locale
   * @param key The setting key
   * 
   * @return A localized name for the given setting attribute
   */
  public String localizeKey(Locale locale, String key);
  
  /**
   * Processes the login request.
   * 
   * @param requestContext
   * @throws AuthenticationException
   */
  public AuthenticationResult processLogin(RequestContext requestContext);
}