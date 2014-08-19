package fi.internetix.edelphi.auth;

import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.internetix.smvc.controllers.RequestContext;

public abstract class OAuthAuthenticationStrategy extends AbstractAuthenticationStrategy {

  public OAuthAuthenticationStrategy(String... defaultScopes) {
    this.defaultScopes = defaultScopes;
  }
  
  protected String[] getDefaultScopes() {
    return defaultScopes;
  }

  @Override
  public abstract String getName();
  
  protected abstract Class<? extends Api> getApiClass();

  protected abstract String getApiKey();

  protected abstract String getApiSecret();

  protected abstract AuthenticationResult processResponse(RequestContext requestContext, OAuthService service, String[] requestedScopes);
  
  public boolean requiresCredentials() {
    return false;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    if (!"rsp".equals(requestContext.getString("_stg"))) {
      String[] scopes;
      
      String[] extraScopes = requestContext.getStrings("extraScope");
      if ((extraScopes != null) && (extraScopes.length > 0)) {
        int defaultScopesLength = getDefaultScopes() != null ? getDefaultScopes().length : 0;
        int extraScopesLength = extraScopes.length;
        scopes = new String[defaultScopesLength + extraScopesLength];
        for (int i = 0; i < defaultScopesLength; i++) {
          scopes[i] = getDefaultScopes()[i];
        }

        for (int i = 0; i < extraScopesLength; i++) {
          scopes[i + defaultScopesLength] = extraScopes[i];
        }
      } else {
        scopes = requestContext.getStrings("scope");
      }
      
      if (scopes == null)
        scopes = defaultScopes;

      session.setAttribute(getName() + ".requestedScopes", scopes);
      
      performDiscovery(requestContext, scopes);
      
      return AuthenticationResult.PROCESSING;
    } else {
      String[] requestedScopes = (String[]) session.getAttribute(getName() + ".requestedScopes");
      session.removeAttribute(getName() + ".requestedScopes");
      OAuthService service = getOAuthService(requestContext, requestedScopes);
      return processResponse(requestContext, service, requestedScopes);
    }
  }

  protected abstract String getOAuthCallbackURL(RequestContext requestContext);
  
  protected OAuthService getOAuthService(RequestContext requestContext, String... scopes) {
    String apiKey = getApiKey();
    String apiSecret = getApiSecret();
    String callback = getOAuthCallbackURL(requestContext);
    Class<? extends Api> apiClass = getApiClass();

    ServiceBuilder serviceBuilder = new ServiceBuilder()
      .provider(apiClass)
      .apiKey(apiKey)
      .apiSecret(apiSecret)
      .callback(callback);
    
    if (scopes != null && scopes.length > 0) {
      StringBuilder scopeBuilder = new StringBuilder();
      for (int i = 0, l = scopes.length; i < l;i++) {
        scopeBuilder.append(scopes[i]);
        if (i < (l - 1))
          scopeBuilder.append(' ');
      }
      serviceBuilder = serviceBuilder.scope(scopeBuilder.toString());
    }
    
    return serviceBuilder.build();
  }
  
  protected void setRequestToken(RequestContext requestContext, Token requestToken) {
    HttpSession session = requestContext.getRequest().getSession();

    if (requestToken != null)
      session.setAttribute("OAuthRequestToken", requestToken);
    else
      session.removeAttribute("OAuthRequestToken");
  }

  protected Token getRequestToken(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    
    return (Token) session.getAttribute("OAuthRequestToken");
  }
  
  public void performDiscovery(RequestContext requestContext, String... scopes) {
    OAuthService service = getOAuthService(requestContext, scopes);
    
    Token requestToken = null;
    boolean isV1 = DefaultApi10a.class.isAssignableFrom(getApiClass());

    // For OAuth version 1 the request token is fetched, for v2 it's not  
    if (isV1)
      requestToken = service.getRequestToken();

    String authUrl = service.getAuthorizationUrl(requestToken);

    setRequestToken(requestContext, requestToken);

    requestContext.setRedirectURL(authUrl);
  }

  private String[] defaultScopes;
}
