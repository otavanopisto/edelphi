package fi.internetix.edelphi.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.ActionedController;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.SmvcMessage;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class PageController implements fi.internetix.smvc.controllers.PageController, ActionedController {

  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    String actionAccessName = getAccessActionName() == null ? null : getAccessActionName().toString();
    DelfoiActionScope actionAccessScope = getAccessActionScope();
    
    if (actionAccessName != null) {
      switch(actionAccessScope) {
        case DELFOI:
          Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
          if (delfoi == null)
            throw new AccessDeniedException(requestContext.getRequest().getLocale());
          
          if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName)) {
            if (!requestContext.isLoggedIn()) {
              throw new LoginRequiredException(RequestUtils.getCurrentUrl(requestContext.getRequest(), true), "DELFOI", delfoi.getId() + "");
            }
            else {
              throw new AccessDeniedException(requestContext.getRequest().getLocale());
            }
          }
        break;
        
        case PANEL:
          Panel panel = RequestUtils.getPanel(requestContext);
          if (panel == null)
            throw new AccessDeniedException(requestContext.getRequest().getLocale());
            
          if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName)) {
            if (!requestContext.isLoggedIn()) {
              throw new LoginRequiredException(RequestUtils.getCurrentUrl(requestContext.getRequest(), true), "PANEL", panel.getId() + "");
            }
            else {
              throw new AccessDeniedException(requestContext.getRequest().getLocale());
            }
          }
        break;
      }
    }
  }

  protected void authorizePanel(RequestContext requestContext, Panel panel, String actionAccessName) {
    if (panel == null)
      throw new IllegalStateException("PageController panel action without panel");
      
    if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }

  protected void authorizeDelfoi(RequestContext requestContext, Delfoi delfoi, String actionAccessName) {
    if (delfoi == null)
      throw new IllegalStateException("PageController Delfoi action without Delfoi");
    
    if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }
  
  public void process(PageRequestContext pageRequestContext) {
    List<SmvcMessage> messages = RequestUtils.retrieveRedirectMessages(pageRequestContext);
    if (messages != null) {
      for (SmvcMessage message : messages) {
        pageRequestContext.addMessage(message);
      }
    }
  }
  
  protected void setJsDataVariable(PageRequestContext pageRequestContext, String name, String value) {
    @SuppressWarnings("unchecked")
    Map<String, String> jsData = (Map<String, String>) pageRequestContext.getRequest().getAttribute("jsData");
    if (jsData == null) {
      jsData = new HashMap<String, String>();
      pageRequestContext.getRequest().setAttribute("jsData", jsData);
    }
    
    jsData.put(name, value);
  }
  
  protected void setAccessAction(DelfoiActionName actionName, DelfoiActionScope actionScope) {
    this.accessActionName = actionName;
    this.accessActionScope = actionScope;
  }
  
  public DelfoiActionName getAccessActionName() {
    return accessActionName;
  }
  
  public DelfoiActionScope getAccessActionScope() {
    return accessActionScope;
  }
  
  private DelfoiActionName accessActionName = null;
  private DelfoiActionScope accessActionScope;
}
