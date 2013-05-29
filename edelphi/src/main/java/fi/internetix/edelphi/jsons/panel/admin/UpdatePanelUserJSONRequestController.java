package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
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
    
    UserDAO userDAO = new UserDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();

    PanelUser panelUser = panelUserDAO.findById(panelUserId);
    PanelUserRole panelUserRole = panelUserRoleDAO.findById(newRoleId);
    
    User modifier = RequestUtils.getUser(jsonRequestContext);

    // Update Role of Panel User
    if (!panelUser.getUser().getId().equals(jsonRequestContext.getLoggedUserId()))
      panelUserDAO.updateRole(panelUser, panelUserRole, modifier);
    
    String firstName = jsonRequestContext.getString("newFirstName");
    String lastName = jsonRequestContext.getString("newLastName");
    String nickname = panelUser.getUser().getNickname();

    // Update User
    userDAO.update(panelUser.getUser(), firstName, lastName, nickname, modifier);
  }
  
}
