package fi.internetix.edelphi.pages;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.PasswordResetDAO;
import fi.internetix.edelphi.domainmodel.users.PasswordReset;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ResetPasswordPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String email = pageRequestContext.getString("email");
    String hash = pageRequestContext.getString("hash");
    PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
    PasswordReset passwordReset = passwordResetDAO.findByEmailAndHash(email, hash);
    if (passwordReset == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_PASSWORD_RESET_REQUEST, messages.getText(locale, "exception.1019.invalidPasswordReset"));
    }
    pageRequestContext.getRequest().setAttribute("email", email);
    pageRequestContext.getRequest().setAttribute("hash", hash);
    pageRequestContext.setIncludeJSP("/jsp/pages/resetpassword.jsp");
  }

}
