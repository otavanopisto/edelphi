package fi.internetix.edelphi.jsons.resources;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class ArchiveResourceJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long resourceId = requestContext.getLong("resourceId");
    
    Resource resource = resourceDAO.findById(resourceId);
    
    Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
    
    if (resourcePanel != null) {
      authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
    } else {
      Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
      authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
    }
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    ResourceDAO resourceDAO = new ResourceDAO();
    UserDAO userDAO = new UserDAO();

    Long resourceId = jsonRequestContext.getLong("resourceId");
    
    Resource resource = resourceDAO.findById(resourceId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    resourceDAO.setArchived(resource, Boolean.TRUE, loggedUser);
  }
}
