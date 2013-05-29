package fi.internetix.edelphi.jsons;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.PasswordResetDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.domainmodel.users.PasswordReset;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ChangePasswordJSONRequestController extends JSONController {

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
    // Password reset request exists?
    String hash = jsonRequestContext.getString("hash");
    PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    PasswordReset passwordReset = passwordResetDAO.findByEmailAndHash(email, hash);
    if (passwordReset == null) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_PASSWORD_RESET_REQUEST, messages.getText(locale, "exception.1019.invalidPasswordReset"));
    }
    
    String password = jsonRequestContext.getString("password");
    
    // Create or change password
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    UserPassword userPassword = userPasswordDAO.findByUser(userEmail.getUser());
    if (userPassword != null) {
      userPasswordDAO.updatePasswordHash(userPassword, password);
    }
    else {
      userPasswordDAO.create(userEmail.getUser(), password);
    }
    // Delete password reset request
    passwordResetDAO.delete(passwordReset);
    // All done
    jsonRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.passwordChanged"));
  }
  
}
