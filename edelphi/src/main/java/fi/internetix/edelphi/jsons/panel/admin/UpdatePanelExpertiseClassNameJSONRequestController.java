package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdatePanelExpertiseClassNameJSONRequestController extends JSONController {

  public UpdatePanelExpertiseClassNameJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long expertiseClassId = jsonRequestContext.getLong("expertiseClassId");
    String newName = jsonRequestContext.getString("name");
    PanelUserExpertiseClassDAO expertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserExpertiseClass expertiseClass = expertiseClassDAO.findById(expertiseClassId);
    expertiseClassDAO.updateName(expertiseClass, newName);
  }
  
}
