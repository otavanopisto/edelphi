package fi.internetix.edelphi.jsons.panel.admin;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.UserUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ArchivePanelUserJSONRequestController extends JSONController {

  public ArchivePanelUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long panelUserId = jsonRequestContext.getLong("panelUserId");
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUser panelUser = panelUserDAO.findById(panelUserId);

    if (!panelUser.getUser().getId().equals(jsonRequestContext.getLoggedUserId())) {
      UserUtils.archivePanelUser(panelUser, RequestUtils.getUser(jsonRequestContext));
    }
    else {
      // Cannot archive yourself
      throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_ARCHIVE_SELF_FROM_PANEL, messages.getText(locale, "exception.1021.cannotArchiveSelfFromPanel"));
    }
    
    jsonRequestContext.addMessage(Severity.OK, messages.getText(
        locale, "panel.admin.managePanelUsers.msgUserRemovedSuccessfully"));
  }
  
}
