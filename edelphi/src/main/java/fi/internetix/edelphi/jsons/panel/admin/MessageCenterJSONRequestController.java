package fi.internetix.edelphi.jsons.panel.admin;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class MessageCenterJSONRequestController extends JSONController {

  public MessageCenterJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    String messageSubject = jsonRequestContext.getString("messageCenterMessageSubject");
    String messageContent = jsonRequestContext.getString("messageCenterMessageContent");

    PanelUserDAO panelUserDAO = new PanelUserDAO();

    List<String> emails = new ArrayList<String>();
    List<PanelUser> users = panelUserDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    for (PanelUser user : users) {
      if (user.getId().equals(jsonRequestContext.getLong("messageCenterMessageRecipient." + user.getId())))
        emails.add(user.getUser().getDefaultEmail().getAddress());
    }

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    if (emails.size() > 0) {
      InternetAddress from;
      try {
        from = new InternetAddress(loggedUser.getDefaultEmail().getAddress(), loggedUser.getFullName(false, false));
      } catch (UnsupportedEncodingException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1016.invalidConfiguration"));
      }
      
      MailUtils.sendMail(locale, emails.toArray(new String[0]), from, messageSubject, messageContent, "text/plain");

      jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.messageCenter.messagesSent", new String[] { emails.size() + "" }));
    } else {
      jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.messageCenter.noMessagesSent"));
    }
  }

}
