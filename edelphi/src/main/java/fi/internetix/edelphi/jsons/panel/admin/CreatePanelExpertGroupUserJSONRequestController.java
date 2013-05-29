package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreatePanelExpertGroupUserJSONRequestController extends JSONController {

  public CreatePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    
    Long expertiseGroupId = jsonRequestContext.getLong("expertiseGroupId");
    PanelUserExpertiseGroup expertiseGroup = panelUserExpertiseGroupDAO.findById(expertiseGroupId);
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    PanelUser panelUser = panelUserDAO.findById(panelUserId);
    Double weight = jsonRequestContext.getDouble("weight");

    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.create(expertiseGroup, panelUser, weight);
    
    jsonRequestContext.addResponseParameter("id", panelExpertiseGroupUser.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelExpertiseGroupUser.getPanelUser().getUser().getFullName(true, false));
    jsonRequestContext.addResponseParameter("email", panelExpertiseGroupUser.getPanelUser().getUser().getDefaultEmailAsString());
  }
  
}
