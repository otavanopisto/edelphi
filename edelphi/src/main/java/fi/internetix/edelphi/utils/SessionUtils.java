package fi.internetix.edelphi.utils;

import javax.servlet.http.HttpSession;

import fi.internetix.smvc.controllers.RequestContext;

public class SessionUtils {

  private static final String THEME_ATTRIBUTE = "theme";
  
  public static String getCurrentTheme(HttpSession session) {
    return (String) session.getAttribute(THEME_ATTRIBUTE);
  }
  
  public static void setCurrentTheme(HttpSession session, String theme) {
    session.setAttribute(THEME_ATTRIBUTE, theme);
  }
 
  public static String getThemePath(RequestContext requestContext) {
    String currentTheme = getCurrentTheme(requestContext.getRequest().getSession());
    String baseUrl = RequestUtils.getBaseUrl(requestContext.getRequest());
    return baseUrl + "/_themes/" + currentTheme;
  }
}
