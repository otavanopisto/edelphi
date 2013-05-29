package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ArchiveUserGroupJSONRequestController extends JSONController {

  public ArchiveUserGroupJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long userGroupId = jsonRequestContext.getLong("userGroupId");
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    PanelUserGroup panelUserGroup = panelUserGroupDAO.findById(userGroupId);
    panelUserGroupDAO.archive(panelUserGroup);
  }
  
}
