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

public class UpdatePanelBulletinJSONRequestController extends JSONController {

  public UpdatePanelBulletinJSONRequestController() {
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
    
    Long bulletinId = jsonRequestContext.getLong("bulletinId");
    String title = jsonRequestContext.getString("title");
    String message = jsonRequestContext.getString("message");

    PanelBulletin bulletin = panelBulletinDAO.findById(bulletinId);
    panelBulletinDAO.updateTitle(bulletin, title, loggedUser);
    panelBulletinDAO.updateMessage(bulletin, message, loggedUser);
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.managePanelBulletins.bulletinUpdated"));
    jsonRequestContext.addResponseParameter("bulletinId", bulletin.getId());
  }

}
