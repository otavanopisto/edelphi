package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdatePanelInterestClassNameJSONRequestController extends JSONController {

  public UpdatePanelInterestClassNameJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long interestClassId = jsonRequestContext.getLong("interestClassId");
    String newName = jsonRequestContext.getString("name");
    PanelUserIntressClassDAO interestClassDAO = new PanelUserIntressClassDAO();
    PanelUserIntressClass expertiseClass = interestClassDAO.findById(interestClassId);
    interestClassDAO.updateName(expertiseClass, newName);
  }
  
}
