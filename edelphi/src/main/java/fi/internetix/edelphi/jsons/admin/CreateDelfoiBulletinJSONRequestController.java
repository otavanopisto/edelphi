package fi.internetix.edelphi.jsons.admin;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreateDelfoiBulletinJSONRequestController extends JSONController {

  public CreateDelfoiBulletinJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    if (delfoi == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    UserDAO userDAO = new UserDAO();

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    String title = jsonRequestContext.getString("title");
    String message = jsonRequestContext.getString("message");

    DelfoiBulletin bulletin = bulletinDAO.create(delfoi, title, message, loggedUser);
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "admin.managePanelBulletins.bulletinCreated"));
    jsonRequestContext.addResponseParameter("bulletinId", bulletin.getId());
  }

}
