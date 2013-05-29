package fi.internetix.edelphi.jsons.resources;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SetResourceVisibilityJSONRequestController extends JSONController {

  public SetResourceVisibilityJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    ResourceDAO resourceDAO = new ResourceDAO();
    UserDAO userDAO = new UserDAO();

    Long resourceId = jsonRequestContext.getLong("resourceId");
    Boolean visible = "1".equals(jsonRequestContext.getString("visible"));
    
    Resource resource = resourceDAO.findById(resourceId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    resourceDAO.updateVisible(resource, visible, loggedUser);
  }
}
