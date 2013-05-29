package fi.internetix.edelphi.jsons.issues;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.mail.internet.InternetAddress;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ReportIssueJSONRequestController extends JSONController {

  public ReportIssueJSONRequestController() {
    super();
    // TODO: User should be at least guest to send reports
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();

    String address = SystemUtils.getSettingValue("system.issueEmail");
    
    String subject = jsonRequestContext.getString("subject");
   
    StringBuilder content = new StringBuilder();
    content.append(jsonRequestContext.getString("content"));
    
    Locale locale = jsonRequestContext.getRequest().getLocale();
    Messages messages = Messages.getInstance();
    User loggedUser = jsonRequestContext.isLoggedIn() ? userDAO.findById(jsonRequestContext.getLoggedUserId()) : null;
    
    InternetAddress from = null;
    if (loggedUser != null) {
      try {
        from = new InternetAddress(loggedUser.getDefaultEmail().getAddress(), loggedUser.getFullName(false, false));
      } catch (UnsupportedEncodingException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1016.invalidConfiguration"));
      }
    }
    
    MailUtils.sendMail(locale, new String[] { address }, from, subject, content.toString(), MailUtils.PLAIN);
    
    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "reportIssue.block.issueReportedMessage"));
  }
}