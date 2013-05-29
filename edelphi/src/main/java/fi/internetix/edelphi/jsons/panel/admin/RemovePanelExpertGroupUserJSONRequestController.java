package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class RemovePanelExpertGroupUserJSONRequestController extends JSONController {

  public RemovePanelExpertGroupUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    
    Long panelExpertGroupUserId = jsonRequestContext.getLong("panelExpertGroupUserId");
    PanelExpertiseGroupUser panelExpertiseGroupUser = panelExpertiseGroupUserDAO.findById(panelExpertGroupUserId);

    panelExpertiseGroupUserDAO.delete(panelExpertiseGroupUser);
  }
  
}
