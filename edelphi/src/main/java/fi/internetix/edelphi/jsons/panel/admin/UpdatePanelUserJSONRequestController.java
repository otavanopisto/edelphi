package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdatePanelUserJSONRequestController extends JSONController {

  public UpdatePanelUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    Long newRoleId = jsonRequestContext.getLong("newRoleId");
    
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();

    PanelUser panelUser = panelUserDAO.findById(panelUserId);
    PanelUserRole panelUserRole = panelUserRoleDAO.findById(newRoleId);
    
    User modifier = RequestUtils.getUser(jsonRequestContext);

    // Update role of panel user
    
    if (!panelUser.getUser().getId().equals(jsonRequestContext.getLoggedUserId())) {
      panelUserDAO.updateRole(panelUser, panelUserRole, modifier);
    }
  }
  
}
