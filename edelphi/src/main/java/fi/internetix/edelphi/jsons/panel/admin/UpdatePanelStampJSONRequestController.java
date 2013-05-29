package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdatePanelStampJSONRequestController extends JSONController {

  public UpdatePanelStampJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    String name = jsonRequestContext.getString("title");
    String description = jsonRequestContext.getString("description");
    PanelStamp panelStamp = panelStampDAO.findById(jsonRequestContext.getLong("stampId"));
    panelStampDAO.update(panelStamp, name, description, panelStamp.getStampTime(), loggedUser);
  }
  
}
