package fi.internetix.edelphi.jsons;

import fi.internetix.edelphi.ActionedController;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestController;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class JSONController implements JSONRequestController, ActionedController {

  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    String actionAccessName = getAccessActionName() == null ? null : getAccessActionName().toString();
    DelfoiActionScope actionAccessScope = getAccessActionScope();
    
    if (actionAccessName != null) {
      switch(actionAccessScope) {
        case DELFOI:
          Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
          authorizeDelfoi(requestContext, delfoi, actionAccessName);
        break;
        
        case PANEL:
          Panel panel = RequestUtils.getPanel(requestContext);
          authorizePanel(requestContext, panel, actionAccessName);
        break;
      }
    }
  }

  protected void authorizePanel(RequestContext requestContext, Panel panel, String actionAccessName) {
    if (panel == null)
      throw new IllegalStateException("JSONController panel action without panel");
      
    if (!ActionUtils.hasPanelAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }

  protected void authorizeDelfoi(RequestContext requestContext, Delfoi delfoi, String actionAccessName) {
    if (delfoi == null)
      throw new IllegalStateException("JSONController Delfoi action without Delfoi");
    
    if (!ActionUtils.hasDelfoiAccess(requestContext, actionAccessName.toString())) {
      throw new AccessDeniedException(requestContext.getRequest().getLocale());
    }
  }
  
  protected void setAccessAction(DelfoiActionName accessActionName, DelfoiActionScope actionScope) {
    this.accessActionName = accessActionName;
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
