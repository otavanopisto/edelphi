package fi.internetix.edelphi.jsons;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserActivationDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserIdentificationDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiDefaults;
import fi.internetix.edelphi.domainmodel.base.DelfoiUser;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserActivation;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.MailUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class RegisterJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    // Data Access Objects
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    // Registration fields
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String email = StringUtils.lowerCase(jsonRequestContext.getString("email"));
    String password = jsonRequestContext.getString("password");
    Boolean skipEmailVerification = jsonRequestContext.getBoolean("skipEmailVerification");
    // Check for e-mail in use
    UserEmail userEmail = userEmailDAO.findByAddress(email);
    UserPassword userPassword = userEmail == null ? null : userPasswordDAO.findByUser(userEmail.getUser()); 
    if (userEmail != null) {
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(userEmail.getUser());
      if (userPassword != null || !userIdentifications.isEmpty()) {
        // Refuse registration as user with at least some sort of login capability already exists 
        Messages messages = Messages.getInstance();
        Locale locale = jsonRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.REGISTRATION_EMAIL_EXISTS, messages.getText(locale, "exception.1009.registerEmailInUse"));
      }
    }
    User user = null;
    if (userEmail == null) {
      user = userDAO.create(firstName, lastName, null,  null);
    }
    else {
      user = userDAO.update(userEmail.getUser(), firstName, lastName, null, userEmail.getUser());
    }
    // Email
    if (userEmail == null) {
      userEmail = userEmailDAO.create(user, email);
      userDAO.addUserEmail(user, userEmail, true, user);
    }
    // Password
    if (userPassword == null) {
      userPassword = userPasswordDAO.create(user, password);
    }
    // Delfoi role
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    DelfoiDefaults delfoiDefaults = RequestUtils.getDefaults(jsonRequestContext);
    DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(delfoi, user);
    if (delfoiUser == null) {
      delfoiUserDAO.create(delfoi, user, delfoiDefaults.getDefaultDelfoiUserRole(), user);
    }
    if (skipEmailVerification) {
      RequestUtils.loginUser(jsonRequestContext, user);
      String baseURL = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
      String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(jsonRequestContext);
      String redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
      jsonRequestContext.setRedirectURL(redirectUrl);
    }
    else {
      // E-mail verification
      UserActivationDAO userActivationDAO = new UserActivationDAO();
      UserActivation userActivation = userActivationDAO.create(user, email, UUID.randomUUID().toString());
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      String mailSubject = messages.getText(locale, "userRegistration.mailSubject");
      String verificationLink = RequestUtils.getBaseUrl(jsonRequestContext.getRequest()) + "/activateaccount.page?email=" + userActivation.getEmail() + "&hash=" + userActivation.getHash();
      String mailContent = messages.getText(locale, "userRegistration.mailTemplate", new String [] { email, verificationLink });
      String infoMessage = messages.getText(locale, "userRegistration.infoMessage", new String [] { email });
      MailUtils.sendMail(locale, email, mailSubject, mailContent);
      jsonRequestContext.addMessage(Severity.INFORMATION, infoMessage);
    }
  }
  
}
