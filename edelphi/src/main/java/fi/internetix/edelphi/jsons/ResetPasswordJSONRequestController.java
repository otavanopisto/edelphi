package fi.internetix.edelphi.jsons;

import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.PasswordResetDAO;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.users.PasswordReset;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ResetPasswordJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    // E-mail given and exists?
    String email = StringUtils.lowerCase(jsonRequestContext.getString("email"));
    if (email == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.PASSWORD_RESET_NO_EMAIL, messages.getText(locale, "exception.1017.passwordResetNoEmail"));
    }
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    if (userEmail == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.PASSWORD_RESET_UNKNOWN_EMAIL, messages.getText(locale, "exception.1018.passwordResetUnknownEmail"));
    }
    // Has the account been activated yet?
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByUser(userEmail.getUser());
    if (userActivation != null) {
      String errorLink = messages.getText(locale,  "exception.1039.accountNotYetActivated.link");
      errorLink = "<a href=\"" + RequestUtils.getBaseUrl(jsonRequestContext.getRequest()) + "/resendactivation.page?email=" + email + "\">" + errorLink + "</a>";
      String errorTemplate = messages.getText(locale,  "exception.1039.accountNotYetActivated.template", new String[] { errorLink });
      throw new SmvcRuntimeException(EdelfoiStatusCode.ACCOUNT_NOT_YET_ACTIVATED, errorTemplate);
    }
    // Reset request
    PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    PasswordReset passwordReset = passwordResetDAO.findByEmail(email);
    if (passwordReset == null) {
      passwordReset = passwordResetDAO.create(email, UUID.randomUUID().toString());
    }
    // Reset e-mail
    String mailSubject = messages.getText(locale, "passwordReset.mailSubject");
    String resetLink = RequestUtils.getBaseUrl(jsonRequestContext.getRequest()) + "/resetpassword.page?email=" + passwordReset.getEmail() + "&hash=" + passwordReset.getHash();
    String mailContent = messages.getText(locale, "passwordReset.mailTemplate", new String [] { email, resetLink });
    String infoMessage = messages.getText(locale, "passwordReset.infoMessage", new String [] { email });
    MailUtils.sendMail(locale, email, mailSubject, mailContent);
    jsonRequestContext.addMessage(Severity.INFORMATION, infoMessage);
  }
  
}
