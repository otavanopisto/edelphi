package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdatePanelExpertGroupUserJSONRequestController extends JSONController {

  public UpdatePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    
    Long newExpertiseGroupId = jsonRequestContext.getLong("newExpertiseGroupId");
    PanelUserExpertiseGroup newExpertiseGroup = panelUserExpertiseGroupDAO.findById(newExpertiseGroupId);
    Long panelExpertGroupUserId = jsonRequestContext.getLong("panelExpertGroupUserId");
    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.findById(panelExpertGroupUserId);

    panelExpertiseGroupUser = panelExpertiseGroupUserDAO.updateGroup(panelExpertiseGroupUser, newExpertiseGroup);
    
    jsonRequestContext.addResponseParameter("id", panelExpertiseGroupUser.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelExpertiseGroupUser.getPanelUser().getUser().getFullName(true, false));
    jsonRequestContext.addResponseParameter("email", panelExpertiseGroupUser.getPanelUser().getUser().getDefaultEmailAsString());
  }
  
}
