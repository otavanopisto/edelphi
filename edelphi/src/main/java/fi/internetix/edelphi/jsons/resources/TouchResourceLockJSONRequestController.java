package fi.internetix.edelphi.jsons.resources;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.dao.resources.ResourceLockDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.resources.ResourceLock;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceLockUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class TouchResourceLockJSONRequestController extends JSONController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long resourceId = requestContext.getLong("resourceId");
    if (resourceId != null) {
      Resource resource = resourceDAO.findById(resourceId);
      Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
      if (resourcePanel != null) {
        authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
      }
      else {
        Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
        authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
      }
    }
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    ResourceDAO resourceDAO = new ResourceDAO(); 
    ResourceLockDAO resourceLockDAO = new ResourceLockDAO();
    
    Long resourceId = jsonRequestContext.getLong("resourceId");
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    if (resourceId != null) {
      Locale locale = jsonRequestContext.getRequest().getLocale();
      Resource resource = resourceDAO.findById(resourceId);
      ResourceLock resourceLock = resourceLockDAO.findByResource(resource);
      if (resourceLock == null) {
        Messages messages = Messages.getInstance();
        throw new SmvcRuntimeException(EdelfoiStatusCode.RESOURCE_LOCK_NOT_FOUND, messages.getText(locale, "exception.1022.resourceLockNotFound"));
      }
      else {
        if (!resourceLock.getCreator().getId().equals(loggedUser.getId()))
          throw new AccessDeniedException(locale);
        
        ResourceLockUtils.touchResourceLock(resource);
      }
    }
  }
}
