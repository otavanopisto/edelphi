package fi.internetix.edelphi.utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.DelfoiDAO;
import fi.internetix.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.internetix.edelphi.dao.base.SystemUserRoleDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserPictureDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiDefaults;
import fi.internetix.edelphi.domainmodel.base.DelfoiUser;
import fi.internetix.edelphi.domainmodel.base.SystemUserRole;
import fi.internetix.edelphi.domainmodel.base.SystemUserRoleType;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserRole;
import fi.internetix.smvc.SmvcMessage;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;

public class RequestUtils {
  
  public static String getBaseUrl(HttpServletRequest request) {
    String currentURL = request.getRequestURL().toString();
    String pathInfo = request.getRequestURI();
    return currentURL.substring(0, currentURL.length() - pathInfo.length()) + request.getContextPath();
  }

  public static String getCurrentUrl(HttpServletRequest request, boolean stripApp) {
    if (stripApp == false) {
      StringBuilder currentUrl = new StringBuilder(request.getRequestURL());
      String queryString = request.getQueryString();
      if (!StringUtils.isBlank(queryString)) {
        currentUrl.append('?');
        currentUrl.append(queryString);
      }
      return currentUrl.toString();
    } else {
      String stripString = "/_app";
      StringBuilder currentUrl = new StringBuilder(getBaseUrl(request));
      String contextPath = request.getContextPath();
      String pathInfo = request.getRequestURI().substring(contextPath.length());
      if (pathInfo.startsWith(stripString)) {
        pathInfo = pathInfo.substring(stripString.length());
      }
      currentUrl.append(pathInfo);      
      String queryString = request.getQueryString();
      if (!StringUtils.isBlank(queryString)) {
        currentUrl.append('?');
        currentUrl.append(queryString);
      }
      return currentUrl.toString();
    }
  }

  public static void storeRedirectMessage(RequestContext requestContext, SmvcMessage message) {
    HttpSession session = requestContext.getRequest().getSession();
    @SuppressWarnings("unchecked")
    List<SmvcMessage> messages = (List<SmvcMessage>) session.getAttribute("redirectMessages");
    if (messages == null) {
      messages = new ArrayList<SmvcMessage>();
    }
    messages.add(message);
    session.setAttribute("redirectMessages", messages);
  }

  public static List<SmvcMessage> retrieveRedirectMessages(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    @SuppressWarnings("unchecked")
    List<SmvcMessage> messages = (List<SmvcMessage>) session.getAttribute("redirectMessages");
    session.removeAttribute("redirectMessages");
    return messages;
  }
  
  public static String sortUrlQueryParams(String url) {
    int pos = url.indexOf('?');
    if (pos != -1) {
      String[] queryParams = url.substring(pos + 1).split("&");
      Arrays.sort(queryParams);
      StringBuilder urlBuilder = new StringBuilder();
      urlBuilder.append(url.substring(0, pos + 1));
      for (int i = 0; i < queryParams.length; i++) {
        urlBuilder.append(queryParams[i]);
        if (i < queryParams.length - 1) {
          urlBuilder.append('&');
        }
      }
      url = urlBuilder.toString();
    }
    return url;
  }
  
  public static void loginUser(RequestContext requestContext, User user) {
    HttpSession session = requestContext.getRequest().getSession(true);
    
    // Store last login  
    
    UserDAO userDAO = new UserDAO();
    userDAO.updateLastLogin(user, new Date());

    UserPictureDAO pictureDAO = new UserPictureDAO();
    Boolean hasPicture = pictureDAO.findUserHasPicture(user);
    
    session.setAttribute("loggedUserId", user.getId());
    session.setAttribute("loggedUserFullName", user.getFullName(false));
    session.setAttribute("loggedUserHasPicture", hasPicture);
  }
  
  public static void logoutUser(RequestContext requestContext) {
    HttpSession session = requestContext.getRequest().getSession();
    session.invalidate();
  }
  
  public static User getUser(RequestContext requestContext) {
    UserDAO userDAO = new UserDAO();
    
    Long userId = requestContext.getLoggedUserId();

    if (userId != null)
      return userDAO.findById(userId);
    else    
      return null;
  }
  
  public static UserRole getUserRole(RequestContext requestContext, DelfoiActionScope actionScope) {
    // TODO role caching
    User user = RequestUtils.getUser(requestContext);
    if (user == null) {
      return getEveryoneRole();
    }
    else {
      switch (actionScope) {
      case DELFOI:
        Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
        if (delfoi != null) {
          DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
          DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(delfoi, user);
          return delfoiUser == null ? getEveryoneRole() : delfoiUser.getRole();
        }
        return getEveryoneRole();
      case PANEL:
        Panel panel = RequestUtils.getPanel(requestContext);
        if (panel != null) {
          PanelUserDAO panelUserDAO = new PanelUserDAO();
          PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
          return panelUser == null ? getEveryoneRole() : panelUser.getRole();
        }
        return getEveryoneRole();
      }
    }
    return getEveryoneRole();
  }

  public static Delfoi getDelfoi(RequestContext requestContext) {
    DelfoiDAO delfoiDAO = new DelfoiDAO(); 
    
    Long delfoiId = (Long) requestContext.getRequest().getSession().getAttribute("delfoiId");
    
    return delfoiDAO.findById(delfoiId);
  }
  
  public static PanelStamp getActiveStamp(RequestContext requestContext) {
    Long panelId = getPanelIdFromRequest(requestContext);
    if (panelId == null) {
      // not within panel, cannot have stamp
      return null;
    }
    PanelStamp panelStamp = null;
    Long stampId = (Long) requestContext.getRequest().getSession().getAttribute("stampId");
    if (stampId != null) {
      Panel panel = getPanel(requestContext);
      if (stampId.equals(panel.getCurrentStamp().getId())) {
        // latest stamp of the current panel
        return panel.getCurrentStamp();
      }
      PanelStampDAO panelStampDAO = new PanelStampDAO();
      panelStamp = panelStampDAO.findById(stampId);
      if (panelStamp != null && !panelId.equals(panelStamp.getPanel().getId())) {
        // reset active stamp as we reside in a different panel 
        setActiveStamp(requestContext, null);
      }
      else {
        return panelStamp;
      }
    }
    // by default, return the current stamp of the current panel
    return getPanel(requestContext).getCurrentStamp();
  }

  public static void setActiveStamp(RequestContext requestContext, Long panelStampId) {
    HttpSession session = requestContext.getRequest().getSession(true);
    if (panelStampId == null) {
      session.removeAttribute("stampId");
    }
    else {
      session.setAttribute("stampId", panelStampId);
    }
  }
  
  public static SystemUserRole getEveryoneRole() {
    SystemUserRoleDAO systemUserRoleDAO = new SystemUserRoleDAO();

    return systemUserRoleDAO.findByType(SystemUserRoleType.EVERYONE);
  }
  
  public static DelfoiDefaults getDefaults(RequestContext requestContext) {
    DelfoiDefaultsDAO delfoiDefaultsDAO = new DelfoiDefaultsDAO();
    Delfoi delfoi = getDelfoi(requestContext);
    return delfoiDefaultsDAO.findByDelfoi(delfoi);
  }
  
  public static Panel getPanel(RequestContext requestContext) {
    PanelDAO panelDAO = new PanelDAO();  
    
    Long panelId = getPanelIdFromRequest(requestContext);

    if (panelId != null)
      return panelDAO.findById(panelId);
    else    
      return null;
  }
  
  public static Long getPanelIdFromRequest(RequestContext requestContext) {
    Long panelId;

    if ("PANEL".equals(getSecurityContextType(requestContext)))
      panelId = getSecurityContextId(requestContext);
    else
      panelId = requestContext.getLong("panelId");
    
    return panelId;
  }

  public static String md5EncodeString(String s) {
    try {
      if (s == null)
        return null;
  
      if (StringUtils.isBlank(s))
        return "";
  
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(s.getBytes("UTF-8"));
      byte messageDigest[] = algorithm.digest();
  
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
        String hex = Integer.toHexString(0xFF & messageDigest[i]);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    }
    catch (Exception e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
  

  public static Long getSecurityContextId(RequestContext requestContext) {
    return requestContext.getLong("securityContextId");
  }

  public static String getSecurityContextType(RequestContext requestContext) {
    return requestContext.getString("securityContextType");
  }
}
