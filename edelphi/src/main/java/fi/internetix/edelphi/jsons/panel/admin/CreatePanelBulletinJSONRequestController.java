package fi.internetix.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelBulletinDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelBulletin;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreatePanelBulletinJSONRequestController extends JSONController {

  public CreatePanelBulletinJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_BULLETINS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();
    UserDAO userDAO = new UserDAO();

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    String title = jsonRequestContext.getString("title");
    String message = jsonRequestContext.getString("message");

    PanelBulletin bulletin = panelBulletinDAO.create(panel, title, message, loggedUser);
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.managePanelBulletins.bulletinCreated"));
    jsonRequestContext.addResponseParameter("bulletinId", bulletin.getId());
  }

}
