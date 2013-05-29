package fi.internetix.edelphi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.auth.AuthenticationProviderFactory;
import fi.internetix.edelphi.auth.InternalAuthenticationStrategy;
import fi.internetix.edelphi.auth.OAuthAccessToken;
import fi.internetix.edelphi.dao.base.DelfoiAuthDAO;
import fi.internetix.edelphi.dao.panels.PanelAuthDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.domainmodel.base.AuthSource;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiAuth;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelAuth;
import fi.internetix.smvc.controllers.RequestContext;

public class AuthUtils {

  private static final String INTERNAL_AUTHORIZATION_HEADER = "InternalAuthorization ";
  
  /**
   * Includes authentication source information to the given request context. The available authentication sources
   * are set as a list to attribute <code>authSources</code>. If internal authentication is available, it is not
   * included in the list but its identifier is present as attribute <code>internalAuthSource</code>.
   * 
   * @param requestContext The request context to which the authentication source information is stored.
   */
  public static void includeAuthSources(RequestContext requestContext) {
    includeAuthSources(requestContext, getLoginContextType(requestContext), getLoginContextId(requestContext));
  }
  
  public static void includeAuthSources(RequestContext requestContext, String contextType, Long contextId) {
    List<AuthSource> authSources = new ArrayList<AuthSource>();
    boolean delfoiLevelAuth = true;
    if ("PANEL".equals(contextType)) {
      // Panel specific authentication sources
      PanelDAO panelDAO = new PanelDAO();
      PanelAuthDAO panelAuthDAO = new PanelAuthDAO();
      Panel panel = panelDAO.findById(contextId);
      List<PanelAuth> panelAuths = panelAuthDAO.listByPanel(panel);
      for (PanelAuth panelAuth : panelAuths) {
        authSources.add(panelAuth.getAuthSource());
      }
      delfoiLevelAuth = panelAuths.isEmpty();
    }
    if (delfoiLevelAuth) {
      // Delfoi specific authentication sources; used outside panels or when panel doesn't specify its own
      Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
      DelfoiAuthDAO delfoiAuthDAO = new DelfoiAuthDAO();
      List<DelfoiAuth> delfoiAuths = delfoiAuthDAO.listByDelfoi(delfoi);
      for (DelfoiAuth delfoiAuth : delfoiAuths) {
        authSources.add(delfoiAuth.getAuthSource());
      }
    }
    boolean hasInternalAuth = false;
    int credentialAuthCount = 0;
    AuthenticationProviderFactory authFactory = AuthenticationProviderFactory.getInstance();
    for (AuthSource authSource : authSources) {
      if (authSource.getStrategy().equals(InternalAuthenticationStrategy.STRATEGY_NAME)) {
        hasInternalAuth = true;
      }
      if (authFactory.requiresCredentials(authSource.getStrategy())) {
        credentialAuthCount++;
      }
    }
    Collections.sort(authSources, new Comparator<AuthSource>() {
      @Override
      public int compare(AuthSource o1, AuthSource o2) {
        AuthenticationProviderFactory authFactory = AuthenticationProviderFactory.getInstance();
        boolean o1Credential = authFactory.requiresCredentials(o1.getStrategy());
        boolean o2Credential = authFactory.requiresCredentials(o2.getStrategy());
        if (o1Credential != o2Credential) {
          return o1Credential ? -1 : 1;
        }
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    requestContext.getRequest().setAttribute("authSources", authSources);
    requestContext.getRequest().setAttribute("hasInternalAuth", hasInternalAuth);
    requestContext.getRequest().setAttribute("authCount", authSources.size());
    requestContext.getRequest().setAttribute("credentialAuthCount", credentialAuthCount);
  }

  public static void storeRedirectUrl(RequestContext requestContext, String redirectUrl) {
    HttpSession session = requestContext.getRequest().getSession();
    session.setAttribute("loginRedirectUrl", redirectUrl);
  }

  public static String retrieveRedirectUrl(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String redirectUrl = (String) session.getAttribute("loginRedirectUrl");
    session.removeAttribute("loginRedirectUrl");
    return redirectUrl;
  }
  
  public static void storeAuthSourceId(RequestContext requestContext, Long authSourceId) {
    HttpSession session = requestContext.getRequest().getSession();
    session.setAttribute("authSourceId", authSourceId);
  }

  public static Long retrieveAuthSourceId(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    Long authSourceId = (Long) session.getAttribute("authSourceId");
    session.removeAttribute("authSourceId");
    return authSourceId;
  }

  public static String getLoginContextType(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String contextType = (String) session.getAttribute("loginContextType");
    contextType = contextType == null ? "DELFOI" : contextType;
    return contextType;
  }

  public static Long getLoginContextId(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    String loginContextId = (String) session.getAttribute("loginContextId");
    return NumberUtils.isNumber(loginContextId) ? new Long(loginContextId) : RequestUtils.getDelfoi(requestContext).getId();
  }
  
  @SuppressWarnings("unchecked")
  public static void addAuthenticationStrategy(RequestContext requestContext, String strategy) {
    HttpSession session = requestContext.getRequest().getSession();
    Set<String> authenticationStrategies = (Set<String>) session.getAttribute("authenticationStrategies");
    if (authenticationStrategies == null) {
      authenticationStrategies = new HashSet<String>();
    }
    if (!authenticationStrategies.contains(strategy)) {
      authenticationStrategies.add(strategy);
    }
    session.setAttribute("authenticationStrategies", authenticationStrategies); 
  }
  
  @SuppressWarnings("unchecked")
  public static boolean isAuthenticatedBy(RequestContext requestContext, String strategy) {
    HttpSession session = requestContext.getRequest().getSession();
    Set<String> authenticationStrategies = (Set<String>) session.getAttribute("authenticationStrategies");
    return authenticationStrategies != null && authenticationStrategies.contains(strategy);
  }
  
  public static void storeOAuthAccessToken(RequestContext requestContext, String provider, OAuthAccessToken token) {
    HttpSession session = requestContext.getRequest().getSession();
    OAuthAccessToken[] accessTokens = (OAuthAccessToken[]) session.getAttribute(provider + ".accessTokens");
    if (accessTokens == null) {
      session.setAttribute(provider + ".accessTokens", new OAuthAccessToken[]{token});
    } else {
      OAuthAccessToken[] newTokens = new OAuthAccessToken[accessTokens.length + 1];
      for (int i = 0, l = accessTokens.length; i < l; i++) {
        newTokens[i] = accessTokens[i];
      }
      newTokens[newTokens.length - 1] = token;
      session.setAttribute(provider + ".accessTokens", newTokens);
    }
  }

  public static OAuthAccessToken getOAuthAccessToken(RequestContext requestContext, String provider, String... scopes) {
    HttpSession session = requestContext.getRequest().getSession();
    
    OAuthAccessToken[] accessTokens = (OAuthAccessToken[]) session.getAttribute(provider + ".accessTokens");
    if (accessTokens != null) {
      for (OAuthAccessToken accessToken : accessTokens) {
      	if (Arrays.asList(accessToken.getScopes()).containsAll(Arrays.asList(scopes))) {
      		return accessToken;
      	}
      }
    }
    
    return null;
  }
  
  public static boolean isGrantedOAuthScope(RequestContext requestContext, String provider, String scope) {
    return getOAuthAccessToken(requestContext, provider, scope) != null;
  }
  
  public static boolean isOAuthTokenExpired(OAuthAccessToken accessToken) {
    return (accessToken.getExpires() != null) && (accessToken.getExpires().getTime() < System.currentTimeMillis());
  }
  
  public static String getInternalAuthorization(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    if (!StringUtils.startsWith(authorizationHeader, INTERNAL_AUTHORIZATION_HEADER)) {
      return null;
    }
    
    return authorizationHeader.substring(INTERNAL_AUTHORIZATION_HEADER.length());
  }
}
