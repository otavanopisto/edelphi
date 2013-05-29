package fi.internetix.edelphi.jsons.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class ArchiveDelfoiBulletinJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    UserDAO userDAO = new UserDAO();

    Long bulletinId = jsonRequestContext.getLong("bulletinId");

    DelfoiBulletin bulletin = bulletinDAO.findById(bulletinId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    bulletinDAO.archive(bulletin, loggedUser);
  }
}
