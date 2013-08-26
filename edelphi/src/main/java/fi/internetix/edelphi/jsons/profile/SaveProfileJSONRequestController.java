package fi.internetix.edelphi.jsons.profile;

import java.util.List;
import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserSettingDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserSetting;
import fi.internetix.edelphi.domainmodel.users.UserSettingKey;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveProfileJSONRequestController extends JSONController {

  public SaveProfileJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    User user = userDAO.findById(jsonRequestContext.getLong("userId"));
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    // Verify that the logged user is the same as the user being edited.
    if (!loggedUser.getId().equals(user.getId())) {
      throw new AccessDeniedException(jsonRequestContext.getRequest().getLocale());
    }
    
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String nickname = jsonRequestContext.getString("nickname");
    String email = jsonRequestContext.getLowercaseString("email");
    
    // Update user's basic information
    
    userDAO.update(user, firstName, lastName, nickname, loggedUser);
    
    // Add, edit, or delete user e-mail address
    
    Long userEmailId = jsonRequestContext.getLong("emailId");
    UserEmail userEmail = userEmailId == null ? null : userEmailDAO.findById(userEmailId); 
    if (email == null && userEmail != null) {
      // No e-mail given, user has existing e-mail -> remove e-mail and if user has additional e-mails,
      // select the first of them as the new default e-mail. Essentially this never happens as e-mail
      // in the profile view is mandatory 
      userDAO.updateDefaultEmail(user, null, loggedUser);
      userEmailDAO.delete(userEmail);
      List<UserEmail> userEmails = userEmailDAO.listByUser(user);
      if (!userEmails.isEmpty()) {
        userDAO.updateDefaultEmail(user, userEmails.get(0), loggedUser);
      }
    }
    else if (email != null && userEmail != null && !email.equals(userEmail.getAddress())) {
      // E-mail entered but differs from user's current e-mail address. Possible scenarios:
      // A) entered e-mail belongs to someone else -> display an error message
      // B) entered e-mail is available -> update current e-mail address
      // C) entered e-mail belongs to user -> switch current e-mail address 
      UserEmail enteredEmail = userEmailDAO.findByAddress(email);
      if (enteredEmail == null) {
        // Scenario B
        userEmailDAO.updateAddress(userEmail, email);
      }
      else if (!user.getId().equals(enteredEmail.getUser().getId())) {
        // Scenario A
        throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_EMAIL, messages.getText(locale, "exception.1036.duplicateEmail"));
      }
      else {
        // Scenario C
        userDAO.updateDefaultEmail(user, enteredEmail, loggedUser);
      }
    }
    else if (email != null && userEmail == null) {
      // E-mail entered, user has no current e-mail. Possible scenarios:
      // A) entered e-mail belongs to someone else -> display an error message
      // B) entered e-mail is available -> created and set as current e-mail address
      UserEmail enteredEmail = userEmailDAO.findByAddress(email);
      if (enteredEmail != null) {
        // Scenario A
        throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_EMAIL, messages.getText(locale, "exception.1036.duplicateEmail"));
      }
      // Scenario B
      userEmail = userEmailDAO.create(user, email);
      userDAO.updateDefaultEmail(user, userEmail, loggedUser);
    }
    
    // Comment mail support
    
    Integer commentMail = jsonRequestContext.getInteger("commentMail");
    UserSettingDAO userSettingDAO = new UserSettingDAO();
    UserSetting userSetting = userSettingDAO.findByUserAndKey(loggedUser,  UserSettingKey.MAIL_COMMENT_REPLY);
    if (userSetting == null) {
      userSettingDAO.create(loggedUser, UserSettingKey.MAIL_COMMENT_REPLY, commentMail.toString());
    }
    else {
      userSettingDAO.updateValue(userSetting, commentMail.toString());
    }

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "profile.block.savedMessage"));
  }

}
