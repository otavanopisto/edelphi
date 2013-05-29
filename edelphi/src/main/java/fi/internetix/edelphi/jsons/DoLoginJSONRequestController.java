package fi.internetix.edelphi.jsons;

import fi.internetix.edelphi.auth.AuthenticationProvider;
import fi.internetix.edelphi.auth.AuthenticationProviderFactory;
import fi.internetix.edelphi.auth.AuthenticationResult;
import fi.internetix.edelphi.dao.base.AuthSourceDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.AuthSource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class DoLoginJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    // Authentication source and its parameters
    Long authSourceId = jsonRequestContext.getLong("authSource");
    if (authSourceId == null) {
      authSourceId = AuthUtils.retrieveAuthSourceId(jsonRequestContext);
    }
    else {
      AuthUtils.storeAuthSourceId(jsonRequestContext, authSourceId);
    }
    AuthSourceDAO authSourceDAO = new AuthSourceDAO();
    AuthSource authSource = authSourceDAO.findById(authSourceId);
    AuthenticationProvider authProvider = (AuthenticationProvider) AuthenticationProviderFactory.getInstance().createAuthenticationProvider(authSource);
    AuthenticationResult result = authProvider.processLogin(jsonRequestContext);
    if (result != AuthenticationResult.PROCESSING) {
      AuthUtils.addAuthenticationStrategy(jsonRequestContext, authProvider.getName());

      UserDAO userDAO = new UserDAO();
      User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());      
      
      String baseURL = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
      String redirectUrl = null;
      if (result == AuthenticationResult.NEW_ACCOUNT || (loggedUser != null && (loggedUser.getFirstName() == null || loggedUser.getLastName() == null || loggedUser.getDefaultEmail() == null))) {
        redirectUrl = baseURL + "/profile.page";
      }
      else {
        String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(jsonRequestContext);
        redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
      }
      
      jsonRequestContext.setRedirectURL(redirectUrl);
    }
  }
  
}
