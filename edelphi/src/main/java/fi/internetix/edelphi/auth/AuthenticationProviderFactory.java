package fi.internetix.edelphi.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.domainmodel.base.AuthSource;

/**
 * The class responsible of managing the authentication providers of the application.
 */
public class AuthenticationProviderFactory {
  
  /**
   * Returns a singleton instance of this class.
   * 
   * @return A singleton instance of this class
   */
  public static AuthenticationProviderFactory getInstance() {
    return instance;
  }
  
  public AuthenticationProvider createAuthenticationProvider(AuthSource authSource) {
    AuthenticationProvider provider = instantiateProvider(authenticationProviders.get(authSource.getStrategy()));
    provider.initialize(authSource);
    return provider;
  }
  
  public List<String> getRegisteredProviderNames() {
    return new ArrayList<String>(authenticationProviders.keySet());
  }
  
  public boolean requiresCredentials(String provider) {
    return credentialAuths.get(provider);
  }

  /**
   * Registers an authentication provider to this class.
   * 
   * @param providerClass The authentication provider to be registered
   */
  public void registerAuthenticationProvider(Class<? extends AuthenticationProvider> providerClass) {
    AuthenticationProvider provider = instantiateProvider(providerClass);
    authenticationProviders.put(provider.getName(), providerClass);  
    credentialAuths.put(provider.getName(), provider.requiresCredentials());
  }
  
  private AuthenticationProvider instantiateProvider(Class<? extends AuthenticationProvider> providerClass) {
    try {
      return providerClass.newInstance();
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Error instantiating AuthenticationProvider " + providerClass.getName());
    }
  }
  
  /** Map containing authentication provider names as keys and the provider classes as values */ 
  private Map<String, Class<? extends AuthenticationProvider>> authenticationProviders = new HashMap<String, Class<? extends AuthenticationProvider>>();
  
  private Map<String, Boolean> credentialAuths = new HashMap<String, Boolean>();
  
  /** The singleton instance of this class */
  private static AuthenticationProviderFactory instance = new AuthenticationProviderFactory();
  
  static {
    AuthenticationProviderFactory.getInstance().registerAuthenticationProvider(GoogleAuthenticationStrategy.class);
    AuthenticationProviderFactory.getInstance().registerAuthenticationProvider(InternalAuthenticationStrategy.class);
    AuthenticationProviderFactory.getInstance().registerAuthenticationProvider(FacebookAuthenticationStrategy.class);
    AuthenticationProviderFactory.getInstance().registerAuthenticationProvider(TwitterAuthenticationStrategy.class);
    AuthenticationProviderFactory.getInstance().registerAuthenticationProvider(NingAuthenticationStrategy.class);
  }
}
