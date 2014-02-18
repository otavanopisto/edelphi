package fi.internetix.edelphi.pages;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcMessage;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ActivateAccountPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String email = pageRequestContext.getString("email");
    String hash = pageRequestContext.getString("hash");
    UserActivationDAO userActivationDAO = new UserActivationDAO();
    UserActivation userActivation = userActivationDAO.findByEmailAndHash(email, hash);
    if (userActivation == null) {
      Messages messages = Messages.getInstance();
      Locale locale = pageRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_ACCOUNT_ACTIVATION, messages.getText(locale, "exception.1015.invalidAccountActivation"));
    }
    userActivationDAO.delete(userActivation);
    User user = userActivation.getUser();
    RequestUtils.loginUser(pageRequestContext, user);

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    SmvcMessage message = new SmvcMessage(Severity.INFORMATION, messages.getText(locale, "index.block.accountActivated"));
    RequestUtils.storeRedirectMessage(pageRequestContext, message);
    
    String baseUrl = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
    String redirectUrl = baseUrl + "/index.page";
    pageRequestContext.setRedirectURL(redirectUrl);
  }

}
