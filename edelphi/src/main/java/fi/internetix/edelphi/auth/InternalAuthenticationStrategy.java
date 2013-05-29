package fi.internetix.edelphi.auth;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.RequestContext;

public class InternalAuthenticationStrategy 
    extends AbstractAuthenticationStrategy 
    implements AuthenticationProvider {
  
  public static final String STRATEGY_NAME = "eDelfoi";
  
  public InternalAuthenticationStrategy() {
  }
  
  public String getName() {
    return STRATEGY_NAME;
  }
  
  public boolean requiresCredentials() {
    return true;
  }

  @Override
  public AuthenticationResult processLogin(RequestContext requestContext) {
    String username = StringUtils.lowerCase(requestContext.getString("username"));
    String password = RequestUtils.md5EncodeString(requestContext.getString("password"));
    
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();

    UserEmail userEmail = userEmailDAO.findByAddress(username);
    
    if (userEmail != null) {
      User user = userEmail.getUser();
      UserPassword userPassword = userPasswordDAO.findByUser(user);
      UserActivationDAO userActivationDAO = new UserActivationDAO();
      UserActivation userActivation = userActivationDAO.findByUser(user);
      
      if (userActivation == null && userPassword != null && password.equals(userPassword.getPasswordHash())) {
        RequestUtils.loginUser(requestContext, user);
        return AuthenticationResult.LOGIN;
      }
      else {
        Messages messages = Messages.getInstance();
        Locale locale = requestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
      }
    } else {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_LOGIN, messages.getText(locale, "exception.1007.invalidLogin"));
    }
  }

  @Override
  public String[] getKeys() {
    // Internal login needs no settings
    return null;
  }

  @Override
  public String localizeKey(Locale locale, String key) {
    // Internal login needs no settings
    return null;
  }

}
