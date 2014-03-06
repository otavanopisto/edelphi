package fi.internetix.edelphi.jsons.panel.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.base.EmailMessageDAO;
import fi.internetix.edelphi.dao.base.MailQueueItemDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.base.MailQueueItemState;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SendEmailJSONRequestController extends JSONController {

  public SendEmailJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }
    
    String mailSubject = jsonRequestContext.getString("sendEmailSubject");
    String mailContent = jsonRequestContext.getString("sendEmailContent");

    PanelUserDAO panelUserDAO = new PanelUserDAO();

    List<String> emails = new ArrayList<String>();
    List<PanelUser> users = panelUserDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    for (PanelUser user : users) {
      if (user.getId().equals(jsonRequestContext.getLong("emailRecipient." + user.getId()))) {
        if (user.getUser().getDefaultEmail() == null) {
          jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.sendEmail.noEmail", new String[] { user.getUser().getFullName(false, false)}));
        }
        else {
          emails.add(user.getUser().getDefaultEmail().getAddress());
        }
      }
    }
    
    if (emails.size() > 0) {
      EmailMessageDAO emailMessageDAO = new EmailMessageDAO();
      MailQueueItemDAO mailQueueItemDAO = new MailQueueItemDAO();
      for (String email : emails) {
        EmailMessage emailMessage = emailMessageDAO.create(loggedUser.getDefaultEmail().getAddress(), email, mailSubject, mailContent, loggedUser);
        mailQueueItemDAO.create(MailQueueItemState.IN_QUEUE, emailMessage, loggedUser);
      }
      jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.sendEmail.mailsSent", new String[] { emails.size() + "" }));
    }
    else {
      jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.sendEmail.noMailsSent"));
    }
  }

}
