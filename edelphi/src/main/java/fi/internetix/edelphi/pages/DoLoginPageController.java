package fi.internetix.edelphi.pages;

import java.util.Locale;

import fi.internetix.edelphi.auth.AuthenticationProvider;
import fi.internetix.edelphi.auth.AuthenticationProviderFactory;
import fi.internetix.edelphi.auth.AuthenticationResult;
import fi.internetix.edelphi.dao.base.AuthSourceDAO;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.AuthSource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcMessage;
import fi.internetix.smvc.controllers.PageRequestContext;

public class DoLoginPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    // Authentication source and its parameters
    Long authSourceId = pageRequestContext.getLong("authSource");
    if (authSourceId == null) {
      authSourceId = AuthUtils.retrieveAuthSourceId(pageRequestContext);
    }
    else {
      AuthUtils.storeAuthSourceId(pageRequestContext, authSourceId);
    }
    if (authSourceId != null) {
      AuthSourceDAO authSourceDAO = new AuthSourceDAO();
      AuthSource authSource = authSourceDAO.findById(authSourceId);
      AuthenticationProvider authProvider = (AuthenticationProvider) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(authSource);
      AuthenticationResult result = authProvider.processLogin(pageRequestContext);
      if (result != AuthenticationResult.PROCESSING) {
        AuthUtils.addAuthenticationStrategy(pageRequestContext, authProvider.getName());
        
        String redirectUrl = null;
        String baseURL = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
        
        UserDAO userDAO = new UserDAO();
        User loggedUser = userDAO.findById(pageRequestContext.getLoggedUserId());      

        if (result == AuthenticationResult.NEW_ACCOUNT || (loggedUser != null && (loggedUser.getFirstName() == null || loggedUser.getLastName() == null || loggedUser.getDefaultEmail() == null))) {
          redirectUrl = baseURL + "/profile.page";
        }
        else {

          // Delete a possible user activation request due to a successful login 
          
          if (loggedUser != null) {
            UserActivationDAO userActivationDAO = new UserActivationDAO();
            UserActivation userActivation = userActivationDAO.findByUser(loggedUser);
            if (userActivation != null) {
              userActivationDAO.delete(userActivation);
              Messages messages = Messages.getInstance();
              Locale locale = pageRequestContext.getRequest().getLocale();
              SmvcMessage message = new SmvcMessage(Severity.INFORMATION, messages.getText(locale, "index.block.accountActivated"));
              RequestUtils.storeRedirectMessage(pageRequestContext, message);
            }
          }

          // Redirect to wherever we were going in the first place
          
          String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(pageRequestContext);
          redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
        }
        
        pageRequestContext.setRedirectURL(redirectUrl);
      }
    }
    else {
      pageRequestContext.setRedirectURL(RequestUtils.getBaseUrl(pageRequestContext.getRequest()) + "/index.page");
    }
  }

}
