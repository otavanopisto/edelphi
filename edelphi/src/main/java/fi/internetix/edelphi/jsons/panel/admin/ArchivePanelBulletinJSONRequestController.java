package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelBulletinDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelBulletin;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class ArchivePanelBulletinJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_PANEL_BULLETINS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();
    UserDAO userDAO = new UserDAO();

    Long bulletinId = jsonRequestContext.getLong("bulletinId");

    PanelBulletin bulletin = panelBulletinDAO.findById(bulletinId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    panelBulletinDAO.archive(bulletin, loggedUser);
  }
}
