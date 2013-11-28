package fi.internetix.edelphi.jsons;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class ArchiveInvitationJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Long invitationId = jsonRequestContext.getLong("invitationId");
    PanelInvitation invitation = panelInvitationDAO.findById(invitationId);
    if (invitation != null) {
      panelInvitationDAO.archive(invitation, loggedUser);
    }
  }
}
